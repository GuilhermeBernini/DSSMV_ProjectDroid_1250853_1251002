package com.example.spinparty.data;

import android.os.Handler;
import android.os.Looper;

import com.example.spinparty.BuildConfig;
import com.example.spinparty.model.Desafio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DesafioRepository {

    /** Resultado do pedido a API; os dois metodos sao chamados na thread principal. */
    public interface Callback {
        void onSucesso(List<Desafio> desafios);

        void onErro(String mensagem);
    }

    private final RestDbClient client =
            new RestDbClient(BuildConfig.RESTDB_BASE_URL, BuildConfig.RESTDB_API_KEY);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /** Lista local (fixa), usada quando a API nao esta configurada ou falha. */
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

    /** Pede os desafios de um mini-jogo ao restdb.io (GET /desafios?q={"minijogo":"<tipo>"}). */
    public void carregarDesafios(String tipo, Callback callback) {
        if (!client.isConfigured()) {
            callback.onErro("Falta configurar RESTDB_BASE_URL e RESTDB_API_KEY");
            return;
        }
        executor.execute(() -> {
            try {
                String filtro = "{\"minijogo\":\"" + tipo + "\"}";
                String path = "/desafios?q=" + URLEncoder.encode(filtro, "UTF-8");

                String json = client.get(path);
                JSONArray array = new JSONArray(json);
                List<Desafio> lista = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String texto = obj.optString("texto", "").trim();
                    if (texto.isEmpty()) {
                        continue;
                    }
                    String resposta = obj.optString("resposta", "").trim();
                    String minijogo = obj.optString("minijogo", "").trim();
                    lista.add(new Desafio(texto, resposta, minijogo));
                }

                if (lista.isEmpty()) {
                    mainHandler.post(() -> callback.onErro("A API nao devolveu desafios"));
                } else {
                    mainHandler.post(() -> callback.onSucesso(lista));
                }
            } catch (IOException | JSONException | UnsupportedEncodingException e) {
                mainHandler.post(() -> callback.onErro("Erro ao obter desafios: " + e.getMessage()));
            }
        });
    }

    /** Para a thread de rede; chamar quando o ecra deixa de existir. */
    public void fechar() {
        executor.shutdownNow();
    }
}