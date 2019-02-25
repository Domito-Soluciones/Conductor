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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.adapter.ReciclerViewDetalleAdapter;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.thread.DesAsignarServicioOperation;
import cl.domito.conductor.thread.IniciarServicioOperation;
import cl.domito.conductor.thread.RealizarServicioOperation;

public class ServicioDetalleActivity extends AppCompatActivity {

    private ImageView imageViewAtrasInt;
    private RecyclerView recyclerViewDetalle;
    private RecyclerView.Adapter mAdapterDetalle;
    private RecyclerView.LayoutManager layoutManagerDetalle;
    private Button buttonConfirmar;
    private Button buttonCancelar;
    private TextView textviewServicioValor;
    private TextView textviewFechaValor;
    private TextView textviewClienteValor;
    private TextView textviewRutaValor;
    private TextView textviewTarifaValor;
    private TextView textviewCantidadValor;
    private TextView textviewObservacionValor;
    private String estado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicio_detalle);

        recyclerViewDetalle = (RecyclerView) findViewById(R.id.recyclerViewDetalle);
        recyclerViewDetalle.setHasFixedSize(true);
        layoutManagerDetalle = new LinearLayoutManager(this);
        recyclerViewDetalle.setLayoutManager(layoutManagerDetalle);
        imageViewAtrasInt = findViewById(R.id.imageViewAtrasInt);
        buttonConfirmar = findViewById(R.id.buttonConfirmar);
        buttonCancelar = findViewById(R.id.buttonCancelar);
        textviewServicioValor = findViewById(R.id.textViewIdServicioValor);
        textviewFechaValor = findViewById(R.id.textViewFechaValor);
        textviewClienteValor = findViewById(R.id.textViewClienteValor);
        textviewRutaValor = findViewById(R.id.textViewRutaValor);
        textviewTarifaValor = findViewById(R.id.textViewTarifaValor);
        textviewCantidadValor = findViewById(R.id.textViewCantidadValor);
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
            //if(date.before(dateNow)) {
            Long data = Math.abs(lNow - l);
            //if (data <= 600000) {
            Conductor conductor = Conductor.getInstance();
                try {
                    int cantidad = 0;
                    ArrayList<String> lista = new ArrayList();
                    for(int i =  0; i < conductor.getServicios().length();i++ ) {
                        JSONObject servicio = conductor.getServicios().getJSONObject(i);
                        if(servicio.getString("servicio_id").equals(getIntent().getExtras().getString("id"))) {
                            textviewServicioValor.setText(servicio.getString("servicio_id"));
                            textviewFechaValor.setText(servicio.getString("servicio_fecha") + " " + servicio.getString("servicio_hora"));
                            textviewClienteValor.setText(servicio.getString("servicio_cliente"));
                            textviewRutaValor.setText(servicio.getString("servicio_ruta"));
                            textviewTarifaValor.setText(servicio.getString("servicio_tarifa"));
                            textviewObservacionValor.setText(servicio.getString("servicio_observacion").equals("")?"Sin observaciones":servicio.getString("servicio_observacion"));
                            cantidad++;
                            estado = servicio.getString("servicio_estado");
                            String nombre = servicio.getString("servicio_pasajero_nombre");
                            String celular = servicio.getString("servicio_pasajero_celular");
                            String destino = servicio.getString("servicio_destino");
                            lista.add(nombre + "%"+celular + "%" + destino);
                        }
                    }
                    textviewCantidadValor.setText(cantidad+"");
                    Conductor.getInstance().setCantidadPasajeros(cantidad);
                    if(lista.size() > 0 ) {
                        String[] array = new String[lista.size()];
                        array  = lista.toArray(array);
                        ReciclerViewDetalleAdapter mAdapter = new ReciclerViewDetalleAdapter(this,array);
                        recyclerViewDetalle.setAdapter(mAdapter);
                        conductor.setOcupado(true);
                    }
                    else
                    {
                        Toast.makeText(this,"No hay servicios en el historial",Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(this,ServicioActivity.class);
        startActivity(intent);
    }

    private void aceptarServicio() {
        if(estado.equals("1")) {
            RealizarServicioOperation realizarServicioOperation = new RealizarServicioOperation(this);
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
        DesAsignarServicioOperation desAsignarServicioOperation = new DesAsignarServicioOperation(this);
        desAsignarServicioOperation.execute();

    }




}
