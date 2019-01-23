package cl.domito.conductor.thread;

import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class RealizarServicioOperation extends AsyncTask<Object, Void, Void> {

    private WeakReference<MapsActivity> context;
    private TextView textView;
    private TextView textViewError;
    private ConstraintLayout servicioLayout;


    public RealizarServicioOperation(MapsActivity activity) {
        context = new WeakReference<MapsActivity>(activity);
    }

    @Override
    protected Void doInBackground(Object... objects) {
        GoogleMap map = (GoogleMap) objects[0];
        String url = Utilidades.URL_BASE_SERVICIO + "AddServicio.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        textView = context.get().findViewById(R.id.textView4);
        servicioLayout = context.get().findViewById(R.id.constrainLayoutServicio);
        params.add(new BasicNameValuePair("id",textView.getText().toString()));
        Utilidades.enviarPost(url,params);
        Conductor.getInstance().setTiempoEspera(30);
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (servicioLayout.getVisibility() == View.VISIBLE) {
                    servicioLayout.setVisibility(View.GONE);
                    //Utilidades.GONE = true;
                }
                JSONObject servicio = Conductor.getInstance().getServicio();
                try {
                    String url = Utilidades.URL_BASE_SERVICIO + "GetDetalleServicio.php";
                    List<NameValuePair> params = new ArrayList();
                    params.add(new BasicNameValuePair("id", servicio.getString("servicio_id")));
                    JSONObject route = RequestConductor.getRoute(url,params);
                    ActivityUtils.dibujarRuta(context.get(),map,route);
                    Conductor.getInstance().setServicio(null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return null;
    }
}
