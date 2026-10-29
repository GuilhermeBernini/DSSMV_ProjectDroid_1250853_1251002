package com.example.spinparty.model;

public class Jogador {
    private final String id;
    private final String nome;
    private final int pontuacao;

    public Jogador(String id, String nome, int pontuacao) {
        this.id = id;
        this.nome = nome;
        this.pontuacao = pontuacao;
    }

    public String getId() {
        return id;
    }
    
    public String getNome() {
        return nome;
    }

    public int getPontuacao() {
        return pontuacao;
    }

}
