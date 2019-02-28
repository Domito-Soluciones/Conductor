package cl.domito.conductor.thread;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.ServicioActivity;
import cl.domito.conductor.activity.ServicioDetalleActivity;
import cl.domito.conductor.activity.adapter.ReciclerViewDetalleAdapter;
import cl.domito.conductor.activity.adapter.ReciclerViewPasajeroAdapter;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;
import okhttp3.internal.Util;

public class IniciarServicioOperation extends AsyncTask<Object, Void, String> {

    private WeakReference<MapsActivity> context;
    private ProgressBar progressBar;
    ConstraintLayout constraintLayoutPasajero;
    ConstraintLayout constraintLayoutEstado;


    public IniciarServicioOperation(MapsActivity activity) {
        context = new WeakReference<MapsActivity>(activity);
        progressBar = context.get().findViewById(R.id.progressBarGeneral);
        constraintLayoutPasajero = context.get().findViewById(R.id.constraitLayoutPasajero);
        constraintLayoutEstado = context.get().findViewById(R.id.constrainLayoutEstado);
    }

    @Override
    protected String doInBackground(Object... objects) {
        GoogleMap map = (GoogleMap) objects[0];
        String idServicio = (String) objects[1];
        String url2 = Utilidades.URL_BASE_SERVICIO + "GetDetalleServicio.php";
        List<NameValuePair> params2 = new ArrayList();
        params2.add(new BasicNameValuePair("id", idServicio));
        JSONArray route = RequestConductor.getRoute(url2,params2);
        ActivityUtils.dibujarRuta(context.get(),map,route);
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
        Conductor conductor = Conductor.getInstance();
        String url3 = Utilidades.URL_BASE_SERVICIO + "GetServicioProgramado.php";
        List<NameValuePair> params3 = new ArrayList();
        params3.add(new BasicNameValuePair("id",idServicio));
        params3.add(new BasicNameValuePair("conductor",Conductor.getInstance().getNick()));
        conductor.setServicio(Utilidades.enviarPostArray(url3, params3));
        if(conductor.getServicios() != null) {
            try {
                JSONObject primero = conductor.getServicios().getJSONObject(0);
                String ruta = primero.getString("servicio_ruta").split("-")[1];
                if (ruta.equals("ZP")) {
                    String cliente = primero.getString("servicio_cliente");
                    String destino = primero.getString("servicio_cliente_direccion");
                    lista.add(cliente + "%%" + destino);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        for(int i =  0; i < conductor.getServicio().length();i++ ) {
            try {
                JSONObject servicio = conductor.getServicios().getJSONObject(i);
                if (servicio.getString("servicio_id").equals(idServicio)) {
                    String id = servicio.getString("servicio_pasajero_id");
                    String nombre = servicio.getString("servicio_pasajero_nombre");
                    String celular = servicio.getString("servicio_pasajero_celular");
                    String destino = servicio.getString("servicio_destino");
                    lista.add(nombre + "%" + celular + "%" + destino + "%" + id);
                }
            }
            catch(Exception e){e.printStackTrace();}
        }
        if(conductor.getServicios() != null) {
            try {
                JSONObject ultimo = conductor.getServicios().getJSONObject(conductor.getServicios().length() - 1);
                String ruta = ultimo.getString("servicio_ruta").split("-")[1];
                if (ruta.equals("RG")) {
                    String cliente = ultimo.getString("servicio_cliente");
                    String destino = ultimo.getString("servicio_cliente_direccion");
                    lista.add(cliente + "%%" + destino);
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
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                constraintLayoutPasajero.setVisibility(View.VISIBLE);
            }
        });
        return idServicio;
    }
    @Override
    protected void onPreExecute() {
        if(context != null) {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                  //  buttonConfirmar.setText("En Proceso...");
                }
            });
        }
    }

    @Override
    protected void onPostExecute(String aString) {
         }
}
