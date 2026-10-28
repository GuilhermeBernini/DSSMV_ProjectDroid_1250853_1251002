package com.example.spinparty.model;

public class Desafio {
    private String texto;      // a pergunta
    private String resposta;   // a resposta (pode ficar vazia)
    private String minijogo;   // o tipo do jogo, ex: "batata_quente"

    public String getTexto() {
        return texto;
    }

    public String getResposta() {
        return resposta;
    }

    public String getMinijogo() {
        return minijogo;
    }

    public Desafio(String texto, String resposta, String minijogo) {
        this.texto = texto;
        this.resposta = resposta;
        this.minijogo = minijogo;

    }
}
