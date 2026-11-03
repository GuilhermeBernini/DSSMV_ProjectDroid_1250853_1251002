package com.example.spinparty;

import android.content.Intent;                          // NOVO
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spinparty.model.Partida;             // NOVO

import java.util.List;                                  // NOVO
import java.util.Random;

public class ContarVibracoesActivity extends AppCompatActivity {

    // ===== VARIÁVEIS =====
    private TextView tvTitulo;
    private TextView tvMensagem;
    private Button btnVibrar;
    private Button btnRepetir;
    private LinearLayout layoutNumeros;   // a caixa com os 6 botões

    private Handler handler = new Handler(Looper.getMainLooper());
    private Random random = new Random();
    private int numeroCerto;              // quantas vezes vibrou nesta ronda

    private List<String> jogadores;       // NOVO: os nomes, vindos da Partida
    private int jogadorAtual = 0;         // NOVO: de quem é a vez (0 = o primeiro)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contar_vibracoes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. ir buscar os elementos do layout
        tvTitulo = findViewById(R.id.tvTitulo);
        tvMensagem = findViewById(R.id.tvMensagem);
        btnVibrar = findViewById(R.id.btnVibrar);
        btnRepetir = findViewById(R.id.btnRepetir);
        layoutNumeros = findViewById(R.id.layoutNumeros);

        // 2. título que veio da SpinActivity
        String nome = getIntent().getStringExtra("nome");
        if (nome != null) {
            tvTitulo.setText(nome);
        }

        jogadores = Partida.getAtual().getJogadores();    // NOVO

        // 3. VIBRAR começa a ronda; o botão de baixo passa ao próximo jogador
        btnVibrar.setOnClickListener(v -> comecarRonda());
        btnRepetir.setOnClickListener(v -> proximoJogador());   // NOVO (antes era comecarRonda)

        // 4. os 6 botões dos números: em vez de 6 blocos iguais, um ciclo
        int[] ids = {R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8};
        for (int i = 0; i < ids.length; i++) {
            int numero = i + 3;                       // btn3 -> 3, btn4 -> 4, ...
            Button b = findViewById(ids[i]);
            b.setOnClickListener(v -> verificar(numero));
        }
    }

    // ===== CICLO DE VIDA =====
    @Override
    protected void onResume() {
        super.onResume();
        mostrarInicio();                              // ecrã inicial com o botão VIBRAR
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);     // se sair, pára as vibrações marcadas
    }

    // ===== JOGO =====
    private void mostrarInicio() {
        // NOVO: diz de quem é a vez
        tvMensagem.setText("Vez do " + jogadores.get(jogadorAtual) + "!\n\nFecha os olhos, segura o telemóvel na mão e conta as vibrações!");
        btnVibrar.setVisibility(View.VISIBLE);
        btnRepetir.setVisibility(View.GONE);
        layoutNumeros.setVisibility(View.GONE);
    }

    private void comecarRonda() {
        handler.removeCallbacksAndMessages(null);
        numeroCerto = 3 + random.nextInt(6);          // entre 3 e 8

        tvMensagem.setText("A vibrar... 🤫");
        btnVibrar.setVisibility(View.GONE);
        btnRepetir.setVisibility(View.GONE);
        layoutNumeros.setVisibility(View.GONE);

        // marca cada vibração com um intervalo diferente, para não dar para contar pelo ritmo
        long tempo = 1500;                            // 1,5 s para fechar os olhos
        for (int i = 0; i < numeroCerto; i++) {
            handler.postDelayed(this::vibrarUmaVez, tempo);
            tempo += 400 + random.nextInt(800);       // próxima entre 0,4 e 1,2 s depois
        }
        handler.postDelayed(this::perguntar, tempo + 1000);   // 1 s depois da última
    }

    private void vibrarUmaVez() {
        Vibrator vibrator = getSystemService(Vibrator.class);
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    private void perguntar() {
        tvMensagem.setText("Quantas vibrações foram?");
        layoutNumeros.setVisibility(View.VISIBLE);    // aparecem os botões 3 a 8
    }

    private void verificar(int resposta) {
        layoutNumeros.setVisibility(View.GONE);
        if (resposta == numeroCerto) {
            Partida.getAtual().darPontos(jogadorAtual, 2);    // NOVO: quem acerta leva 2
            tvMensagem.setText("Acertaste! 🎉 +2 pontos\nForam " + numeroCerto + " vibrações.");
        } else {
            tvMensagem.setText("Falhaste! 😅\nEram " + numeroCerto + " vibrações.");
        }

        // NOVO: o texto do botão depende de ainda faltar alguém ou não
        if (jogadorAtual == jogadores.size() - 1) {
            btnRepetir.setText("VER PONTUAÇÃO");
        } else {
            btnRepetir.setText("PRÓXIMO JOGADOR");
        }
        btnRepetir.setVisibility(View.VISIBLE);
    }

    // NOVO: passa a vez; depois do último, abre a pontuação
    private void proximoJogador() {
        jogadorAtual++;
        if (jogadorAtual < jogadores.size()) {
            mostrarInicio();                          // "Vez do próximo!"
        } else {
            startActivity(new Intent(this, PontuacaoActivity.class));
            finish();                                 // fecha o jogo; a pontuação fica por cima do Spin
        }
    }

}