package com.example.spinparty;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import com.example.spinparty.data.JogadorRepository;
import com.example.spinparty.model.Jogador;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom);

                    return insets;
                });

        Button btnStart = findViewById(R.id.btnStart);
        Button btnRules = findViewById(R.id.btnRules);
        EditText etNomeJogador = findViewById(R.id.etNomeJogador);

        jogadorRepository = new JogadorRepository();

        btnStart.setOnClickListener(v -> {
            String nome = etNomeJogador.getText().toString().trim();
            if (nome.isEmpty()) {
                Toast.makeText(this, "Escreve o teu nome primeiro", Toast.LENGTH_SHORT).show();
                return;
            }

            jogadorRepository.criarJogador(nome, new JogadorRepository.Callback() {
                @Override
                public void onSucesso(Jogador jogador) {
                    abrirSpinActivity(jogador.getId(), jogador.getNome());
                }

                @Override
                public void onErro(String mensagem) {
                    Toast.makeText(MainActivity.this,
                            "Não foi possível guardar o jogador, mas podes jogar na mesma",
                            Toast.LENGTH_SHORT).show();
                    abrirSpinActivity(null, nome);
                }
            });
        });

        btnRules.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "O telemóvel escolhe o desafio. "
                                + "Joga com os teus amigos e ganha pontos!",
                        Toast.LENGTH_LONG
                ).show()
        );
    }
    
    private void abrirSpinActivity(String jogadorId, String nomeJogador) {
    Intent intent = new Intent(this, SpinActivity.class);
    intent.putExtra("jogadorId", jogadorId);
    intent.putExtra("jogadorNome", nomeJogador);
    startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        jogadorRepository.fechar();
    }
}