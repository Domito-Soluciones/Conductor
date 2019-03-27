package cl.domito.conductor.thread;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MainActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;
import cl.domito.conductor.service.AsignacionServicioService;

public class DatosConductorOperation  extends AsyncTask<Void, Void, Void> {

    private WeakReference<MainActivity> context;
    Conductor conductor;
    TextView textViewError;

    public DatosConductorOperation(MainActivity activity) {
        context = new WeakReference<MainActivity>(activity);
        conductor = Conductor.getInstance();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String url = Utilidades.URL_BASE_CONDUCTOR + "GetConductor.php";
        try {
            List<NameValuePair> params = new ArrayList();
            params.add(new BasicNameValuePair("id",conductor.getNick()));
            JSONObject jsonObject = RequestConductor.datosConductor(url,params);
            if(jsonObject != null) {
                conductor.setNombre(jsonObject.getString("conductor_nombre"));
                conductor.setEstado(jsonObject.getInt("conductor_estado"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = context.get().findViewById(R.id.textViewNombreUsuario);
                TextView textViewEstado = context.get().findViewById(R.id.textViewEstadoValor);
                textView.setText(conductor.getNombre());
                if(conductor.getEstado() == 1)
                {
                    textViewEstado.setText("Conectado");
                    AsignacionServicioService asignacionServicioService = new AsignacionServicioService(context.get());
                    Intent i = new Intent(context.get(), AsignacionServicioService.class);
                    if(!ActivityUtils.isRunning(asignacionServicioService.getClass(),context.get())) {
                        context.get().startService(i);
                    }
                    AsignacionServicioService.IS_INICIADO = true;
                    conductor.setActivo(true);
                }
                else if(conductor.getEstado() == 0)
                {
                    textViewEstado.setText("Desconectado");
                }
            }
        });
    }
}
