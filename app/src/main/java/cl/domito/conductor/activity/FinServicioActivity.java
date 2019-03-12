package cl.domito.conductor.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cl.domito.conductor.R;
import cl.domito.conductor.dominio.Conductor;

public class FinServicioActivity extends AppCompatActivity {

    private ImageView imageViewAtras;
    private TextView textView;
    private TextView buttonFinalizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_servicio);
        imageViewAtras = findViewById(R.id.imageViewAtras);
        textView = findViewById(R.id.textViewIdServicio);
        buttonFinalizar = findViewById(R.id.buttonFinalizar);
        Conductor.getInstance().setServicioAceptado(false);
        Conductor.getInstance().setIndiceViaje(0);
        imageViewAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });

        buttonFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void volver()
    {
        this.finish();
    }

}
