package cl.domito.dmttransfer.thread;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import cl.domito.dmttransfer.activity.LoginActivity;
import cl.domito.dmttransfer.activity.MainActivity;
import cl.domito.dmttransfer.activity.SplashScreenActivity;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.RequestConductor;
import cl.domito.dmttransfer.http.Utilidades;
import cl.domito.dmttransfer.service.AsignacionServicioService;

public class LoginOperation extends AsyncTask<String, Void, Void> {

    WeakReference<LoginActivity> context;
    TextView textViewError;
    Conductor conductor;
    AlertDialog dialog;

    public LoginOperation(LoginActivity activity) {
        context = new WeakReference<LoginActivity>(activity);
        conductor = Conductor.getInstance();
        dialog = ActivityUtils.setProgressDialog(context.get());
    }

    @Override
    protected Void doInBackground(String... strings) {
        LoginActivity loginActivity = context.get();
        boolean login = RequestConductor.loginConductor(strings[0],strings[1]);
        if (login) {
            if("".equals(SplashScreenActivity.ANDROID_ID)) {
                loginActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast t = Toast.makeText(loginActivity, "Usuario activo en otro dispositivo", Toast.LENGTH_SHORT);
                        t.show();
                    }
                });
                return null;
            }
            conductor.activo = true;
            conductor.nick = strings[0];
            if(conductor.recordarSession) {
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
            Intent mainIntent = new Intent(loginActivity, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
            loginActivity.startActivity(mainIntent);
            loginActivity.finish();
            RequestConductor.cambiarEstadoMovil("1");
        } else if(Utilidades.tipoError == 0) {
            loginActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast t = Toast.makeText(loginActivity, "Usuario y/o contraseña no coinciden", Toast.LENGTH_SHORT);
                    t.show();
                }
            });
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(conductor.activo) {
            AsignacionServicioService asignacionServicioService = new AsignacionServicioService(context.get());
            Intent i = new Intent(context.get(), AsignacionServicioService.class);
            if (!ActivityUtils.isRunning(asignacionServicioService.getClass(), context.get())) {
                context.get().startService(i);
            }
        AsignacionServicioService.IS_INICIADO = true;
        }
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
    }
}
