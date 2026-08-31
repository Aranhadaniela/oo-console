# Missao Marte Unifor

Repositorio do jogo em console desenvolvido em Java para a disciplina de Programacao Orientada a Objetos.

## Repositorio

https://github.com/Aranhadaniela/oo-console

## Participante

| Matricula | Participante |
| --- | --- |
| 2410902 | Daniela Aranha Goes |

## Sobre o projeto

O jogo coloca o jogador no controle de uma nave em um mapa bidimensional. O objetivo e resgatar os passageiros, evitar asteroides e terminar a missao com a maior pontuacao possivel.

## Como executar

Compile e rode o projeto a partir da raiz do repositório:

```bash
javac -d out $(find src -name "*.java")
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