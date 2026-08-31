package missao;

public class Astronauta extends Passageiro {
    public Astronauta(String nome, int x, int y) {
        super(nome, "Professor", x, y);
    }
    @Override
    public int getPontuacao(){
        return 20;
    }
}
