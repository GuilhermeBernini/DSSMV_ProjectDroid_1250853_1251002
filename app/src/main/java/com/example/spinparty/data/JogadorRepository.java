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

}
