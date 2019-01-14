package cl.domito.conductor.thread;

import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import cl.domito.conductor.activity.LoginActivity;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class AsignacionOperation extends AsyncTask<Void,Void,Void> {

    private WeakReference<LoginActivity> context;
    private ConstraintLayout servicioLayout;
    private TextView textViewNServicio;
    private TextView textViewOrigen;
    private TextView textViewDestino;
    private TextView textViewTipo;
    private TextView textViewNombre;
    private TextView textViewDireccion;
    private TextView textViewCelular;

    public AsignacionOperation(LoginActivity activity) {
        context = new WeakReference<LoginActivity>(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        servicioLayout = context.get().findViewById(R.id.relativeLayout5);
        textViewNServicio = context.get().findViewById(R.id.textView4);
        textViewOrigen = context.get().findViewById(R.id.textView7);
        textViewDestino = context.get().findViewById(R.id.textView10);
        textViewTipo = context.get().findViewById(R.id.textView12);
        textViewNombre = context.get().findViewById(R.id.textView14);
        textViewDireccion = context.get().findViewById(R.id.textView16);
        textViewCelular = context.get().findViewById(R.id.textView18);
        String url = Utilidades.URL_BASE_SERVICIO + "ServiciosAsignado.php";
        String urlDes = Utilidades.URL_BASE_SERVICIO + "DesAsignarServicio.php";
        String urlMod = Utilidades.URL_BASE_CONDUCTOR + "ModificarUbicacion.php";
        while(true) {
            if(conductor.isActivo())
            {
                Log.i("EJECUTANDO THREAD","EJECUTANDO THREAD");
                try
                {
                    List<NameValuePair> params = new ArrayList();
                    params.add(new BasicNameValuePair("user",conductor.getNick()));
                    JSONObject servicio = RequestConductor.obtenerServicioAsignado(url,params);
                    if(servicio != null && !servicio.getString("servicio_id").equals("")) {
                        if(conductor.getServicio() == null) {
                            conductor.setServicio(servicio);
                        }
                        Log.i("Servicio asignado",servicio.getString("servicio_id"));
                        if(conductor.getTiempoEspera() == 0)
                        {
                            RequestConductor.desAsignarServicio(urlDes,servicio.getString("servicio_id"));
                            context.get().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    servicioLayout.setVisibility(View.GONE);
                                    //Utilidades.GONE = true;
                                }
                            });
                            conductor.setTiempoEspera(30);
                        }
                        else
                        {
                            ActivityUtils.enviarNotificacion(context.get(),"Titulo",servicio.getString("servicio_id")+"",0);
                            if(Utilidades.GONE) {
                                context.get().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        servicioLayout.setVisibility(View.VISIBLE);
                                        //Utilidades.GONE = false;
                                        try {
                                            String partida = new String(servicio.getString("servicio_partida").getBytes("ISO-8859-1"), "UTF-8");
                                            String destino = new String(servicio.getString("servicio_destino").getBytes("ISO-8859-1"), "UTF-8");
                                            textViewNServicio.setText(servicio.getString("servicio_id"));
                                            textViewOrigen.setText(URLDecoder.decode(partida,"ISO-8859-1"));
                                            textViewDestino.setText(URLDecoder.decode(destino,"ISO-8859-1"));
                                            textViewTipo.setText(servicio.getString("servicio_tipo"));
                                            textViewNombre.setText(servicio.getString("servicio_pasajero"));
                                            textViewDireccion.setText(servicio.getString("servicio_pasajero_direccion"));
                                            textViewCelular.setText(servicio.getString("servicio_pasajero_celular"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                            conductor.setTiempoEspera(conductor.getTiempoEspera()-1);
                            Log.i("tirmpo restante",conductor.getTiempoEspera()+"");
                        }
                    }
                    if(conductor.getLocation() != null) {
                        RequestConductor.actualizarUbicacion(urlMod,conductor.getLocation());
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
}
