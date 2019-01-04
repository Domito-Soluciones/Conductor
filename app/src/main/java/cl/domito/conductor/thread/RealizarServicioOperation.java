package cl.domito.conductor.thread;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class RealizarServicioOperation extends AsyncTask<Void, Void, Void> {

    private WeakReference<MapsActivity> context;
    TextView textViewError;

    public RealizarServicioOperation(MapsActivity activity) {
        context = new WeakReference<MapsActivity>(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String url = Utilidades.URL_BASE_SERVICIO + "AddServicio.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        TextView textView = MapsActivity.mapsActivity.findViewById(R.id.textView4);
        params.add(new BasicNameValuePair("id",textView.getText().toString()));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Utilidades.enviarPost(url,params);
                Utilidades.TIEMPO_ESPERA = 30;
                MapsActivity.mapsActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(MapsActivity.servicioLayout.getVisibility() == View.VISIBLE) {
                            MapsActivity.servicioLayout.setVisibility(View.GONE);
                            Utilidades.GONE = true;
                        }
                        JSONObject servicio = Utilidades.SERVICIO;
                        String partida = null;
                        String destino = null;
                        try {
                            partida = new String(servicio.getString("servicio_partida").getBytes("ISO-8859-1"), "UTF-8");
                            destino = new String(servicio.getString("servicio_destino").getBytes("ISO-8859-1"), "UTF-8");
                            ActivityUtils.dibujarRuta(URLDecoder.decode(partida,"ISO-8859-1"),URLDecoder.decode(destino,"ISO-8859-1"));
                            Utilidades.SERVICIO = null;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        thread.start();
    }
}
