package br.com.mvbos.lgj;

public class Jogador implements Comparable<Jogador>{
    private String Nome;
    private int Pontuacao;

    public String getNome() {
        return this.Nome;

    }

    public void setNome(String nome) {
        this.Nome = nome;

    }

    public int getPontuacao() {
        return this.Pontuacao;

    }

    public void setPontuacao(int pontuacao) {
        this.Pontuacao = pontuacao;

    }

    @Override
    public int compareTo(Jogador Rank) {

        return Rank.getPontuacao() - Pontuacao;

    }


}
