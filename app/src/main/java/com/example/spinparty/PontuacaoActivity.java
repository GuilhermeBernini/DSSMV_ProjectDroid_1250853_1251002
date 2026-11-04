package com.example.spinparty;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spinparty.model.Partida;
import com.example.spinparty.data.JogadorRepository;
import com.example.spinparty.model.Jogador;

import java.util.ArrayList;
import java.util.List;

public class PontuacaoActivity extends AppCompatActivity {

    private static final int TOTAL_JOGOS = 3;      // ao fim de 3 jogos, a partida acaba

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pontuacao);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvTitulo = findViewById(R.id.tvTitulo);
        TextView tvTabela = findViewById(R.id.tvTabela);
        Button btnProximo = findViewById(R.id.btnProximo);

        Partida partida = Partida.getAtual();
        List<String> jogadores = partida.getJogadores();

        // 1. ordenar os jogadores do que tem mais pontos para o que tem menos
        List<Integer> ordem = new ArrayList<>();
        for (int i = 0; i < jogadores.size(); i++) {
            ordem.add(i);                               // [0, 1, 2] = posições dos jogadores
        }
        ordem.sort((a, b) -> partida.getPontos(b) - partida.getPontos(a));

        // 2. escrever a tabela, uma linha por jogador
        String texto = "";
        for (int lugar = 0; lugar < ordem.size(); lugar++) {
            int j = ordem.get(lugar);                   // qual jogador está neste lugar
            texto += (lugar + 1) + ". " + jogadores.get(j) + "   " + partida.getPontos(j) + " pts\n";
        }
        tvTabela.setText(texto.trim());

        // 3. a meio da partida ou no fim?
        if (partida.getNumeroJogosJogados() >= TOTAL_JOGOS) {
            // FIM: mostra o vencedor e volta ao início
            String vencedor = jogadores.get(ordem.get(0));
            tvTitulo.setText("🏆 " + vencedor + " ganhou!");
            btnProximo.setText("VOLTAR AO INÍCIO");
            guardarPontosNoServidor(partida);       // PUT dos pontos finais de cada jogador
            btnProximo.setOnClickListener(v -> {
                Partida.novaPartida();                  // apaga tudo para a próxima festa
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   // fecha os ecrãs do meio
                startActivity(intent);
                finish();
            });

        } else {
            // A MEIO: fecha este ecrã e volta ao Spin
            btnProximo.setOnClickListener(v -> finish());
        }
    }

    // manda os pontos de cada jogador para o restdb (PUT /jogadores/{id})
    private void guardarPontosNoServidor(Partida partida) {
        JogadorRepository repository = new JogadorRepository();
        for (int i = 0; i < partida.getJogadores().size(); i++) {
            String id = partida.getId(i);             // o id que guardámos quando o jogador foi criado
            if (id == null) continue;                 // o POST falhou (sem net): não há nada para atualizar
            repository.atualizarPontuacao(id, partida.getPontos(i), new JogadorRepository.Callback() {
                @Override
                public void onSucesso(Jogador jogador) { }   // guardado, não é preciso mostrar nada

                @Override
                public void onErro(String mensagem) { }      // sem net: o jogo continua na mesma
            });
        }
    }
}
