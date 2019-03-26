package cl.domito.conductor.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.JSONObject;

import java.util.ArrayList;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.adapter.ReciclerViewPasajeroAdapter;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.thread.IniciarServicioOperation;
import cl.domito.conductor.thread.ObtenerServicioOperation;

public class PasajeroActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasajero);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        recyclerView = this.findViewById(R.id.recyclerViewPasajero);
        IniciarServicioOperation iniciarServicioOperation = new IniciarServicioOperation(this);
        iniciarServicioOperation.execute();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

    }


    @Override
    protected void onResume() {
        Conductor conductor = Conductor.getInstance();
        if(conductor.isNavegando())
        {
            recargarPasajeros();
            conductor.setNavegando(false);
        }
        super.onResume();
    }

    private void refreshContent() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                recargarPasajeros();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void recargarPasajeros()
    {
        Conductor conductor = Conductor.getInstance();
        ArrayList<String> lista = new ArrayList();
        ArrayList<String> listaFinalizados = new ArrayList();
        String idServicio = conductor.getServicioActual();
        try {
            ObtenerServicioOperation obtenerServicioOperation = new ObtenerServicioOperation();
            conductor.setServicio(obtenerServicioOperation.execute().get());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(conductor.getServicio() != null) {
            try {
                JSONObject primero = conductor.getServicio().getJSONObject(0);
                String ruta = primero.getString("servicio_ruta").split("-")[1];
                if (ruta.equals("ZP")) {
                    String cliente = primero.getString("servicio_cliente");
                    String destino = primero.getString("servicio_cliente_direccion");
                    lista.add(cliente + "%%" + destino + "%0%0");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        for(int i =  0; i < conductor.getServicio().length();i++ ) {
            try {
                JSONObject servicio = conductor.getServicio().getJSONObject(i);
                if (servicio.getString("servicio_id").equals(idServicio)) {
                    String id = servicio.getString("servicio_pasajero_id");
                    String nombre = servicio.getString("servicio_pasajero_nombre");
                    String celular = servicio.getString("servicio_pasajero_celular");
                    String destino = servicio.getString("servicio_destino");
                    String estado = servicio.getString("servicio_pasajero_estado");
                    System.out.println("este es el estado: " + estado + " del pasajero " + nombre);
                    if (estado.equals("3"))
                    {
                        listaFinalizados.add(nombre + "%" + celular + "%" + destino + "%" + estado + "%" + id);
                    }
                    else
                    {
                        lista.add(nombre + "%" + celular + "%" + destino + "%" + estado + "%" + id);
                    }
                }
            }
            catch(Exception e){e.printStackTrace();}
        }
        if(conductor.getServicio() != null) {
            try {
                JSONObject ultimo = conductor.getServicio().getJSONObject(conductor.getServicio().length() - 1);
                String ruta = ultimo.getString("servicio_ruta").split("-")[1];
                if (ruta.equals("RG")) {
                    String cliente = ultimo.getString("servicio_cliente");
                    String destino = ultimo.getString("servicio_cliente_direccion");
                    lista.add(cliente + "%%" + destino + "%0%0");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if(lista.size() > 0 ) {
            String[] array = new String[lista.size()];
            lista.addAll(listaFinalizados);
            array  = lista.toArray(array);
            ReciclerViewPasajeroAdapter mAdapter = new ReciclerViewPasajeroAdapter(this,array);
            recyclerView.setAdapter(mAdapter);
        }
    }

}
