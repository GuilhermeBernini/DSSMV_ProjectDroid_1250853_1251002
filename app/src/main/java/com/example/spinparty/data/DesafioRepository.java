package com.example.spinparty.data;

import com.example.spinparty.model.Desafio;

import java.util.ArrayList;
import java.util.List;

public class DesafioRepository {

    public List<Desafio> getDesafios(String tipo) {
        // todas as perguntas (por agora fixas, depois vêm do servidor)
        List<Desafio> todos = new ArrayList<>();
        todos.add(new Desafio("Diz 3 capitais europeias", "", "batata_quente"));
        todos.add(new Desafio("Diz 3 marcas de carros", "", "batata_quente"));
        todos.add(new Desafio("Diz uma palavra que rime com 'batata'", "", "batata_quente"));
        todos.add(new Desafio("Diz 3 licenciaturas no ISEP", "", "batata_quente"));
        todos.add(new Desafio("Diz qual é que é o animal mais alto do mundo", "girafa", "batata_quente"));

        // ficar só com as do jogo pedido
        List<Desafio> resultado = new ArrayList<>();
        for (Desafio d : todos) {
            if (d.getMinijogo().equals(tipo)) {
                resultado.add(d);
            }
        }
        return resultado;
    }
}