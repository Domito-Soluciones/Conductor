package cl.domito.dmttransfer.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.FinServicioActivity;
import cl.domito.dmttransfer.activity.MainActivity;
import cl.domito.dmttransfer.activity.PasajeroActivity;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.thread.CambiarUbicacionOperation;
import cl.domito.dmttransfer.thread.EnviarLogOperation;
import cl.domito.dmttransfer.thread.InsertarNavegacionOperation;
import cl.domito.dmttransfer.thread.NotificationOperation;
import cl.domito.dmttransfer.thread.ObtenerServiciosOperation;

public class AsignacionServicioService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String OCULTAR_LAYOUT_SERVICIO = "0";
    public static final String MOSTRAR_LAYOUT_SERVICIO = "1";
    public static final String MOSTRAR_NOTIFICACION_SERVICIO = "2";
    public static final String LLENAR_LAYOUT_SERVICIO = "3";
    public static final String CAMBIAR_UBICACION = "4";
    public static final String CALCULAR_DISTACIA = "5";
    public static boolean LAYOUT_SERVICIO_VISIBLE = false;
    public static boolean IS_INICIADO = false;
    Conductor conductor = Conductor.getInstance();

    private NotificationManager mNM;


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
        startForegroundService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (conductor.activo) {
                   try {
                        ObtenerServiciosOperation obtenerServiciosOperation = new ObtenerServiciosOperation();
                        conductor.servicios = obtenerServiciosOperation.execute().get();
                        obtenerNotificacion();
                        if(conductor.estado == 1) {
                            getUbicacion();
                        }
                        //if (conductor.getLocation() != null) {
                        if (conductor.navegando && conductor.location != null) {
                            insertarNavegacion();
                        }

                        Location location = conductor.location;
                        Location locationDestino = conductor.locationDestino;
                        if (location != null && locationDestino != null) {
                            float distancia = location.distanceTo(locationDestino);
                            if (distancia < 50f) {
                                if(conductor.servicioActual != null) {
                                    abrirActivity();
                                    conductor.locationDestino = null;
                                    if(conductor.servicioActualRuta.contains("RG"))
                                    {
                                        conductor.pasajeroRecogido = true;
                                    }
                                    else if(conductor.servicioActualRuta.contains("ZP"))
                                    {
                                        conductor.pasajeroRepartido = true;
                                    }
                                    else
                                    {
                                        conductor.pasajeroRecogido = true;
                                    }
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                       EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                       enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
                    }
                    finally {
                        try {
                            if(conductor.servicioActual == null)
                            {
                                Thread.sleep(1000);
                            }
                            else {
                                Thread.sleep(1000);
                            }
                        } catch (InterruptedException e) {
                            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
                        }
                   }
                }
            }
        });
        t.start();
        return Service.START_STICKY;
    }

    private void abrirActivity() {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P) {
            //String titulo  = "Llegaste a tu destino";
            //String contenido = "Pincha esta notificación para volver";
            //ActivityUtils.enviarNotificacionPermanente(1,this,titulo,contenido,R.drawable.furgoneta,PasajeroActivity.class);
        }
        else{
            Intent dialogIntent = new Intent(this, PasajeroActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//diferenciar
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(dialogIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        if(conductor.estado != 0) {
            Intent broadcastIntent = new Intent(this, RestartBroadcastReceived.class);
            sendBroadcast(broadcastIntent);
        }
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
        conductor.location = mLastLocation;
        }
        mGoogleApiClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
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

    private void startForegroundService()
    {
        String NOTIFICATION_CHANNEL_ID = "cl.domito.transfer";
        String channelName = "AsignacionServicioService";
        NotificationChannel chan = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        }
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(chan);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//diferenciar
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(0)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(2, notification);
    }

    private void sendMessage(String message, String value) {
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", message);
        intent.putExtra("value", value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}


