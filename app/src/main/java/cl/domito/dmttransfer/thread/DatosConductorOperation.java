package cl.domito.dmttransfer.thread;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.MainActivity;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.activity.utils.StringBuilderUtil;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.RequestConductor;
import cl.domito.dmttransfer.http.Utilidades;
import cl.domito.dmttransfer.service.AsignacionServicioService;

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
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_CONDUCTOR).append("GetConductor.php");
        String url =  builder.toString();
        try {
            List<NameValuePair> params = new ArrayList();
            params.add(new BasicNameValuePair("id",conductor.nick));
            JSONObject jsonObject = RequestConductor.datosConductor(url,params);
            if(jsonObject != null) {
                conductor.nombre = jsonObject.getString("conductor_nombre");
                conductor.estado = 1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
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
                SharedPreferences pref = context.get().getApplicationContext().getSharedPreferences
                        ("preferencias", Context.MODE_PRIVATE);
                String nombre = pref.getString("nombreUsuario", "");
                textView.setText(nombre);
                if(conductor.estado == 1)
                {
                    textViewEstado.setText("Conectado");
                    AsignacionServicioService asignacionServicioService = new AsignacionServicioService(context.get());
                    Intent i = new Intent(context.get(), AsignacionServicioService.class);
                    if(!ActivityUtils.isRunning(asignacionServicioService.getClass(),context.get())) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.get().startForegroundService(i);
                        }
                        else{
                            context.get().startService(i);
                        }
                    }
                    AsignacionServicioService.IS_INICIADO = true;
                    conductor.activo = true;
                }
                else if(conductor.estado == 0)
                {
                    textViewEstado.setText("Desconectado");
                }
            }
        });
    }
}
