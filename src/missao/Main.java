package missao;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Aplicação principal do jogo "Missão Marte Unifor" em modo console.
 */
public class Main {

    public static void main(String[] args) {
        Random random = new Random();

        // Caminho para o arquivo que persiste o ranking de pontuações
        Path rankingPath = Paths.get("ranking.json");
        List<RankingEntry> ranking = loadRanking(rankingPath);

        Scanner scanner = new Scanner(System.in);

        // --- PASSO 2: MENU PRINCIPAL E RESET DO RANKING ---
        boolean rodandoMenu = true;
        while (rodandoMenu) {
            System.out.println("================================================================");
            System.out.println("Missão Marte Unifor — Menu Principal");
            System.out.println("1. Iniciar Nova Missão");
            System.out.println("2. Ver Ranking");
            System.out.println("3. Resetar Ranking");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");
            
            String opcao = scanner.nextLine().trim();
            switch (opcao) {
                case "1":
                    rodandoMenu = false; // Sai do menu e começa o jogo
                    break;
                case "2":
                    System.out.println("\n--- Ranking Atual ---");
                    if (ranking.isEmpty()) {
                        System.out.println("Ainda não há pontuações registradas.");
                    } else {
                        printRanking(ranking);
                    }
                    System.out.println("\nPressione Enter para continuar...");
                    scanner.nextLine();
                    break;
                case "3":
                    try {
                        Files.deleteIfExists(rankingPath);
                        ranking.clear();
                        System.out.println("\n[Sucesso] O arquivo de ranking foi resetado!");
                    } catch (IOException e) {
                        System.out.println("\n[Erro] Não foi possível resetar o ranking: " + e.getMessage());
                    }
                    System.out.println("Pressione Enter para continuar...");
                    scanner.nextLine();
                    break;
                case "4":
                    System.out.println("Encerrando o programa. Até mais!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        }

        System.out.print("Digite o nome do piloto: ");
        String pilotoNome = scanner.nextLine().trim();
        if (pilotoNome.isEmpty()) {
            pilotoNome = "Piloto Anônimo";
        }

        int dimensaoMapa = lerDimensaoMapa(scanner);
        int maxX = dimensaoMapa / 2;
        int minX = -maxX;
        int maxY = dimensaoMapa / 2;
        int minY = -maxY;

        // Cabeçalho e instruções iniciais do jogo
        System.out.println("================================================================");
        System.out.println("Missão Marte Unifor — Console");
        System.out.println();
        System.out.println("Bem-vindo à Missão Marte Unifor! Sua nave foi selecionada para uma expedição de resgate.");
        System.out.println("Objetivo: Resgatar todos os passageiros E navegar até a Plataforma (0,0).");
        System.out.println();
        System.out.println("Comandos: w (cima), s (baixo), a (esquerda), d (direita), c (embarcar), q (sair)");
        System.out.println();
        System.out.println("Pressione Enter para iniciar a missão...");
        scanner.nextLine();
        System.out.println("================================================================");

        // Loop externo: permite jogar várias missões até o usuário optar por sair
        boolean playAgain = true;
        while (playAgain) {
            Missao missao = criarNovaMissao(random, minX, maxX, minY, maxY);
            Nave nave = missao.getNave();
            int score = 20;
            char ultimoMovimento = '\0';
            boolean running = true;
            
            while (running) {
                desenharMapa(missao, minX, maxX, minY, maxY, score, pilotoNome);
                System.out.printf("Nave em (%d,%d) | Pontos: %d | Vidas: %d | Passageiros a bordo: %d | Passageiros restantes: %d\n",
                        nave.getX(), nave.getY(), score, nave.getVidas(), nave.getPassageiros().size(), missao.todosEmbarcados() ? 0 : missao.getPassageiros().size());

                if (missao.verificaColisao()) {
                    nave.perderVida();
                    System.out.printf("Colisão com asteroide! Vidas restantes: %d%n", nave.getVidas());
                    if (!nave.estaViva()) {
                        System.out.println("Sem vidas restantes. Missão abortada.");
                        break;
                    }

                    desfazerUltimoMovimento(nave, ultimoMovimento);
                    System.out.printf("A nave foi reposicionada para (%d,%d).%n", nave.getX(), nave.getY());
                    continue;
                }

                System.out.print("Para onde ir? ");
                String line = scanner.nextLine().trim().toLowerCase();
                if (line.isEmpty()) continue;
                char cmd = line.charAt(0);
                switch (cmd) {
                    case 'w': nave.moveUp(); score--; ultimoMovimento = cmd; break;
                    case 's': nave.moveDown(); score--; ultimoMovimento = cmd; break;
                    case 'a': nave.moveLeft(); score--; ultimoMovimento = cmd; break;
                    case 'd': nave.moveRight(); score--; ultimoMovimento = cmd; break;
                    case 'c': {
                        Passageiro p = missao.passagemNaPosicao();
                        if (p == null) {
                            System.out.println("Nenhum passageiro nesta posição.");
                        } else {
                            boolean ok = missao.embarcarPassageiroNaPosicao();
                            if (ok) {
                                score += p.getPontuacao();
                                System.out.println("Passageiro embarcado. "+p.getPontuacao()+" pontos!");
                            } else {
                                System.out.println("Nave cheia, não foi possível embarcar.");
                            }
                        }
                        break;
                    }
                    case 'q': running = false; break;
                    default: System.out.println("Comando desconhecido.");
                }

                if (score <= 0) {
                    System.out.println("Pontuação zerada. Missão perdida.");
                    break;
                }

                // --- PASSO 1: CONDIÇÃO DE VITÓRIA (Passageiros + Posição 0,0) ---
                if (missao.todosEmbarcados() && nave.getX() == 0 && nave.getY() == 0) {
                    System.out.println("Todos os passageiros embarcados e nave na Plataforma (0,0)! Missão concluída com sucesso.");
                    System.out.printf("Pontuação final: %d\n", score);
                    if (score > 0 && isTopScore(ranking, score)) {
                        ranking.add(new RankingEntry(pilotoNome, score));
                        ranking = ranking.stream()
                                .sorted(Comparator.comparingInt((RankingEntry e) -> e.score).reversed())
                                .limit(5)
                                .collect(Collectors.toList());
                        saveRanking(rankingPath, ranking);
                        System.out.println("Novo ranking salvo! Você está entre os 5 maiores pontuadores.");
                    }
                    break;
                } else if (missao.todosEmbarcados()) {
                    System.out.println("-> Todos os passageiros a bordo! Navegue até a coordenada (0,0) para pousar.");
                }
            }

            if (!ranking.isEmpty()) {
                System.out.println();
                System.out.println("Ranking Top 5:");
                printRanking(ranking);
            } else {
                System.out.println();
                System.out.println("Ranking vazio. Seja o primeiro a marcar pontos!");
            }

            System.out.print("Deseja iniciar nova missão? (s/n): ");
            String resposta = scanner.nextLine().trim().toLowerCase();
            if (resposta.equals("s") || resposta.equals("sim")) {
                System.out.println("Preparando nova missão...");
            } else {
                playAgain = false;
            }
        }

        scanner.close();
        System.out.println("Fim da execução.");
    }

    private static void printRanking(List<RankingEntry> ranking) {
        int position = 1;
        for (RankingEntry entry : ranking) {
            System.out.printf("%d. %s - %d pontos%n", position++, entry.name, entry.score);
        }
    }

    private static int lerDimensaoMapa(Scanner scanner) {
        System.out.print("Informe a dimensão do mapa (lado ímpar, ex.: 11): ");
        String entrada = scanner.nextLine().trim();
        try {
            int dimensao = Integer.parseInt(entrada);
            if (dimensao < 5 || dimensao > 21) {
                System.out.println("Valor fora do intervalo [5, 21]. Usando dimensão padrão 11.");
                return 11;
            }
            if (dimensao % 2 == 0) {
                dimensao++;
                System.out.println("Dimensão par ajustada para " + dimensao + ".");
            }
            return dimensao;
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Usando dimensão padrão 11.");
            return 11;
        }
    }

    private static Missao criarNovaMissao(Random random, int minX, int maxX, int minY, int maxY) {
        Nave nave = new Nave("A-1", 4);
        Missao missao = new Missao(nave);

        while (missao.getPassageiros().size() < 4) {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = random.nextInt(maxY - minY + 1) + minY;
            if (x == nave.getX() && y == nave.getY()) continue;
            if (posicaoOcupada(missao, x, y)) continue;
            if (missao.getPassageiros().isEmpty()) {
                missao.addPassageiro(new Professor("Dr. Silva", x, y));
            } else if (missao.getPassageiros().size() == 1) {
                missao.addPassageiro(new Engenheiro("Eng. Rosa", x, y));
            } else {
                missao.addPassageiro(new Professor("Dr. Lima", x, y));
            }
        }

        while (missao.getAsteroides().size() < 2) {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = random.nextInt(maxY - minY + 1) + minY;
            if (x == nave.getX() && y == nave.getY()) continue;
            if (posicaoOcupada(missao, x, y)) continue;
            missao.addAsteroide(new Asteroide(x, y));
        }

        return missao;
    }

    private static boolean posicaoOcupada(Missao missao, int x, int y) {
        if (missao.getNave().getX() == x && missao.getNave().getY() == y) return true;
        for (Passageiro p : missao.getPassageiros()) {
            if (p.getX() == x && p.getY() == y) return true;
        }
        for (Asteroide a : missao.getAsteroides()) {
            if (a.getX() == x && a.getY() == y) return true;
        }
        return false;
    }

    private static void desfazerUltimoMovimento(Nave nave, char ultimoMovimento) {
        switch (ultimoMovimento) {
            case 'w': nave.moveDown(); break;
            case 's': nave.moveUp(); break;
            case 'a': nave.moveRight(); break;
            case 'd': nave.moveLeft(); break;
            default:
                nave.moveRight();
                nave.moveDown();
                break;
        }
    }

    private static void desenharMapa(Missao missao, int minX, int maxX, int minY, int maxY, int score, String pilotoNome) {
        System.out.println();
        System.out.printf("Mapa da Missão (Pontos: %d | Vidas: %d) - Piloto: %s%n", score, missao.getNave().getVidas(), pilotoNome);
        System.out.print("    ");
        for (int x = minX; x <= maxX; x++) {
            System.out.printf(" %2d", x);
        }
        System.out.println();
        System.out.print("    ");
        for (int x = minX; x <= maxX; x++) {
            System.out.print(" __");
        }
        System.out.println();

        for (int y = minY; y <= maxY; y++) {
            System.out.printf("%3d|", y);
            for (int x = minX; x <= maxX; x++) {
                String symbol = "░";
                if (missao.getNave().getX() == x && missao.getNave().getY() == y) {
                    symbol = "🚀";
                } else {
                    for (Passageiro p : missao.getPassageiros()) {
                        if (p.getX() == x && p.getY() == y) {
                            if (p instanceof Engenheiro) {
                                symbol = "👨";
                            } else {
                                symbol = "👨‍🏫";
                            }
                            break;
                        }
                    }
                    if (symbol == "░") {
                        for (Asteroide a : missao.getAsteroides()) {
                            if (a.getX() == x && a.getY() == y) {
                                symbol = "💥";
                                break;
                            }
                        }
                    }
                }
                System.out.printf(" %2s", symbol);
            }
            System.out.println();
        }

        System.out.println("Legenda: 🚀=Nave, 👨‍🏫=Professor, 👨=Engenheiro, 💥=Asteroide, ░=Vazio");
        System.out.println("Passageiros restantes:");
        for (Passageiro p : missao.getPassageiros()) {
            System.out.printf(" - %s (%s) em (%d,%d)\n", p.getNome(), p.getTipo(), p.getX(), p.getY());
        }
        System.out.println();
    }

    private static boolean isTopScore(List<RankingEntry> ranking, int score) {
        if (ranking.size() < 5) {
            return true;
        }
        return score > ranking.get(ranking.size() - 1).score;
    }

    private static List<RankingEntry> loadRanking(Path path) {
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim();
            return parseRankingJson(json);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private static void saveRanking(Path path, List<RankingEntry> ranking) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < ranking.size(); i++) {
            RankingEntry entry = ranking.get(i);
            builder.append("{\"name\":\"")
                    .append(entry.name.replace("\"", "\\\""))
                    .append("\",\"score\":")
                    .append(entry.score)
                    .append("}");
            if (i < ranking.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        try {
            Files.write(path, builder.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Não foi possível salvar o ranking: " + e.getMessage());
        }
    }

    private static List<RankingEntry> parseRankingJson(String json) {
        List<RankingEntry> ranking = new ArrayList<>();
        if (json.isEmpty() || json.equals("[]")) {
            return ranking;
        }
        json = json.trim();
        if (json.startsWith("[")) {
            json = json.substring(1);
        }
        if (json.endsWith("]")) {
            json = json.substring(0, json.length() - 1);
        }

        int index = 0;
        while (index < json.length()) {
            int start = json.indexOf('{', index);
            if (start < 0) break;
            int end = json.indexOf('}', start);
            if (end < 0) break;
            String object = json.substring(start + 1, end);
            String name = null;
            Integer score = null;
            for (String part : object.split(",")) {
                String[] pair = part.split(":", 2);
                if (pair.length != 2) continue;
                String key = pair[0].trim().replaceAll("\"", "");
                String value = pair[1].trim();
                if (key.equals("name")) {
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        name = value.substring(1, value.length() - 1).replace("\\\"", "\"");
                    }
                } else if (key.equals("score")) {
                    try {
                        score = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) {}
                }
            }
            if (name != null && score != null) {
                ranking.add(new RankingEntry(name, score));
            }
            index = end + 1;
        }

        ranking.sort(Comparator.comparingInt((RankingEntry e) -> e.score).reversed());
        return ranking;
    }

    private static class RankingEntry {
        private final String name;
        private final int score;

        private RankingEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}
