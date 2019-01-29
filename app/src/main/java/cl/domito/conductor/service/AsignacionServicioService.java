package cl.domito.conductor.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONObject;

import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.thread.CambiarUbicacionOperation;
import cl.domito.conductor.thread.DesAsignarServicioOperation;
import cl.domito.conductor.thread.ObtenerServicioOperation;

public class AsignacionServicioService extends Service {

    public static final String OCULTAR_LAYOUT_SERVICIO = "0";
    public static final String MOSTRAR_LAYOUT_SERVICIO = "1";
    public static final String MOSTRAR_NOTIFICACION_SERVICIO= "2";
    public static final String LLENAR_LAYOUT_SERVICIO= "3";
    public static final String CAMBIAR_UBICACION= "4";
    public static boolean LAYOUT_SERVICIO_VISIBLE = false;
    public static boolean IS_INICIADO = false;

    public AsignacionServicioService(Context applicationContext) {
        super();
    }

    public AsignacionServicioService() {
    }


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
                while (IS_INICIADO) {
                    while (conductor.isActivo()) {
                        Log.i("I","servicio corriendo correctmente");
                        try {
                            ObtenerServicioOperation obtenerServicioOperation = new ObtenerServicioOperation();
                            JSONObject servicio = obtenerServicioOperation.execute().get();
                            if (servicio.length() > 0) {
                                conductor.setServicio(servicio);
                                if (conductor.getTiempoEspera() == 0) {
                                    DesAsignarServicioOperation desAsignarServicioOperation = new DesAsignarServicioOperation();
                                    desAsignarServicioOperation.execute();
                                    sendMessage(OCULTAR_LAYOUT_SERVICIO, null);
                                    LAYOUT_SERVICIO_VISIBLE = true;
                                    conductor.setOcupado(false);
                                    conductor.setTiempoEspera(30);
                                } else {
                                    sendMessage(MOSTRAR_NOTIFICACION_SERVICIO, servicio.getString("servicio_id"));
                                    if (!LAYOUT_SERVICIO_VISIBLE) {
                                        sendMessage(MOSTRAR_LAYOUT_SERVICIO, null);
                                        conductor.setOcupado(true);
                                        sendMessage(LLENAR_LAYOUT_SERVICIO, servicio.toString());
                                    }
                                    conductor.setTiempoEspera(conductor.getTiempoEspera() - 1);
                                }
                            }
                            if (conductor.getLocation() != null) {
                                CambiarUbicacionOperation cambiarUbicacionOperation = new CambiarUbicacionOperation();
                                cambiarUbicacionOperation.execute();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                    while (!conductor.isActivo() && IS_INICIADO) {
                        try {
                            System.out.println("servicio a la espera");
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
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
        Intent broadcastIntent = new Intent(this, RestartBroadcastReceived.class);
        sendBroadcast(broadcastIntent);
    }

    private void sendMessage(String message,String value) {
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", message);
        intent.putExtra("value", value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
