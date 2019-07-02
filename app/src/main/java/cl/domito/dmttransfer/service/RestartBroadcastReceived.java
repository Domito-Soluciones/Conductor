package cl.domito.dmttransfer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import cl.domito.dmttransfer.dominio.Conductor;

public class RestartBroadcastReceived extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(RestartBroadcastReceived.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, AsignacionServicioService.class));
        } else {
            context.startService(new Intent(context, AsignacionServicioService.class));
        }
    }
}
