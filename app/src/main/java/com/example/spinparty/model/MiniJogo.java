package com.example.spinparty.model;

public class MiniJogo {
    private String nome;
    private String descricao;
    private String tipo;

    public MiniJogo(String tipo, String descricao, String nome) {
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getTipo() {
        return tipo;
    }

}