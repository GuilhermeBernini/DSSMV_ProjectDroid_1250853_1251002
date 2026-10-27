package com.example.spinparty;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.spinparty.data.MiniJogoRepository;
import com.example.spinparty.model.MiniJogo;

import java.util.List;
import java.util.Random;

public class SpinActivity extends AppCompatActivity implements SensorEventListener {
    private TextView tvNumber;
    private TextView tvResult;

    private SensorManager sensorManager;  // declarar as variáveis
    private Sensor gyroscope;

    private List<MiniJogo> jogos;
    private final Random random = new Random();
    private boolean isSpinning = false;   // está a girar agora?
    private long lastSpinTime = 0;        // última vez (em ms) que o vimos girar
    private int currentNumber = 0;        // número que está no ecrã
    private boolean hasResult = false;    // já saiu um jogo?
    private Button btnRespin;             // botão "girar outra vez"

    private Button btnJogar;              // botão "jogar"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_spin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvNumber = findViewById(R.id.tvNumber);
        tvResult = findViewById(R.id.tvResult);

        jogos = new MiniJogoRepository().getMiniJogos();

        btnRespin = findViewById(R.id.btnRespin);
        btnRespin.setOnClickListener(v -> {
            hasResult = false;
            tvNumber.setText("?");
            tvResult.setText("");
            btnRespin.setVisibility(View.GONE);
            btnJogar.setVisibility(View.GONE);

        });
        btnJogar = findViewById(R.id.btnJogar);
        btnJogar.setOnClickListener(v -> abrirJogo());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); // pede ao android acesso aos sensores
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); // já tem acesso por isso vai buscar o gyroscope
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float z = Math.abs(event.values[2]);   // velocidade de rotação, sem sinal
        long now = System.currentTimeMillis(); // hora atual em milissegundos

        if (!hasResult && z > 2) {
            // ESTÁ A GIRAR
            isSpinning = true;
            lastSpinTime = now;
            currentNumber = random.nextInt(jogos.size()) + 1;  // número entre 1 e 3
            tvNumber.setText(String.valueOf(currentNumber));
            tvResult.setText("");
        } else if (isSpinning && now - lastSpinTime > 500) {
            // PAROU: já passou meio segundo sem girar
            isSpinning = false;
            tvResult.setText(jogos.get(currentNumber - 1).getNome());   // nome do jogo
            vibrate();
            hasResult = true;
            btnRespin.setVisibility(View.VISIBLE);
            btnJogar.setVisibility(View.VISIBLE);
        }
    }

    private void vibrate() {
        Vibrator vibrator = getSystemService(Vibrator.class);
        vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    private void abrirJogo(){
        MiniJogo jogo = jogos.get(currentNumber - 1);  // o jogo que saiu
        if (jogo.getTipo().equals("reflexos")){
            Intent intent = new Intent(this, ReflexosActivity.class);
            intent.putExtra("nome", jogo.getNome());
            startActivity(intent);
        }else{
            Toast.makeText(this, "Em breve: " + jogo.getNome(), Toast.LENGTH_SHORT).show();
        }
    }
}