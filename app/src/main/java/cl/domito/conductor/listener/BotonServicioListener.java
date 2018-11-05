package cl.domito.conductor.listener;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class BotonServicioListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button2)
        {
            String url = Utilidades.URL_BASE_SERVICIO + "RealizarServicio.php";
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
                                partida = URLDecoder.decode(servicio.getString("servicio_partida"),"UTF-8");
                                destino = URLDecoder.decode(servicio.getString("servicio_destino"),"UTF-8");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            ActivityUtils.dibujarRuta(partida,destino);
                            Utilidades.SERVICIO = null;
                        }
                    });
                }
            });
            thread.start();
        }
        else if(v.getId() == R.id.button4)
        {
            String url = Utilidades.URL_BASE_SERVICIO + "DesAsignarServicio.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id",Utilidades.USER));
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Utilidades.enviarPost(url,params);
                    Utilidades.TIEMPO_ESPERA = 30;
                }
            });
            thread.start();

        }
        else if(v.getId() == R.id.button5)
        {
                //String dial = "tel:"+R.id.textView3;
                String dial = "tel:995970988";
                MapsActivity.mapsActivity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));

        }
    }
}
