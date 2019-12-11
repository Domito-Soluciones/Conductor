package cl.domito.dmttransfer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.adapter.ReciclerViewDetalleAdapter;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.Utilidades;
import cl.domito.dmttransfer.thread.EnviarLogOperation;
import cl.domito.dmttransfer.thread.ObtenerServicioHistoricoOperation;

public class HistoricoDetalleActivity extends AppCompatActivity {

    private ImageView imageViewAtrasInt;
    private RecyclerView recyclerViewDetalle;
    private RecyclerView.LayoutManager layoutManagerDetalle;
    private TextView textviewServicioValor;
    private TextView textviewFechaValor;
    private TextView textviewClienteValor;
    private TextView textviewRutaValor;
    private TextView textviewTarifaValor;
    private TextView textviewCantidadValor;
    private TextView textviewObservacionValor;
    private Conductor conductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_detalle);

        recyclerViewDetalle = (RecyclerView) findViewById(R.id.recyclerViewDetalle);
        recyclerViewDetalle.setHasFixedSize(true);
        layoutManagerDetalle = new LinearLayoutManager(this);
        recyclerViewDetalle.setLayoutManager(layoutManagerDetalle);
        imageViewAtrasInt = findViewById(R.id.imageViewAtrasInt);
        textviewServicioValor = findViewById(R.id.textViewIdServicioValor);
        textviewFechaValor = findViewById(R.id.textViewFechaValor);
        textviewClienteValor = findViewById(R.id.textViewClienteValor);
        textviewRutaValor = findViewById(R.id.textViewRutaValor);
        textviewTarifaValor = findViewById(R.id.textViewTarifaValor);
        textviewCantidadValor = findViewById(R.id.textViewCantidadValor);
        textviewObservacionValor = findViewById(R.id.textViewObservacionValor);

        conductor = Conductor.getInstance();

        conductor.context = HistoricoDetalleActivity.this;

        imageViewAtrasInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        obtenerHistorico();
        super.onPostCreate(savedInstanceState);
    }

    private void volver() {
        this.finish();
    }

    private void obtenerHistorico() {
        ObtenerServicioHistoricoOperation obtenerServicioHistoricoOperation = new ObtenerServicioHistoricoOperation();
        try
        {
            String idServicio = getIntent().getExtras().getString("idServicio");
            JSONArray historico = obtenerServicioHistoricoOperation.execute(idServicio).get();
            int cantidad = 0;
            ArrayList<String> lista = new ArrayList();
            for (int i = 0; i < historico.length(); i++) {
                JSONObject servicio = historico.getJSONObject(i);
                if (i == 0) {
                    textviewServicioValor.setText(servicio.getString("servicio_id"));
                    textviewFechaValor.setText(servicio.getString("servicio_fecha") + " " + servicio.getString("servicio_hora"));
                    textviewClienteValor.setText(servicio.getString("servicio_cliente"));
                    textviewRutaValor.setText(servicio.getString("servicio_ruta"));
                    textviewTarifaValor.setText("$ "+ Utilidades.formatoMoneda(servicio.getString("servicio_tarifa")));
                    textviewObservacionValor.setText(servicio.getString("servicio_observacion").equals("") ? "Sin observaciones" : servicio.getString("servicio_observacion"));
                }
                String nombre = servicio.getString("servicio_pasajero_nombre");
                String celular = servicio.getString("servicio_pasajero_celular");
                String destino = servicio.getString("servicio_destino");
                if(!destino.trim().equals("")) {
                    cantidad++;
                    if (nombre.trim().equals("")) {
                        String aux = servicio.getString("servicio_pasajero_id_pasajero");
                        String[] data = aux.split("-");
                        nombre = data[0];
                        if (data.length > 1) {
                            celular = data[1];
                        }
                    }

                    lista.add(nombre + "%" + celular + "%" + destino);
                }
            }
            textviewCantidadValor.setText(cantidad + "");
            if (lista.size() > 0) {
                String[] array = new String[lista.size()];
                array = lista.toArray(array);
                ReciclerViewDetalleAdapter mAdapter = new ReciclerViewDetalleAdapter(this, array);
                recyclerViewDetalle.setAdapter(mAdapter);
            }

        } catch (Exception e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
        }
    }

}
