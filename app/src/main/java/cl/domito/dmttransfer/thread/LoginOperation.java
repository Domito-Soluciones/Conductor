package cl.domito.dmttransfer.thread;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

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
        JSONObject login = RequestConductor.loginConductor(strings[0], strings[1]);
        try{
            String id = login.getString("conductor_id");
            String dispositivo = login.getString("conductor_equipo");
            String nombre = login.getString("conductor_nombre");
        if (!id.equals("0")) {
            if (!dispositivo.equals("") && !dispositivo.equals(SplashScreenActivity.ANDROID_ID)) {
                loginActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast t = Toast.makeText(loginActivity, "Usuario activo en otro dispositivo", Toast.LENGTH_SHORT);
                        t.show();
                    }
                });
                return null;
            }
            else
            {
                conductor.id = id;
                conductor.activo = true;
                conductor.nombre = "";
                conductor.nick = strings[0];
                RequestConductor.cambiarEstadoMovil("1");
            }
            SharedPreferences pref = loginActivity.getApplicationContext().getSharedPreferences
                        ("preferencias", Context.MODE_PRIVATE);
            ActivityUtils.guardarSharedPreferences(pref, "idUsuario", conductor.id);
            ActivityUtils.guardarSharedPreferences(pref, "nickUsuario", strings[0]
            );
            ActivityUtils.guardarSharedPreferences(pref, "claveUsuario", strings[1]);
            ActivityUtils.guardarSharedPreferences(pref, "nombreUsuario", nombre);
            if (conductor.recordarSession) {
                ActivityUtils.guardarSharedPreferences(pref, "recordar", "1");
            }
            else{
                ActivityUtils.guardarSharedPreferences(pref, "recordar", "0");
            }
            Intent mainIntent = new Intent(loginActivity, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
            loginActivity.startActivity(mainIntent);
            loginActivity.finish();
        }
        else if(id .equals("0")){
            loginActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast t = Toast.makeText(loginActivity.getBaseContext(), "Usuario y/o contraseña no coinciden", Toast.LENGTH_SHORT);
                    t.show();
                }
            });
        }
        else if (Utilidades.tipoError == 0) {
            loginActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast t = Toast.makeText(loginActivity.getBaseContext(), "Usuario y/o contraseña no coinciden", Toast.LENGTH_SHORT);
                    t.show();
                }
            });
        }
    }
    catch(Exception e){
        e.printStackTrace();
        EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
        enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
    }
        return null;
    }

    @Override
    protected void onPreExecute() {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!context.get().isDestroyed()) {
                    try {
                        dialog.show();
                    }
                    catch(Exception e){

                    }
                }
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.get().startForegroundService(i);
                }
                else{context.get().startService(i);}
            }
        AsignacionServicioService.IS_INICIADO = true;
        }
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!context.get().isDestroyed()) {
                    dialog.dismiss();
                }
            }
        });
    }
}
