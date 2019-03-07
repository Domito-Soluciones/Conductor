package cl.domito.conductor.thread;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.LoginActivity;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;
import cl.domito.conductor.service.AsignacionServicioService;

public class LoginOperation extends AsyncTask<String, Void, Void> {

    WeakReference<LoginActivity> context;
    TextView textViewError;

    public LoginOperation(LoginActivity activity) {
        context = new WeakReference<LoginActivity>(activity);
    }

    @Override
    protected Void doInBackground(String... strings) {
        Conductor conductor = Conductor.getInstance();
        conductor.setConectado(true);
        LoginActivity loginActivity = context.get();
        ProgressBar progressBar = loginActivity.findViewById(R.id.login_progress);
        loginActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(ProgressBar.VISIBLE);
            }
        });
        boolean login = RequestConductor.loginConductor(strings[0],strings[1]);
        loginActivity.runOnUiThread(ActivityUtils.mensajeError(loginActivity));
        if (login) {
            conductor.setActivo(true);
            conductor.setNick(strings[0]);
            if(conductor.isRecordarSession()) {
                SharedPreferences pref = loginActivity.getApplicationContext().getSharedPreferences
                        ("preferencias",Context.MODE_PRIVATE);
                ActivityUtils.guardarSharedPreferences(pref,"idUsuario",strings[0]);
                ActivityUtils.guardarSharedPreferences(pref,"claveUsuario",strings[1]);
            }
            else
            {
                ActivityUtils.eliminarSharedPreferences(context.get().getSharedPreferences
                        ("preferencias", Context.MODE_PRIVATE),"idUsuario");
                ActivityUtils.eliminarSharedPreferences(context.get().getSharedPreferences
                        ("preferencias", Context.MODE_PRIVATE),"claveUsuario");
            }
            Intent mainIntent = new Intent(loginActivity, MapsActivity.class);
            loginActivity.startActivity(mainIntent);
            loginActivity.finish();
            RequestConductor.cambiarEstadoMovil("1");
        } else {
            loginActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast t = Toast.makeText(loginActivity, "Usuario y/o contraseña no coinciden", Toast.LENGTH_SHORT);
                    t.show();
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            });
        }
        return null;
}

    @Override
    protected void onPostExecute(Void aVoid) {
        if(Conductor.getInstance().isActivo()) {
            AsignacionServicioService asignacionServicioService = new AsignacionServicioService(context.get());
            Intent i = new Intent(context.get(), AsignacionServicioService.class);
            if (!ActivityUtils.isRunning(asignacionServicioService.getClass(), context.get())) {
                context.get().startService(i);
                Toast.makeText(context.get(),"servicio iniciado",Toast.LENGTH_LONG).show();
            }
            AsignacionServicioService.IS_INICIADO = true;
        }
    }
}
