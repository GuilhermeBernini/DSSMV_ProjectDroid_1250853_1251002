package com.example.spinparty.model;

import java.util.ArrayList;
import java.util.List;

public class Partida {

    // só existe UMA partida a decorrer; todos os ecrãs vão buscá-la aqui
    private static Partida atual = new Partida();

    private final List<String> jogadores = new ArrayList<>();    // nomes, por ordem
    private final List<Integer> pontos = new ArrayList<>();      // pontos[i] = pontos do jogador i
    private final List<String> jogosJogados = new ArrayList<>(); // tipos já jogados

    public static Partida getAtual() {             // dá a partida (o "caderno") que está a decorrer; todos os ecrãs usam esta
        return atual;
    }

    public static void novaPartida() {              // cria uma partida nova e vazia: apaga nomes, pontos e jogos jogados (VOLTAR AO INÍCIO)
        atual = new Partida();
    }

    // ===== JOGADORES =====
    public void adicionarJogador(String nome) {    // junta um jogador ao fim da lista, com 0 pontos (botão ADICIONAR)
        jogadores.add(nome);
        pontos.add(0);                              // cada jogador começa com 0 pontos
    }

    public List<String> getJogadores() {           // dá a lista dos nomes, por ordem (jogador 0, 1, 2...)
        return jogadores;
    }

    // ===== PONTOS =====
    public int getPontos(int jogador) {            // dá os pontos de UM jogador: getPontos(2) -> 5
        return pontos.get(jogador);
    }

    public void darPontos(int jogador, int quantos) { // soma pontos a UM jogador: darPontos(1, 3) -> o jogador 1 ganha 3
        pontos.set(jogador, pontos.get(jogador) + quantos);
    }

    // ===== JOGOS =====
    public void marcarJogado(String tipo) {        // escreve o tipo na lista jogosJogados (quando o jogo abre)
        jogosJogados.add(tipo);
    }

    public boolean jaJogou(String tipo) {          // sim/não: este jogo já está na lista? O Spin usa para não o sortear outra vez
        return jogosJogados.contains(tipo);
    }

    public int getNumeroJogosJogados() {           // quantos jogos já se jogaram (um número); quando chega a 3, acabou
        return jogosJogados.size();
    }
}