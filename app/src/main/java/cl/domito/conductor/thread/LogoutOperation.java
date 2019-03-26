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

    public LogoutOperation(MainActivity activity) {
        context = new WeakReference<MainActivity>(activity);
    }

    @Override
    protected Void doInBackground(String... strings) {
        AsignacionServicioService.IS_INICIADO = false;
        Conductor.getInstance().setActivo(false);
        Intent mainIntent = new Intent(context.get(), LoginActivity.class);
        context.get().startActivity(mainIntent);
        context.get().finish();
        Conductor conductor = Conductor.getInstance();
        RequestConductor.logOut();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        AsignacionServicioService.TERMINAR = false;
    }
}
