package cl.domito.conductor.thread;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.activity.LoginActivity;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.Utilidades;
import cl.domito.conductor.service.AsignacionServicioService;

public class LogoutOperation extends AsyncTask<String, Void, Void> {

    WeakReference<MapsActivity> context;

    public LogoutOperation(MapsActivity activity) {
        context = new WeakReference<MapsActivity>(activity);
    }

    @Override
    protected Void doInBackground(String... strings) {
        AsignacionServicioService.IS_INICIADO = false;
        ActivityUtils.eliminarSharedPreferences(context.get().getSharedPreferences("preferencias", Context.MODE_PRIVATE),"idUsuario");
        Conductor.getInstance().setActivo(false);
        Intent mainIntent = new Intent(context.get(), LoginActivity.class);
        context.get().startActivity(mainIntent);
        context.get().finish();
        Conductor conductor = Conductor.getInstance();
        String url = Utilidades.URL_BASE_CONDUCTOR + "ModEstadoConductor.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("usuario",Conductor.getInstance().getNick()));
        params.add(new BasicNameValuePair("estado","0"));
        Utilidades.enviarPost(url,params);
        return null;
    }
}
