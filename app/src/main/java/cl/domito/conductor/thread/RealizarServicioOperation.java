package cl.domito.conductor.thread;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.ServicioActivity;
import cl.domito.conductor.activity.ServicioDetalleActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;
import cl.domito.conductor.service.AsignacionServicioService;

public class RealizarServicioOperation extends AsyncTask<Void, Void, Void> {

    private WeakReference<ServicioDetalleActivity> context;
    private TextView textView;
    Button buttonConfirmar;


    public RealizarServicioOperation(ServicioDetalleActivity activity) {
        context = new WeakReference<ServicioDetalleActivity>(activity);
        buttonConfirmar = context.get().findViewById(R.id.buttonConfirmar);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String url = Utilidades.URL_BASE_SERVICIO + "ModEstadoServicio.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        textView = context.get().findViewById(R.id.textViewIdServicioValor);

                params.add(new BasicNameValuePair("id",textView.getText().toString()));
                params.add(new BasicNameValuePair("estado","3"));
        try {
            Utilidades.enviarPost(url,params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            JSONArray servicio = Conductor.getInstance().getServicio();
            String url2 = Utilidades.URL_BASE_SERVICIO + "GetDetalleServicio.php";
            List<NameValuePair> params2 = new ArrayList();
            params2.add(new BasicNameValuePair("id", ((JSONObject)servicio.get(0)).getString("servicio_id")));
            JSONArray route = RequestConductor.getRoute(url2,params2);
            ActivityUtils.dibujarRuta(context.get(),map,route);
            //Conductor.getInstance().setServicio(null);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        return null;
    }
    @Override
    protected void onPreExecute() {
        if(context != null) {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonConfirmar.setText("En Proceso...");
                }
            });
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(context != null) {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context.get(),ServicioActivity.class);
                    intent.putExtra("idServicio",textView.getText().toString());
                    intent.putExtra("accion","0");
                    context.get().startActivity(intent);
                    context.get().finish();
                    Toast.makeText(context.get(),"Servicio aceptado",Toast.LENGTH_LONG).show();
                   //Button buttonNavegar = context.get().findViewById(R.id.buttonNavegar);
                   //buttonNavegar.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
