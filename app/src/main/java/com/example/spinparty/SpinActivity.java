package com.example.spinparty;

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
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class SpinActivity extends AppCompatActivity implements SensorEventListener {
    private TextView tvNumber;
    private TextView tvResult;

    private SensorManager sensorManager;  // declarar as variáveis
    private Sensor gyroscope;

    private final String[] games = {"Reflexos", "Batata quente", "Contar vibrações"};
    private final Random random = new Random();
    private boolean isSpinning = false;   // está a girar agora?
    private long lastSpinTime = 0;        // última vez (em ms) que o vimos girar
    private int currentNumber = 0;        // número que está no ecrã
    private boolean hasResult = false;    // já saiu um jogo?
    private Button btnRespin;             // botão "girar outra vez"


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

        btnRespin = findViewById(R.id.btnRespin);
        btnRespin.setOnClickListener(v -> {
            hasResult = false;
            tvNumber.setText("?");
            tvResult.setText("");
            btnRespin.setVisibility(View.GONE);
        });
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
            currentNumber = random.nextInt(3) + 1;   // número entre 1 e 3
            tvNumber.setText(String.valueOf(currentNumber));
            tvResult.setText("");
        } else if (isSpinning && now - lastSpinTime > 500) {
            // PAROU: já passou meio segundo sem girar
            isSpinning = false;
            tvResult.setText(games[currentNumber - 1]);   // nome do jogo
            vibrate();
            hasResult = true;
            btnRespin.setVisibility(View.VISIBLE);
        }
    }

    private void vibrate() {
        Vibrator vibrator = getSystemService(Vibrator.class);
        vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE));
    }
}