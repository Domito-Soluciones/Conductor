package cl.domito.conductor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cl.domito.conductor.R;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.thread.AgregarObservacionOperation;
import cl.domito.conductor.thread.CambiarMovilOperation;

public class FinServicioActivity extends AppCompatActivity {

    private ImageView imageViewAtras;
    private TextView textView;
    private TextView textViewCliente;
    private TextView textViewFecha;
    private TextView textViewTarifa;
    private TextView buttonGuardar;
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
        buttonGuardar = findViewById(R.id.buttonFinalizar);

        conductor = Conductor.getInstance();
        conductor.zarpeIniciado = false;
        conductor.pasajeroRecogido = false;
        try {
            Bundle bundle = getIntent().getExtras();
            textView.setText(bundle.getString("id"));
            textViewCliente.setText(bundle.getString("cliente"));
            textViewFecha.setText(bundle.getString("fecha"));
            textViewTarifa.setText(bundle.getString("tarifa"));
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

        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                EditText editText = findViewById(R.id.editText);
                AgregarObservacionOperation agregarObservacionOperation = new AgregarObservacionOperation();
                agregarObservacionOperation.execute(bundle.getString("id"),editText.getText().toString());
                finish();
            }
        });

        CambiarMovilOperation cambiarMovilOperation = new CambiarMovilOperation();
        cambiarMovilOperation.execute("");
    }

    private void volver()
    {
        this.finish();
    }

}
