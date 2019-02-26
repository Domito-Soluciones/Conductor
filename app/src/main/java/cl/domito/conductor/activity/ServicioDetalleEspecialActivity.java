package cl.domito.conductor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.adapter.ReciclerViewDetalleAdapter;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.thread.DesAsignarServicioEspecialOperation;
import cl.domito.conductor.thread.DesAsignarServicioOperation;
import cl.domito.conductor.thread.RealizarServicioEspecialOperation;
import cl.domito.conductor.thread.RealizarServicioOperation;

public class ServicioDetalleEspecialActivity extends AppCompatActivity {

    private ImageView imageViewAtrasInt;
    private Button buttonConfirmar;
    private Button buttonCancelar;
    private TextView textviewServicioValor;
    private TextView textviewFechaValor;
    private TextView textviewPartidaValor;
    private TextView textviewDestinoValor;
    private TextView textviewPasajeroValor;
    private TextView textviewCelularValor;
    private TextView textviewTarifaValor;
    private TextView textviewObservacionValor;
    private String estado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicio_detalle_especial);

        imageViewAtrasInt = findViewById(R.id.imageViewAtrasInt);
        buttonConfirmar = findViewById(R.id.buttonConfirmar);
        buttonCancelar = findViewById(R.id.buttonCancelar);
        textviewServicioValor = findViewById(R.id.textViewIdServicioValor);
        textviewFechaValor = findViewById(R.id.textViewFechaValor);
        textviewPartidaValor = findViewById(R.id.textViewPartidaValor);
        textviewDestinoValor = findViewById(R.id.textViewDestinoValor);
        textviewPasajeroValor = findViewById(R.id.textViewRutaValor);
        textviewCelularValor = findViewById(R.id.textViewCelularValor);
        textviewTarifaValor = findViewById(R.id.textViewTarifaValor);
        textviewObservacionValor = findViewById(R.id.textViewObservacionValor);

        buttonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aceptarServicio();
            }
        });

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desasignarServicio();
            }
        });

        try {
            //vh.textView.setTextColor(vh.textView.getContext().getResources().getColor(R.color.verde));
            //vh.imageView.setImageDrawable(vh.textView.getContext().getResources().getDrawable(R.drawable.arriba));
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = sdf.parse(getIntent().getExtras().getString("fecha"));
            System.out.println(date);
            Long l = date.getTime();
            Date dateNow = new Date();
            System.out.println(dateNow);
            Long lNow = dateNow.getTime();
            //if(date.before(dateN ow)) {
            Long data = Math.abs(lNow - l);
            //if (data <= 600000) {
            Conductor conductor = Conductor.getInstance();
                try {
                    int cantidad = 0;
                    ArrayList<String> lista = new ArrayList();
                    for(int i =  0; i < conductor.getServiciosEspeciales().length();i++ ) {
                        JSONObject servicio = conductor.getServiciosEspeciales().getJSONObject(i);
                        textviewServicioValor.setText(servicio.getString("servicio_id"));
                        textviewFechaValor.setText(servicio.getString("servicio_fecha") + " " + servicio.getString("servicio_hora"));
                        textviewPartidaValor.setText(servicio.getString("servicio_partida"));
                        textviewDestinoValor.setText(servicio.getString("servicio_destino"));
                        textviewPasajeroValor.setText(servicio.getString("servicio_pasajero"));
                        textviewCelularValor.setText(servicio.getString("servicio_celular"));
                        textviewTarifaValor.setText(servicio.getString("servicio_tarifa"));
                        textviewObservacionValor.setText(servicio.getString("servicio_observacion").equals("")?"Sin observaciones":servicio.getString("servicio_observacion"));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                if(estado.equals("1"))
                {
                    buttonConfirmar.setText("Aceptar");
                }
                else if(estado.equals("3"))
                {
                    buttonConfirmar.setText("Iniciar");
                }
            conductor.setServicioActual(null);
            //activity.finish();
            //} else {
            //    System.out.println("la diferencia es de mas de 10 minutos");
            //}
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageViewAtrasInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
    }

    private void volver()
    {
        this.finish();
        Intent intent = new Intent(this,ServicioEspecialActivity.class);
        startActivity(intent);
    }

    private void aceptarServicio() {
        if(estado.equals("1")) {
            RealizarServicioEspecialOperation realizarServicioOperation = new RealizarServicioEspecialOperation(this);
            realizarServicioOperation.execute();
        }
        else if(estado.equals("3"))
        {
            try {
                TextView textView = findViewById(R.id.textViewFechaValor);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date date = sdf.parse(textView.getText().toString());
                Long l = date.getTime();
                Date dateNow = new Date();
                Long lNow = dateNow.getTime();
                Long data = Math.abs(lNow - l);
                if (data <= 1.8e+6 || dateNow.after(date)) {
                    Conductor.getInstance().setServicioActual(textviewServicioValor.getText().toString());
                    Conductor.getInstance().setServicioAceptado(true);
                    this.finish();
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Falta mas de 30 minutos para el inicio del servicio", Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void desasignarServicio() {
        DesAsignarServicioEspecialOperation desAsignarServicioEspecialOperation = new DesAsignarServicioEspecialOperation(this);
        desAsignarServicioEspecialOperation.execute();

    }

}
