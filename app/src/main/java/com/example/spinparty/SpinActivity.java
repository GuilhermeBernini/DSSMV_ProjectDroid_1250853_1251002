package com.example.spinparty;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SpinActivity extends AppCompatActivity implements SensorEventListener {
    private TextView tvNumber;
    private TextView tvResult;

    private SensorManager sensorManager;  // declarar as variáveis
    private Sensor gyroscope;

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
        float z = event.values[2];          // rotação à volta do eixo z (rad/s)
        tvNumber.setText(String.valueOf(z)); // mostra o valor no ecrã
    }
}