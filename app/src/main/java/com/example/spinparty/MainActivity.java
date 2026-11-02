package com.example.spinparty;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spinparty.data.JogadorRepository;
import com.example.spinparty.model.Jogador;
import com.example.spinparty.model.Partida;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private JogadorRepository jogadorRepository;
    private TextView tvJogadores;                 // onde aparece a lista "1. Dinis..."

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnStart = findViewById(R.id.btnStart);
        Button btnRules = findViewById(R.id.btnRules);
        Button btnAdicionar = findViewById(R.id.btnAdicionar);
        EditText etNomeJogador = findViewById(R.id.etNomeJogador);
        tvJogadores = findViewById(R.id.tvJogadores);

        jogadorRepository = new JogadorRepository();

        // botão +: junta o nome escrito à partida
        btnAdicionar.setOnClickListener(v -> {
            String nome = etNomeJogador.getText().toString().trim();
            if (nome.isEmpty()) {
                Toast.makeText(this, "Escreve um nome primeiro", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Partida.getAtual().getJogadores().size() >= 4) {
                Toast.makeText(this, "Máximo 4 jogadores", Toast.LENGTH_SHORT).show();
                return;
            }
            Partida.getAtual().adicionarJogador(nome);   // escreve no "caderno"
            etNomeJogador.setText("");                   // limpa a caixa para o próximo
            mostrarJogadores();                          // atualiza a lista no ecrã

            // grava também no restdb.io (parte do Bernini); se falhar, joga-se na mesma
            jogadorRepository.criarJogador(nome, new JogadorRepository.Callback() {
                @Override
                public void onSucesso(Jogador jogador) { }

                @Override
                public void onErro(String mensagem) { }
            });
        });

        // COMEÇAR: só deixa avançar com pelo menos 2 jogadores
        btnStart.setOnClickListener(v -> {
            if (Partida.getAtual().getJogadores().size() < 2) {
                Toast.makeText(this, "Adiciona pelo menos 2 jogadores", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, SpinActivity.class));
        });

        btnRules.setOnClickListener(v ->
                Toast.makeText(this,
                        "O telemóvel escolhe o desafio. Joga com os teus amigos e ganha pontos!",
                        Toast.LENGTH_LONG).show()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarJogadores();     // ao voltar a este ecrã, mostra a lista atualizada
    }

    // escreve "Jogadores: 1. Dinis 2. Bernini..." no tvJogadores
    private void mostrarJogadores() {
        List<String> jogadores = Partida.getAtual().getJogadores();
        if (jogadores.isEmpty()) {
            tvJogadores.setText("Ainda não há jogadores");
            return;
        }
        String texto = "Jogadores:";
        for (int i = 0; i < jogadores.size(); i++) {
            texto += "\n" + (i + 1) + ". " + jogadores.get(i);
        }
        tvJogadores.setText(texto);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jogadorRepository.fechar();
    }
}