package cl.domito.conductor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.adapter.ReciclerViewProgramadoAdapter;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.Utilidades;
import cl.domito.conductor.thread.ObtenerServiciosOperation;

public class ServicioActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView imageViewAtras;
    private ImageView imageViewAtrasInt;
    private SwipeRefreshLayout swipeRefreshLayout;
    Conductor conductor;

    ConstraintLayout constraintLayoutProgramado;
    ConstraintLayout constraintLayoutDetalle;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_servicio);

            conductor = Conductor.getInstance();
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            imageViewAtras = findViewById(R.id.imageViewAtras);
            imageViewAtrasInt = findViewById(R.id.imageViewAtrasInt);
            constraintLayoutProgramado = findViewById(R.id.constrainLayoutProgramado);
            constraintLayoutDetalle = findViewById(R.id.constrainLayoutServicio);
            swipeRefreshLayout = findViewById(R.id.swiperefresh);
            JSONArray jsonArray =conductor.servicios;
            conductor.context = ServicioActivity.this;

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshContent();
                }
            });

            ArrayList<String> lista = new ArrayList();
            String ant = "";
            if(jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String servicioId = jsonObject.getString("servicio_id");
                        String servicioFecha = jsonObject.getString("servicio_fecha");
                        Date date = Utilidades.FORMAT.parse(servicioFecha);
                        String servicioHora = jsonObject.getString("servicio_hora");
                        String servicioCliente = jsonObject.getString("servicio_cliente");
                        String servicioEstado = jsonObject.getString("servicio_estado");
                        if (!servicioId.equals(ant)) {
                            lista.add(servicioId + "%" + Utilidades.FORMAT.format(date) + "%" + servicioHora + "%" + servicioCliente + "%" + servicioEstado);
                        }
                        ant = servicioId;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if(lista.size() > 0 ) {
                String[] array = new String[lista.size()];
                array = lista.toArray(array);
                mAdapter = new ReciclerViewProgramadoAdapter(this, array);
                recyclerView.setAdapter(mAdapter);
            }
            else
            {
                mAdapter = new ReciclerViewProgramadoAdapter(this, new String[0]);
                Toast.makeText(this,"No hay servicios programados",Toast.LENGTH_LONG).show();
                recyclerView.setAdapter(mAdapter);
            }
            imageViewAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    volver();
                }
            });

    }

    @Override
    protected void onResume() {
        obtenerServicos();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        conductor.volver = true;
        super.onBackPressed();
    }

    private void volver()
    {
        conductor.volver = true;
        this.finish();
    }

    private void obtenerServicos()
    {
        conductor.context = ServicioActivity.this;
        ObtenerServiciosOperation obtenerServiciosOperation = new ObtenerServiciosOperation();
        JSONArray jsonArray = null;
        try {
            jsonArray = obtenerServiciosOperation.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<String> lista = new ArrayList();
        String ant = "";
        if(jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try
                {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String servicioId = jsonObject.getString("servicio_id");
                    String servicioFecha = jsonObject.getString("servicio_fecha");
                    Date date = Utilidades.FORMAT.parse(servicioFecha);
                    String servicioHora = jsonObject.getString("servicio_hora");
                    String servicioCliente = jsonObject.getString("servicio_cliente");
                    String servicioEstado = jsonObject.getString("servicio_estado");
                    if (!servicioId.equals(ant)) {
                        lista.add(servicioId + "%" + Utilidades.FORMAT.format(date) + "%" + servicioHora + "%" + servicioCliente + "%" + servicioEstado);
                    }
                    ant = servicioId;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(lista.size() > 0 ) {
            String[] array = new String[lista.size()];
            array = lista.toArray(array);
            mAdapter = new ReciclerViewProgramadoAdapter(this, array);

            recyclerView.setAdapter(mAdapter);
        }
        else
        {
            mAdapter = new ReciclerViewProgramadoAdapter(this, new String[0]);
            Toast.makeText(this,"No hay servicios programados",Toast.LENGTH_LONG).show();
            recyclerView.setAdapter(mAdapter);
        }
    }

    private void refreshContent() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                obtenerServicos();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


}
