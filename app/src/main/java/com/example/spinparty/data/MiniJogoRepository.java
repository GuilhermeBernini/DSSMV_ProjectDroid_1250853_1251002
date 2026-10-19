package com.example.spinparty.data;

import com.example.spinparty.model.MiniJogo;

import java.util.ArrayList;
import java.util.List;

public class MiniJogoRepository {

    public List<MiniJogo> getMiniJogos() {
        List<MiniJogo> lista = new ArrayList<>();
        lista.add(new MiniJogo("reflexos", "Pega no telemóvel quando o ecrã ficar verde", "Reflexos"));
        lista.add(new MiniJogo("batata_quente", "o telemóvel passa de mão em mão até explodir", "Batata Quente"));
        lista.add(new MiniJogo("contar_vibracoes", "o telemóvel vibra N vezes e os jogadores adivinham o número", "Contar Vibrações"));
        return lista;
    }
}