package com.example.spinparty;

import android.content.Intent;                    // NOVO
import android.graphics.Color;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spinparty.data.DesafioRepository;
import com.example.spinparty.model.Desafio;
import com.example.spinparty.model.Partida;       // NOVO

import java.util.ArrayList;                       // NOVO
import java.util.List;
import java.util.Random;

public class BatataQuenteActivity extends AppCompatActivity {

    // ===== VARIÁVEIS =====
    private View fundo;               // o ecrã todo, para mudar a cor
    private TextView tvTitulo;
    private TextView tvPergunta;
    private Button btnPassar;
    private Button btnRepetir;

    private List<Desafio> desafios;   // as perguntas da Batata quente
    private DesafioRepository desafioRepository = new DesafioRepository();   // fala com o servidor
    private Handler handler = new Handler(Looper.getMainLooper());
    private Random random = new Random();

    // NOVO: jogadores e eliminação
    private List<String> jogadores;                       // nomes vindos da Partida
    private final List<Integer> vivos = new ArrayList<>();       // quem ainda está em jogo
    private final List<Integer> eliminados = new ArrayList<>();  // por ordem de eliminação
    private int vez = 0;                                  // posição na roda de quem tem a batata
    private boolean aEsperaDoBotao = false;               // BOOM no ecrã, à espera do botão?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_batata_quente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. ir buscar os elementos do layout
        fundo = findViewById(R.id.main);
        tvTitulo = findViewById(R.id.tvTitulo);
        tvPergunta = findViewById(R.id.tvPergunta);
        btnPassar = findViewById(R.id.btnPassar);
        btnRepetir = findViewById(R.id.btnRepetir);

        // 2. título que veio da SpinActivity
        String nome = getIntent().getStringExtra("nome");
        if (nome != null) {
            tvTitulo.setText(nome);
        }

        // 3. ir buscar as perguntas ao repositório
        desafios = desafioRepository.getDesafios("batata_quente");   // plano B: perguntas locais

        // pede as perguntas ao servidor (GET /desafios?q={"minijogo":"batata_quente"})
        desafioRepository.carregarDesafios("batata_quente", new DesafioRepository.Callback() {
            @Override
            public void onSucesso(List<Desafio> lista) {
                desafios = lista;                     // passa a usar as do servidor
            }

            @Override
            public void onErro(String mensagem) { }   // sem net: fica com as locais
        });

        // NOVO: no início estão todos vivos (0, 1, 2, ...)
        jogadores = Partida.getAtual().getJogadores();
        for (int i = 0; i < jogadores.size(); i++) vivos.add(i);

        // 4. botões
        btnPassar.setOnClickListener(v -> passar());          // NOVO: passa a vez
        btnRepetir.setOnClickListener(v -> depoisDoBoom());   // NOVO: próxima ronda ou pontuação
    }

    // ===== CICLO DE VIDA =====
    @Override
    protected void onResume() {
        super.onResume();
        if (!aEsperaDoBotao) comecarRonda();          // NOVO: não recomeça se o BOOM está no ecrã
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);     // se sair do ecrã, a bomba não explode
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        desafioRepository.fechar();                   // desliga a thread de rede
    }

    // ===== JOGO =====
    private void comecarRonda() {
        aEsperaDoBotao = false;                       // NOVO
        handler.removeCallbacksAndMessages(null);     // cancela alguma bomba antiga
        fundo.setBackgroundColor(Color.parseColor("#E65100"));
        btnPassar.setVisibility(View.VISIBLE);
        btnRepetir.setVisibility(View.GONE);
        mostrarPergunta();

        int tempo = 15000 + random.nextInt(15000);    // entre 15 e 30 segundos, escondido
        handler.postDelayed(this::explodir, tempo);
    }

    private void mostrarPergunta() {
        String quem = jogadores.get(vivos.get(vez)) + "\n\n";   // nome de quem responde
        if (desafios.isEmpty()) {
            tvPergunta.setText(quem + "Sem perguntas 😅");
            return;
        }
        Desafio d = desafios.get(random.nextInt(desafios.size()));
        tvPergunta.setText(quem + d.getTexto());
    }

    // NOVO: passa a batata à próxima pessoa da roda
    private void passar() {
        vez = (vez + 1) % vivos.size();               // a seguir ao último volta ao primeiro
        mostrarPergunta();
    }

    private void explodir() {
        aEsperaDoBotao = true;                                    // NOVO
        int perdeu = vivos.remove(vez);                           // NOVO: sai da roda quem tinha a batata
        eliminados.add(perdeu);                                   // NOVO: guarda a ordem
        if (vez >= vivos.size()) vez = 0;                         // NOVO: se era o último, volta ao início

        fundo.setBackgroundColor(Color.parseColor("#C62828"));
        String texto = "BOOM! 💥\n" + jogadores.get(perdeu) + " foi eliminado!";

        // NOVO: sobrou só um? Ganhou!
        if (vivos.size() == 1) {
            texto += "\n\n🏆 " + jogadores.get(vivos.get(0)) + " sobreviveu!";
            btnRepetir.setText("VER PONTUAÇÃO");
        } else {
            btnRepetir.setText("PRÓXIMA RONDA");
        }
        tvPergunta.setText(texto);

        btnPassar.setVisibility(View.GONE);
        btnRepetir.setVisibility(View.VISIBLE);
        vibrate();
    }

    // NOVO: o que o botão faz depois do BOOM
    private void depoisDoBoom() {
        if (vivos.size() > 1) {
            comecarRonda();                                       // ainda há jogo
        } else {
            darPontos();
            startActivity(new Intent(this, PontuacaoActivity.class));
            finish();                                             // fecha a Batata quente
        }
    }

    // NOVO: sobrevivente 3, último eliminado 2, depois 1, primeiro eliminado 0
    private void darPontos() {
        List<Integer> ordem = new ArrayList<>();
        ordem.add(vivos.get(0));                                  // 1.º lugar: o sobrevivente
        for (int i = eliminados.size() - 1; i >= 0; i--) {
            ordem.add(eliminados.get(i));                         // depois, do último eliminado ao primeiro
        }

        int[] pontosPorLugar = {3, 2, 1, 0};
        for (int lugar = 0; lugar < ordem.size(); lugar++) {
            Partida.getAtual().darPontos(ordem.get(lugar), pontosPorLugar[lugar]);
        }
    }

    private void vibrate() {
        Vibrator vibrator = getSystemService(Vibrator.class);
        vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
    }

}