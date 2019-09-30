package cl.domito.dmttransfer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.thread.AgregarObservacionOperation;
import cl.domito.dmttransfer.thread.CambiarMovilOperation;
import cl.domito.dmttransfer.thread.EnviarLogOperation;

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
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
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
               guardarComentario();
            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        CambiarMovilOperation cambiarMovilOperation = new CambiarMovilOperation();
        cambiarMovilOperation.execute("");
        super.onPostCreate(savedInstanceState);
    }

    private void volver()
    {
        this.finish();
    }


    private void guardarComentario() {
        Bundle bundle = getIntent().getExtras();
        EditText editText = findViewById(R.id.editText);
        AgregarObservacionOperation agregarObservacionOperation = new AgregarObservacionOperation(this);
        agregarObservacionOperation.execute(bundle.getString("id"),editText.getText().toString());
        finish();
    }
}
