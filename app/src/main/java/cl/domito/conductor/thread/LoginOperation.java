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
        String url = Utilidades.URL_BASE_CONDUCTOR + "Login.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("usuario", strings[0]));
        params.add(new BasicNameValuePair("password", strings[1]));
        boolean login = RequestConductor.loginConductor(url,params);
        loginActivity.runOnUiThread(ActivityUtils.mensajeError(loginActivity));
        if (login) {
            conductor.setActivo(true);
            conductor.setNick(strings[0]);
            if(conductor.isRecordarSession()) {
                SharedPreferences pref = loginActivity.getApplicationContext().getSharedPreferences
                        ("preferencias",Context.MODE_PRIVATE);
                ActivityUtils.guardarSharedPreferences(pref,"idUsuario",conductor.getNick());
            }
            Intent mainIntent = new Intent(loginActivity, MapsActivity.class);
            loginActivity.startActivity(mainIntent);
            loginActivity.finish();
            String url2 = Utilidades.URL_BASE_CONDUCTOR + "ModEstadoConductor.php";
            List<NameValuePair> params2 = new ArrayList();
            params2.add(new BasicNameValuePair("usuario",Conductor.getInstance().getNick()));
            params2.add(new BasicNameValuePair("estado","1"));
            Utilidades.enviarPost(url2,params2);
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

    }
}
