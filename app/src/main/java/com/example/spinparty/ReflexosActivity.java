package com.example.spinparty;

import android.graphics.Color;
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
        btnRepetir.setOnClickListener(v -> comecarRonda());

        String nome = getIntent().getStringExtra("nome");   // o que veio da SpinActivity
        tvTitulo.setText(nome);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_GAME);
        comecarRonda();
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
            tvMensagem.setText("Batoteiro! Pegaste antes do verde 😅");
        } else {
            long tempo = System.currentTimeMillis() - horaVerde;
            tvMensagem.setText("Tempo de reação: " + tempo + " ms");
        }
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

}