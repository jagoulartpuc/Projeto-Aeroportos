package pucrs.myflight.modelo;

public class Pais {
    private String nome;
    private String sigla;

    public Pais(String sigla, String nome) {
        this.nome = nome;
        this.sigla = sigla;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String toString() {
        return "Sigla: "+sigla+" , "+" Nome: "+nome;
    }
}
