# Missao Marte Unifor

Repositorio do jogo em console desenvolvido em Java para a disciplina de Programacao Orientada a Objetos.

## Repositorio

https://github.com/Aranhadaniela/oo-console

## Participante

| Matricula | Participante | GitHub |
| --- | --- | --- |
| 2410902 | Daniela Aranha Goes |Aranhadaniela |
| 2417366 | Jonathas Dantas Limeira| jonathasdlimeira |

## Sobre o projeto

O jogo coloca o jogador no controle de uma nave em um mapa bidimensional. O objetivo e resgatar os passageiros, evitar asteroides e terminar a missao com a maior pontuacao possivel.

## Como executar

Abra o terminal na raiz do projeto e execute os comandos abaixo:

1. Compile os arquivos Java:

```bash
javac -d out $(find src -name "*.java")
```

2. Execute o jogo:

```bash
java -cp out missao.Main
```


## Estrutura

```text
src/missao/
├── Asteroide.java
├── Engenheiro.java
├── Main.java
├── Missao.java
├── Nave.java
├── Passageiro.java
└── Professor.java
```

## Observacoes

- O arquivo `ranking.json` e criado automaticamente ao salvar o ranking.
- O diretorio `docs/` contem a documentacao gerada do projeto.