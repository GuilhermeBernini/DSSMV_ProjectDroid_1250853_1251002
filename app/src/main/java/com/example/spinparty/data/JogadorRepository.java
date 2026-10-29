package com.example.spinparty.data;

import android.os.Handler;
import android.os.Looper;

import com.example.spinparty.BuildConfig;
import com.example.spinparty.model.Jogador;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JogadorRepository {
    
    /** Aqui temos o resultado do pedido a API e os dois metodos sao chamados na thread principal. */
    public interface Callback {
        void onSucesso(Jogador jogador);

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
    /** Para a thread de rede; chamar quando o ecra deixa de existir. */
    public void fechar() {
        executor.shutdownNow();
    }

}
