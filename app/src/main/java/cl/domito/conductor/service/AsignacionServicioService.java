package cl.domito.conductor.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
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
import cl.domito.conductor.thread.CambiarUbicacionOperation;
import cl.domito.conductor.thread.DesAsignarServicioOperation;
import cl.domito.conductor.thread.ObtenerServicioOperation;

public class AsignacionServicioService extends Service {

    public static String OCULTAR_LAYOUT_SERVICIO = "0";
    public static String MOSTRAR_LAYOUT_SERVICIO = "1";
    public static boolean LAYOUT_SERVICIO_VISIBLE = false;

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
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Conductor conductor = Conductor.getInstance();
                while(conductor.isActivo()) {
                    try {
                        ObtenerServicioOperation obtenerServicioOperation = new ObtenerServicioOperation();
                        JSONObject servicio = obtenerServicioOperation.execute().get();
                        if(servicio != null) {
                            conductor.setServicio(servicio);
                            if(conductor.getTiempoEspera() == 0)
                            {
                                DesAsignarServicioOperation desAsignarServicioOperation = new DesAsignarServicioOperation();
                                desAsignarServicioOperation.execute();
                                sendMessage(OCULTAR_LAYOUT_SERVICIO);
                                LAYOUT_SERVICIO_VISIBLE = true;
                                conductor.setOcupado(false);
                                conductor.setTiempoEspera(30);
                            }
                            else
                            {
                                //ActivityUtils.enviarNotificacion(activity,"Titulo",servicio.getString("servicio_id")+"",0);
                                if(!LAYOUT_SERVICIO_VISIBLE) {
                                    sendMessage(MOSTRAR_LAYOUT_SERVICIO);
                                    conductor.setOcupado(true);
                                    try {
                                        String partida = new String(servicio.getString("servicio_partida").getBytes("ISO-8859-1"), "UTF-8");
                                        String destino = new String(servicio.getString("servicio_destino").getBytes("ISO-8859-1"), "UTF-8");
                                /*textViewIdServicio.setText(servicio.getString("servicio_id"));
                                textViewOrigen.setText(URLDecoder.decode(partida,"ISO-8859-1"));
                                textViewDestino.setText(URLDecoder.decode(destino,"ISO-8859-1"));
                                textViewTipo.setText(servicio.getString("servicio_tipo"));
                                textViewNombre.setText(servicio.getString("servicio_pasajero"));
                                textViewDireccion.setText(servicio.getString("servicio_pasajero_direccion"));
                                textViewCelular.setText(servicio.getString("servicio_pasajero_celular"));*/
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                                conductor.setTiempoEspera(conductor.getTiempoEspera()-1);
                            }
                        }
                        if(conductor.getLocation() != null) {
                            CambiarUbicacionOperation cambiarUbicacionOperation = new CambiarUbicacionOperation();
                            cambiarUbicacionOperation.execute();
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(1000);
                    }
                    catch(InterruptedException e)
                    {}
                }
            }
        });
        t.start();

        return Service.START_STICKY ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("El servicio a Terminado");
    }

    private void sendMessage(String message) {
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
