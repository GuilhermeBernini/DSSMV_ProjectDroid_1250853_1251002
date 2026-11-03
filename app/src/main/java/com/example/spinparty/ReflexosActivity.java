package com.example.spinparty;

import android.graphics.Color;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;
import com.example.spinparty.model.Partida;

import java.util.ArrayList;
import java.util.List;

public class ReflexosActivity extends AppCompatActivity implements SensorEventListener {

    // ===== VARIÁVEIS =====
    private View fundo;               // o ecrã todo, para mudar a cor
    private TextView tvTitulo;
    private TextView tvMensagem;
    private Button btnRepetir;

    private SensorManager sensorManager;
    private Sensor acelerometro;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private boolean verde = false;     // o ecrã já ficou verde?
    private boolean acabou = false;    // a ronda já terminou?
    private long horaVerde = 0;        // a que horas (ms) ficou verde
    private long horaInicio = 0;      // quando começou a ronda

    // jogadores e tempos
    private List<String> jogadores;                 // nomes vindos da Partida
    private int jogadorAtual = 0;                   // de quem é a vez (0 = primeiro)
    private long[] tempos;                          // tempo de reação de cada jogador
    private boolean aEsperaDeComecar = true;        // o botão diz COMEÇAR ou PRÓXIMO?
    private static final long BATOTA = Long.MAX_VALUE;  // tempo "infinito" = fez batota

    // ===== QUANDO O ECRÃ É CRIADO =====
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reflexos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fundo = findViewById(R.id.main);
        tvTitulo = findViewById(R.id.tvTitulo);
        tvMensagem = findViewById(R.id.tvMensagem);
        btnRepetir = findViewById(R.id.btnRepetir);
        // o mesmo botão faz duas coisas
        btnRepetir.setOnClickListener(v -> {
            if (aEsperaDeComecar) comecarRonda();   // COMEÇAR → começa a ronda
            else proximoJogador();                  // PRÓXIMO → passa ao seguinte
        });

        String nome = getIntent().getStringExtra("nome");   // o que veio da SpinActivity
        tvTitulo.setText(nome);
        jogadores = Partida.getAtual().getJogadores();   // vai buscar os nomes ao caderno
        tempos = new long[jogadores.size()];             // cria uma caixinha por jogador

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_GAME);
        mostrarVez();      // primeiro mostra de quem é a vez
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (acabou) return;                                            // ronda já terminou
        if (System.currentTimeMillis() - horaInicio < 1500) return;    // dar tempo para pousar

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        double forca = Math.sqrt(x * x + y * y + z * z);               // força total
        boolean mexeu = Math.abs(forca - SensorManager.GRAVITY_EARTH) > 3;
        if (!mexeu) return;

        acabou = true;
        if (!verde) {
            handler.removeCallbacksAndMessages(null);                  // cancela o verde
            tempos[jogadorAtual] = BATOTA;                             // guarda batota
            tvMensagem.setText("Batoteiro! Pegaste antes do verde 😅");
        } else {
            long tempo = System.currentTimeMillis() - horaVerde;
            tempos[jogadorAtual] = tempo;                              // guarda o tempo
            tvMensagem.setText("Tempo de reação: " + tempo + " ms");
        }

        // o botão passa a ser "seguinte"
        aEsperaDeComecar = false;
        boolean ultimo = jogadorAtual == jogadores.size() - 1;         // é o último jogador?
        btnRepetir.setText(ultimo ? "VER PONTUAÇÃO" : "PRÓXIMO JOGADOR");
        btnRepetir.setVisibility(View.VISIBLE);
    }

    // ecrã "Vez do X" antes de cada ronda
    private void mostrarVez() {
        acabou = true;                 // sensor desligado até carregar COMEÇAR
        aEsperaDeComecar = true;       // o botão vai dizer COMEÇAR
        handler.removeCallbacksAndMessages(null);                 // cancela verdes pendentes
        fundo.setBackgroundColor(Color.parseColor("#C62828"));   // vermelho
        tvMensagem.setText("Vez do " + jogadores.get(jogadorAtual) + "!\n\n"
                + "Carrega em COMEÇAR, pousa o telemóvel e espera pelo verde.");
        btnRepetir.setText("COMEÇAR");
        btnRepetir.setVisibility(View.VISIBLE);
    }
        private void comecarRonda() {

            horaInicio = System.currentTimeMillis();
            verde = false;
            acabou = false;
            fundo.setBackgroundColor(Color.parseColor("#C62828"));   // vermelho
            tvMensagem.setText("Pousa o telemóvel na mesa e espera pelo verde!");
            btnRepetir.setVisibility(View.GONE);

            int espera = 2000 + random.nextInt(3000);   // entre 2 e 5 segundos
            handler.postDelayed(this::ficarVerde, espera);
        }

        private void ficarVerde() {
            verde = true;
            horaVerde = System.currentTimeMillis();
            fundo.setBackgroundColor(Color.parseColor("#2E7D32"));   // verde
            tvMensagem.setText("JÁ! Agarra o telemóvel!");
            vibrate();
        }
        private void vibrate() {
            Vibrator vibrator = getSystemService(Vibrator.class);
            vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE));
        }

    // passa ao próximo jogador ou acaba o jogo
    private void proximoJogador() {
        jogadorAtual++;                                           // vez do seguinte
        if (jogadorAtual < jogadores.size()) {
            mostrarVez();                                         // ainda há jogadores
        } else {
            darPontos();                                          // todos jogaram
            startActivity(new Intent(this, PontuacaoActivity.class));
            finish();                                             // fecha o Reflexos
        }
    }

    // ordena por tempo e dá 3/2/1/0
    private void darPontos() {
        List<Integer> ordem = new ArrayList<>();
        for (int i = 0; i < jogadores.size(); i++) ordem.add(i);          // [0,1,2,...]
        ordem.sort((a, b) -> Long.compare(tempos[a], tempos[b]));        // mais rápido primeiro

        int[] pontosPorLugar = {3, 2, 1, 0};
        for (int lugar = 0; lugar < ordem.size(); lugar++) {
            int jogador = ordem.get(lugar);
            if (tempos[jogador] == BATOTA) continue;                      // batoteiro não ganha
            Partida.getAtual().darPontos(jogador, pontosPorLugar[lugar]);
        }
    }

}