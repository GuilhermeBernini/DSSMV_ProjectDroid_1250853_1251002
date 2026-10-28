package com.example.spinparty;

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
    private Handler handler = new Handler(Looper.getMainLooper());
    private Random random = new Random();

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
        desafios = new DesafioRepository().getDesafios("batata_quente");

        // 4. botões
        btnPassar.setOnClickListener(v -> mostrarPergunta());
        btnRepetir.setOnClickListener(v -> comecarRonda());
    }

    // ===== CICLO DE VIDA =====
    @Override
    protected void onResume() {
        super.onResume();
        comecarRonda();                               // ao abrir o ecrã, começa logo
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);     // se sair do ecrã, a bomba não explode
    }

    // ===== JOGO =====
    private void comecarRonda() {
        handler.removeCallbacksAndMessages(null);     // cancela alguma bomba antiga
        fundo.setBackgroundColor(Color.parseColor("#E65100"));
        btnPassar.setVisibility(View.VISIBLE);
        btnRepetir.setVisibility(View.GONE);
        mostrarPergunta();

        int tempo = 15000 + random.nextInt(15000);    // entre 15 e 30 segundos, escondido
        handler.postDelayed(this::explodir, tempo);
    }

    private void mostrarPergunta() {
        if (desafios.isEmpty()) {
            tvPergunta.setText("Sem perguntas 😅");
            return;
        }
        Desafio d = desafios.get(random.nextInt(desafios.size()));
        tvPergunta.setText(d.getTexto());
    }

    private void explodir() {
        fundo.setBackgroundColor(Color.parseColor("#C62828"));
        tvPergunta.setText("BOOM! 💥\nQuem tem o telemóvel perdeu!");
        btnPassar.setVisibility(View.GONE);
        btnRepetir.setVisibility(View.VISIBLE);
        vibrate();
    }

    private void vibrate() {
        Vibrator vibrator = getSystemService(Vibrator.class);
        vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
    }

}