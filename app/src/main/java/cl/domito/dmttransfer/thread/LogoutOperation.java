package cl.domito.dmttransfer.thread;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import cl.domito.dmttransfer.activity.LoginActivity;
import cl.domito.dmttransfer.activity.MainActivity;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.RequestConductor;
import cl.domito.dmttransfer.service.AsignacionServicioService;

public class LogoutOperation extends AsyncTask<String, Void, Void> {

    WeakReference<MainActivity> context;
    Conductor conductor;

    public LogoutOperation(MainActivity activity) {
        context = new WeakReference<MainActivity>(activity);
        conductor = Conductor.getInstance();
    }

    @Override
    protected Void doInBackground(String... strings) {
        AsignacionServicioService.IS_INICIADO = false;
        conductor.activo = false;
        SharedPreferences sP = context.get().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        ActivityUtils.eliminarSharedPreferences(sP,"idUsuario");
        ActivityUtils.eliminarSharedPreferences(sP,"claveUsuario");
        Intent mainIntent = new Intent(context.get(), LoginActivity.class);
        context.get().startActivity(mainIntent);
        context.get().finish();
        RequestConductor.logOut();
        conductor.estado = 0;
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Intent i = new Intent(context.get(), AsignacionServicioService.class);
        context.get().stopService(i);
    }
}
