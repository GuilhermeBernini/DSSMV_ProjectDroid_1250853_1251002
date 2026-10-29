package com.example.spinparty.data;

import android.os.Handler;
import android.os.Looper;

import com.example.spinparty.BuildConfig;
import com.example.spinparty.model.MiniJogo;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MiniJogoRepository {
    
     /** Resultado do pedido a API; os dois metodos sao chamados na thread principal. */
    public interface Callback {
        void onSucesso(List<MiniJogo> miniJogos);

        void onErro(String mensagem);
    }

    private final RestDbClient client =
            new RestDbClient(BuildConfig.RESTDB_BASE_URL, BuildConfig.RESTDB_API_KEY);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

     /** Lista local, usada quando a API nao esta configurada ou falha. */
    public List<MiniJogo> getMiniJogos() {
        List<MiniJogo> lista = new ArrayList<>();
        lista.add(new MiniJogo("reflexos", "Pega no telemóvel quando o ecrã ficar verde", "Reflexos"));
        lista.add(new MiniJogo("batata_quente", "o telemóvel passa de mão em mão até explodir", "Batata Quente"));
        lista.add(new MiniJogo("contar_vibracoes", "o telemóvel vibra N vezes e os jogadores adivinham o número", "Contar Vibrações"));
        return lista;
    }
    
     /** Pede os mini-jogos ao restdb.io numa thread a parte (GET /minijogos). */
    public void carregarMiniJogos(Callback callback) {
        if (!client.isConfigured()) {
            callback.onErro("Falta configurar RESTDB_BASE_URL e RESTDB_API_KEY");
            return;
        }
        executor.execute(() -> {
            try {
                String json = client.get("/minijogos");
                List<MiniJogo> lista = MiniJogoJsonParser.parse(json);
                if (lista.isEmpty()) {
                    mainHandler.post(() -> callback.onErro("A API nao devolveu mini-jogos"));
                } else {
                    mainHandler.post(() -> callback.onSucesso(lista));
                }
            } catch (IOException | JSONException e) {
                mainHandler.post(() -> callback.onErro("Erro ao obter mini-jogos: " + e.getMessage()));
            }
        });
    }

    /** Para a thread de rede; chamar quando o ecra deixa de existir. */
    public void fechar() {
        executor.shutdownNow();
    }
}