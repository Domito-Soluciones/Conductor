package cl.domito.conductor.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
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
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class AsignacionServicioService extends Service {

    ConstraintLayout constraintLayoutServicio;
    TextView textViewIdServicio;
    TextView textViewOrigen;
    TextView textViewDestino;
    TextView textViewTipo;
    TextView textViewNombre;
    TextView textViewDireccion;
    TextView textViewCelular;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Activity activity = (Activity) this.getApplicationContext();
        constraintLayoutServicio = activity.findViewById(R.id.constrainLayoutServicio);
        textViewIdServicio = activity.findViewById(R.id.textViewIdServicioValor);
        textViewOrigen = activity.findViewById(R.id.textViewOrigenValor);
        textViewDestino = activity.findViewById(R.id.textViewDestinoValor);
        textViewTipo = activity.findViewById(R.id.textViewTipoValor);
        textViewNombre = activity.findViewById(R.id.textViewNombreValor);
        textViewDireccion = activity.findViewById(R.id.textViewDireccionValor);
        textViewCelular = activity.findViewById(R.id.textViewCelularValor);
        Conductor conductor = Conductor.getInstance();
        String url = Utilidades.URL_BASE_SERVICIO + "GetServicioConductor.php";
        String urlDes = Utilidades.URL_BASE_SERVICIO + "ModConductorServicio.php";
        String urlMod = Utilidades.URL_BASE_MOVIL + "ModUbicacionMovil.php";
        while(conductor.isActivo()) {
            try {
                List<NameValuePair> params = new ArrayList();
                params.add(new BasicNameValuePair("user",conductor.getNick()));
                JSONObject servicio = RequestConductor.obtenerServicioAsignado(url,params);
                if(servicio != null) {
                    conductor.setServicio(servicio);
                    if(conductor.getTiempoEspera() == 0)
                    {
                        RequestConductor.desAsignarServicio(urlDes,servicio.getString("servicio_id"));
                        activity.runOnUiThread(new Runnable() {
                            @Override
                                public void run() {
                                    constraintLayoutServicio.setVisibility(View.GONE);
                                    conductor.setOcupado(false);
                                }
                            });
                            conductor.setTiempoEspera(30);
                    }
                    else
                    {
                        ActivityUtils.enviarNotificacion(activity,"Titulo",servicio.getString("servicio_id")+"",0);
                        if(constraintLayoutServicio.getVisibility() ==View.GONE) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    constraintLayoutServicio.setVisibility(View.VISIBLE);
                                    conductor.setOcupado(true);
                                    try {
                                        String partida = new String(servicio.getString("servicio_partida").getBytes("ISO-8859-1"), "UTF-8");
                                        String destino = new String(servicio.getString("servicio_destino").getBytes("ISO-8859-1"), "UTF-8");
                                        textViewIdServicio.setText(servicio.getString("servicio_id"));
                                        textViewOrigen.setText(URLDecoder.decode(partida,"ISO-8859-1"));
                                        textViewDestino.setText(URLDecoder.decode(destino,"ISO-8859-1"));
                                        textViewTipo.setText(servicio.getString("servicio_tipo"));
                                        textViewNombre.setText(servicio.getString("servicio_pasajero"));
                                        textViewDireccion.setText(servicio.getString("servicio_pasajero_direccion"));
                                        textViewCelular.setText(servicio.getString("servicio_pasajero_celular"));
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        conductor.setTiempoEspera(conductor.getTiempoEspera()-1);
                    }
                }
                if(conductor.getLocation() != null) {
                    RequestConductor.actualizarUbicacion(urlMod,conductor.getLocation());
                }
                Thread.sleep(3000);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Service.START_STICKY ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("El servicio a Terminado");
    }
}
