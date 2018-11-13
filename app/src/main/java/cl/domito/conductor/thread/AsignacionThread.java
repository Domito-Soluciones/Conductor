package cl.domito.conductor.thread;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;
import cl.domito.conductor.listener.MyMapReadyCallBack;

public  class AsignacionThread extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] objects) {
        String url = Utilidades.URL_BASE_SERVICIO + "ServiciosAsignado.php?user="+Utilidades.USER;
        String urlDes = Utilidades.URL_BASE_SERVICIO + "DesAsignarServicio.php";
        String urlMod = Utilidades.URL_BASE_CONDUCTOR + "ModificarUbicacion.php";
        while(true) {
            if(Utilidades.CONDUCTOR_ACTIVO)
            {
                Log.i("EJECUTANDO THREAD","EJECUTANDO THREAD");
                try {

                    JSONObject servicio = RequestConductor.obtenerServicioAsignado(url);
                    if(!servicio.getString("servicio_id").equals("")) {
                        if(Utilidades.SERVICIO == null) {
                            Utilidades.SERVICIO = servicio;
                        }
                        Log.i("Servicio asignado",servicio.getString("servicio_id"));
                        if(Utilidades.TIEMPO_ESPERA == 0)
                        {
                            RequestConductor.desAsignarServicio(urlDes,servicio.getString("servicio_id"));
                            MapsActivity.mapsActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MapsActivity.servicioLayout.setVisibility(View.GONE);
                                    Utilidades.GONE = true;
                                }
                            });
                            Utilidades.TIEMPO_ESPERA = 30;
                        }
                        else {
                            ActivityUtils.enviarNotificacion(servicio.getString("servicio_id"));
                            if(Utilidades.GONE) {
                                MapsActivity.mapsActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MapsActivity.servicioLayout.setVisibility(View.VISIBLE);
                                        Utilidades.GONE = false;
                                        try {
                                            MapsActivity.textViewNServicio.setText(servicio.getString("servicio_id"));
                                            MapsActivity.textViewOrigen.setText(URLDecoder.decode(servicio.getString("servicio_partida"),"UTF-8"));
                                            MapsActivity.textViewDestino.setText(URLDecoder.decode(servicio.getString("servicio_destino"),"UTF-8"));
                                            MapsActivity.textViewTipo.setText(servicio.getString("servicio_tipo"));
                                            MapsActivity.textViewNombre.setText(servicio.getString("servicio_pasajero"));
                                            MapsActivity.textViewDireccion.setText(servicio.getString("servicio_pasajero_direccion"));
                                            MapsActivity.textViewCelular.setText(servicio.getString("servicio_pasajero_celular"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                            Utilidades.TIEMPO_ESPERA--;
                            Log.i("tirmpo restante",Utilidades.TIEMPO_ESPERA+"");
                        }
                    }
                    if(MyMapReadyCallBack.lastLocation != null) {
                        RequestConductor.actualizarUbicacion(urlMod,MyMapReadyCallBack.lastLocation);
                    }
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                break;
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {

    }
    @Override
    protected void onCancelled() {

    }
}