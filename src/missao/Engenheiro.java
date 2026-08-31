package missao;

/**
 * Passageiro especializado: Engenheiro. Subclasse de `Passageiro` que define o
 * tipo apropriado.
 */
public class Engenheiro extends Passageiro {
    public Engenheiro(String nome, int x, int y) {
        super(nome, "Engenheiro", x, y);
    }
      @Override
    public int getPontuacao(){
        return 15;
    }
}
