package cl.domito.conductor.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.thread.CambiarUbicacionOperation;
import cl.domito.conductor.thread.InsertarNavegacionOperation;
import cl.domito.conductor.thread.NotificationOperation;
import cl.domito.conductor.thread.ObtenerServicioOperation;

public class AsignacionServicioService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String OCULTAR_LAYOUT_SERVICIO = "0";
    public static final String MOSTRAR_LAYOUT_SERVICIO = "1";
    public static final String MOSTRAR_NOTIFICACION_SERVICIO = "2";
    public static final String LLENAR_LAYOUT_SERVICIO = "3";
    public static final String CAMBIAR_UBICACION = "4";
    public static final String CALCULAR_DISTACIA = "5";
    public static boolean LAYOUT_SERVICIO_VISIBLE = false;
    public static boolean IS_INICIADO = false;
    public static boolean TERMINAR = true;


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
                        Log.i("I", "servicio corriendo correctmente");
                        try {
                            ObtenerServicioOperation obtenerServicioOperation = new ObtenerServicioOperation();
                            conductor.setServicios(obtenerServicioOperation.execute().get());
                            obtenerNotificacion();
                            getUbicacion();
                            //if (conductor.getLocation() != null) {
                            if (Conductor.getInstance().isNavegando() && Conductor.getInstance().getLocation() != null) {
                                insertarNavegacion();
                            }
                            Location location = Conductor.getInstance().getLocation();
                            Location locationDestino = Conductor.getInstance().getLocationDestino();
                            if (location != null && locationDestino != null) {
                                float distancia = location.distanceTo(locationDestino);
                                System.out.println("esta es distancia " + distancia + " -------------------------------------");
                                if (distancia < 50f) {
                                    abrirActivity();
                                }
                            }
                            //}
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
        return Service.START_STICKY;
    }

    private void abrirActivity() {
        Intent dialogIntent = new Intent(this, MapsActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//diferenciar
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(dialogIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("El servicio a Terminado");
        if (TERMINAR) {
            Intent broadcastIntent = new Intent(this, RestartBroadcastReceived.class);
            sendBroadcast(broadcastIntent);
        }
    }

    private void sendMessage(String message, String value) {
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", message);
        intent.putExtra("value", value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void obtenerNotificacion() {
        NotificationOperation notificationOperation = new NotificationOperation(this);
        notificationOperation.execute();
    }

    private void insertarNavegacion() {
        InsertarNavegacionOperation insertarNavegacionOperation = new InsertarNavegacionOperation();
        insertarNavegacionOperation.execute();
    }

    private void getUbicacion() throws InterruptedException {
        CambiarUbicacionOperation cambiarUbicacionOperation = new CambiarUbicacionOperation();
        cambiarUbicacionOperation.execute();
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        Thread.sleep(500);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                System.out.println("la wea esta wena");
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(AsignacionServicioService.this.getApplicationContext(),"la wea este wena",Toast.LENGTH_SHORT).show();
                    }
                });
                Conductor.getInstance().setLocation(mLastLocation);
            } else {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(AsignacionServicioService.this.getApplicationContext(),"la wea este mala",Toast.LENGTH_SHORT).show();
                    }
                });
                System.out.println("la wea esta mala");
            }
            mGoogleApiClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


