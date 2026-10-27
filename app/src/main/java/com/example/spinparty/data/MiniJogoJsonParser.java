package com.example.spinparty.data;

import com.example.spinparty.model.MiniJogo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Vai converter a resposta JSON da colecao "minijogos" do restdb.io numa lista de
 * MiniJogo. Os objetos sem nome sao ignorados.
 */

public class MiniJogoJsonParser {
     private MiniJogoJsonParser() {
    }

    public static List<MiniJogo> parse(String json) throws JSONException {
        List<MiniJogo> lista = new ArrayList<>();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String nome = obj.optString("nome", "").trim();
            if (nome.isEmpty()) {
                continue;
            }
            String tipo = obj.optString("tipo", "").trim();
            String descricao = obj.optString("descricao", "").trim();
            lista.add(new MiniJogo(tipo, descricao, nome));
        }
        return lista;
    }
}
