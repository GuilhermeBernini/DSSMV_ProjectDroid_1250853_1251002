package com.example.spinparty.data;

import android.os.Handler;
import android.os.Looper;

import com.example.spinparty.BuildConfig;
import com.example.spinparty.model.Jogador;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JogadorRepository {

    /** Resultado de um pedido com UM jogador; os dois metodos sao chamados na thread principal. */
    public interface Callback {
        void onSucesso(Jogador jogador);

        void onErro(String mensagem);
    }

    /** Resultado de um pedido com uma LISTA de jogadores (usado no ranking). */
    public interface ListaCallback {
        void onSucesso(List<Jogador> jogadores);

        void onErro(String mensagem);
    }

    private final RestDbClient client =
            new RestDbClient(BuildConfig.RESTDB_BASE_URL, BuildConfig.RESTDB_API_KEY);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /** Cria o jogador no restdb.io (POST /jogadores) com pontuacao inicial 0. */
    public void criarJogador(String nome, Callback callback) {
        if (!client.isConfigured()) {
            callback.onErro("Falta configurar RESTDB_BASE_URL e RESTDB_API_KEY");
            return;
        }
        executor.execute(() -> {
            try {
                JSONObject corpo = new JSONObject();
                corpo.put("nome", nome);
                corpo.put("pontuacao", 0);

                String resposta = client.post("/jogadores", corpo.toString());
                JSONObject obj = new JSONObject(resposta);
                Jogador jogador = new Jogador(obj.getString("_id"), nome, 0);

                mainHandler.post(() -> callback.onSucesso(jogador));
            } catch (IOException | JSONException e) {
                mainHandler.post(() -> callback.onErro("Erro ao criar jogador: " + e.getMessage()));
            }
        });
    }

    /** Atualiza a pontuacao do jogador no restdb.io (PUT /jogadores/{id}). */
    public void atualizarPontuacao(String id, int pontos, Callback callback) {
        if (!client.isConfigured()) {
            callback.onErro("Falta configurar RESTDB_BASE_URL e RESTDB_API_KEY");
            return;
        }
        executor.execute(() -> {
            try {
                JSONObject corpo = new JSONObject();
                corpo.put("pontuacao", pontos);

                String resposta = client.put("/jogadores/" + id, corpo.toString());
                JSONObject obj = new JSONObject(resposta);
                String nome = obj.optString("nome", "");
                Jogador jogador = new Jogador(id, nome, pontos);

                mainHandler.post(() -> callback.onSucesso(jogador));
            } catch (IOException | JSONException e) {
                mainHandler.post(() -> callback.onErro("Erro ao atualizar pontuacao: " + e.getMessage()));
            }
        });
    }

    /** Pede o ranking ao restdb.io (GET /jogadores?sort=pontuacao&dir=-1&max=10). */
    public void carregarRanking(ListaCallback callback) {
        if (!client.isConfigured()) {
            callback.onErro("Falta configurar RESTDB_BASE_URL e RESTDB_API_KEY");
            return;
        }
        executor.execute(() -> {
            try {
                String json = client.get("/jogadores?sort=pontuacao&dir=-1&max=10");
                JSONArray array = new JSONArray(json);
                List<Jogador> lista = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String id = obj.optString("_id", "");
                    String nome = obj.optString("nome", "").trim();
                    int pontuacao = obj.optInt("pontuacao", 0);
                    if (nome.isEmpty()) {
                        continue;
                    }
                    lista.add(new Jogador(id, nome, pontuacao));
                }
                mainHandler.post(() -> callback.onSucesso(lista));
            } catch (IOException | JSONException e) {
                mainHandler.post(() -> callback.onErro("Erro ao obter ranking: " + e.getMessage()));
            }
        });
    }

    /** Para a thread de rede; chamar quando o ecra deixa de existir. */
    public void fechar() {
        executor.shutdownNow();
    }
}