package missao;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a nave do jogador, responsável pela posição e pelos passageiros
 * embarcados.
 */
public class Nave {
    private String id;
    private int x;
    private int y;
    private int capacidade;
    private List<Passageiro> passageiros = new ArrayList<>();
    private int vidas;

    /**
     * Cria uma nova nave com identificador e capacidade.
     *
     * @param id identificador da nave
     * @param capacidade número máximo de passageiros que podem ser embarcados
     */
    public Nave(String id, int capacidade) {
        this.id = id;
        this.capacidade = capacidade;
        this.x = 0;
        this.y = 0;
        this.vidas =3;
    }

    public String getId() { return id; }

    public int getX() { return x; }

    public int getY() { return y; }

    public int getCapacidade() { return capacidade; }

    public List<Passageiro> getPassageiros() { return passageiros; }

    public int getVidas() { return vidas; }

    public void perderVida() {
        vidas--;
    }

    public boolean estaViva() {
        return vidas > 0;
    }

    /** Move a nave uma posição para cima (y--). */
    public void moveUp() { y--; }

    /** Move a nave uma posição para baixo (y++). */
    public void moveDown() { y++; }

    /** Move a nave uma posição para a esquerda (x--). */
    public void moveLeft() { x--; }

    /** Move a nave uma posição para a direita (x++). */
    public void moveRight() { x++; }

    /**
     * Tenta embarcar um passageiro na nave.
     *
     * @param p passageiro a embarcar
     * @return true se houve espaço e o embarque foi bem-sucedido
     */
    public boolean embarcar(Passageiro p) {
        // Verifica se há espaço disponível e adiciona o passageiro à lista
        if (passageiros.size() < capacidade) {
            passageiros.add(p);
            return true;
        }
        return false;
    }
}
