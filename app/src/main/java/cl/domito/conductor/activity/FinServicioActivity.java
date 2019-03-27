package cl.domito.conductor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cl.domito.conductor.R;
import cl.domito.conductor.dominio.Conductor;

public class FinServicioActivity extends AppCompatActivity {

    private ImageView imageViewAtras;
    private TextView textView;
    private TextView textViewCliente;
    private TextView textViewFecha;
    private TextView textViewTarifa;
    private TextView buttonFinalizar;
    private Conductor conductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_servicio);
        imageViewAtras = findViewById(R.id.imageViewAtras);
        textView = findViewById(R.id.textViewIdServicio);
        textViewCliente = findViewById(R.id.textViewClienteValor);
        textViewFecha = findViewById(R.id.textViewFechaValor);
        textViewTarifa = findViewById(R.id.textViewTarifaValor);
        buttonFinalizar = findViewById(R.id.buttonFinalizar);

        conductor = Conductor.getInstance();

        try {
            Bundle bundle = getIntent().getExtras();
            textView.setText(conductor.getServicioActual());
            textViewCliente.setText(bundle.getString("cliente"));
            textViewFecha.setText(bundle.getString("fecha"));
            textViewTarifa.setText(bundle.getString("tarifa"));
            conductor.setServicioActual(null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

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
