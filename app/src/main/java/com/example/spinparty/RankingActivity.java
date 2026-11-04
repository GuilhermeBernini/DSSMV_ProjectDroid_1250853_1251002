package com.example.spinparty;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spinparty.data.JogadorRepository;   // NOVO
import com.example.spinparty.model.Jogador;             // NOVO

import java.util.List;                                  // NOVO

public class RankingActivity extends AppCompatActivity {

    private TextView tvRanking;     // onde aparece a lista "1. Ana  9 pts"
    private JogadorRepository jogadorRepository;        // NOVO: quem fala com o servidor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ranking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvRanking = findViewById(R.id.tvRanking);
        Button btnVoltar = findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(v -> finish());   // fecha o Ranking e volta ao ecrã inicial

        // NOVO: pede o top 10 ao servidor (GET /jogadores ordenado por pontuação)
        jogadorRepository = new JogadorRepository();
        jogadorRepository.carregarRanking(new JogadorRepository.ListaCallback() {
            @Override
            public void onSucesso(List<Jogador> jogadores) {     // a resposta chegou
                mostrarRanking(jogadores);
            }

            @Override
            public void onErro(String mensagem) {                // sem internet ou erro no servidor
                tvRanking.setText("Sem ligação ao servidor 😕");
            }
        });
    }

    // NOVO: escreve "1. Ana   9 pts" uma linha por jogador
    private void mostrarRanking(List<Jogador> jogadores) {
        if (jogadores.isEmpty()) {
            tvRanking.setText("Ainda ninguém jogou!");
            return;
        }
        String texto = "";
        for (int i = 0; i < jogadores.size(); i++) {
            Jogador j = jogadores.get(i);
            texto += (i + 1) + ". " + j.getNome() + "   " + j.getPontuacao() + " pts\n";
        }
        tvRanking.setText(texto);
    }

    // NOVO: ao sair do ecrã, desliga a thread de rede
    @Override
    protected void onDestroy() {
        super.onDestroy();
        jogadorRepository.fechar();
    }
}