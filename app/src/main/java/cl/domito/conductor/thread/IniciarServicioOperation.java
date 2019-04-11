package cl.domito.conductor.thread;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MainActivity;
import cl.domito.conductor.activity.PasajeroActivity;
import cl.domito.conductor.activity.adapter.ReciclerViewPasajeroAdapter;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;

public class IniciarServicioOperation extends AsyncTask<Void, Void, String> {

    private WeakReference<Activity> context;
    ConstraintLayout constraintLayoutPasajero;
    ConstraintLayout constraintLayoutEstado;


    public IniciarServicioOperation(Activity activity) {
        context = new WeakReference<Activity>(activity);
        constraintLayoutPasajero = context.get().findViewById(R.id.constrainLayoutPasajero);
        constraintLayoutEstado = context.get().findViewById(R.id.constrainLayoutEstado);
    }

    @Override
    protected String doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        String idServicio = conductor.servicioActual;
        RecyclerView recyclerView = context.get().findViewById(R.id.recyclerViewPasajero);
        final RecyclerView.LayoutManager[] layoutManager = new RecyclerView.LayoutManager[1];
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.setHasFixedSize(true);
                layoutManager[0] = new LinearLayoutManager(context.get());
                recyclerView.setLayoutManager(layoutManager[0]);
            }
        });
        ArrayList<String> lista = new ArrayList();
        RequestConductor.obtenerServicioProgramados(idServicio);
        if(conductor.servicio != null) {
            try {
                JSONObject primero = conductor.servicio.getJSONObject(0);
                String ruta = primero.getString("servicio_truta").split("-")[0];
                if (primero.getString("servicio_estado").equals("4"))
                {
                    conductor.zarpeIniciado = true;
                }
                if ((ruta.equals("ZP") && !conductor.zarpeIniciado) ) {
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
        if(conductor.servicio != null) {
            for (int i = 0; i < conductor.servicio.length(); i++) {
                try {
                    JSONObject servicio = conductor.servicio.getJSONObject(i);
                    if (servicio.getString("servicio_id").equals(idServicio)) {
                        String id = servicio.getString("servicio_pasajero_id");
                        String nombre = servicio.getString("servicio_pasajero_nombre");
                        String celular = servicio.getString("servicio_pasajero_celular");
                        String destino = servicio.getString("servicio_destino");
                        String estado = servicio.getString("servicio_pasajero_estado");
                        if (servicio.getString("servicio_truta").contains("ZP")) {
                            if (!estado.equals("3") && !estado.equals("2")) {
                                lista.add(nombre + "%" + celular + "%" + destino + "%" + estado + "%" + id);
                            }
                        } else if (servicio.getString("servicio_truta").contains("RG")) {
                            if (!estado.equals("3") && !estado.equals("2") && !estado.equals("1")) {
                                lista.add(nombre + "%" + celular + "%" + destino + "%" + estado + "%" + id);
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(conductor.servicio != null) {
            try {
                JSONObject ultimo = conductor.servicio.getJSONObject(conductor.servicio.length() - 1);
                String ruta = ultimo.getString("servicio_truta").split("-")[0];
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
            array  = lista.toArray(array);
            ReciclerViewPasajeroAdapter mAdapter = new ReciclerViewPasajeroAdapter(context.get(),array);
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setAdapter(mAdapter);
                }
            });
        }
        return idServicio;
    }
    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String aString) {
        if(aString != null) {
            CambiarMovilOperation cambiarMovilOperation = new CambiarMovilOperation();
            cambiarMovilOperation.execute(aString);
        }
    }
}
