package cl.domito.conductor.thread;

import android.content.Intent;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import cl.domito.conductor.activity.LoginActivity;
import cl.domito.conductor.activity.MainActivity;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.service.AsignacionServicioService;

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
        conductor.setActivo(false);
        Intent mainIntent = new Intent(context.get(), LoginActivity.class);
        context.get().startActivity(mainIntent);
        context.get().finish();
        RequestConductor.logOut();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Intent i = new Intent(context.get(), AsignacionServicioService.class);
        context.get().stopService(i);
    }
}
