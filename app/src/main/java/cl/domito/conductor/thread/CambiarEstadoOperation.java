package cl.domito.conductor.thread;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.Utilidades;
import cl.domito.conductor.service.AsignacionServicioService;

public class CambiarEstadoOperation extends AsyncTask<Void, Void, Void> {

    private WeakReference<MapsActivity> context;
    private Button buttonConfirmar;
    private TextView textViewEstadoValor;

    public CambiarEstadoOperation(MapsActivity activity) {
        context = new WeakReference<MapsActivity>(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        String url = Utilidades.URL_BASE_MOVIL + "ModEstadoMovil.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("conductor",Conductor.getInstance().getNick()));
        JSONObject jsonObject = null;
        if(conductor.getEstado() == 0)
        {
            params.add(new BasicNameValuePair("estado","1"));
            jsonObject = Utilidades.enviarPost(url,params);
            conductor.setEstado(1);
        }
        else if(conductor.getEstado() == 1)
        {
            params.add(new BasicNameValuePair("estado","0"));
            jsonObject = Utilidades.enviarPost(url,params);
            conductor.setEstado(0);
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
      /*  buttonConfirmar = context.get().findViewById(R.id.buttonEstado);
        if(buttonConfirmar.getText().toString().equals("Terminar")) {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonConfirmar.setText("Terminando...");
                }
            });
        }
        else if(buttonConfirmar.getText().toString().equals("Iniciar"))
        {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonConfirmar.setText("Iniciando...");
                }
            });
        }*/
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        /*buttonConfirmar = context.get().findViewById(R.id.buttonEstado);
        textViewEstadoValor = context.get().findViewById(R.id.textViewEstadoValor);
        if(AsignacionServicioService.IS_INICIADO) {
            if(Conductor.getInstance().getEstado() == 1) {
                Conductor.getInstance().setActivo(true);
                context.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buttonConfirmar.setText("Terminar");
                        textViewEstadoValor.setText("Conectado");
                    }
                });
            }
            else if(Conductor.getInstance().getEstado() == 0)
            {
                Conductor.getInstance().setActivo(false);
                context.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buttonConfirmar.setText("Iniciar");
                        textViewEstadoValor.setText("Desconectado");
                    }
                });
            }
        }
        else
        {
            AsignacionServicioService asignacionServicioService = new AsignacionServicioService(context.get());
            Intent i = new Intent(context.get(), AsignacionServicioService.class);
            if(!ActivityUtils.isRunning(asignacionServicioService.getClass(),context.get())) {
                context.get().startService(i);
            }
            AsignacionServicioService.IS_INICIADO = true;
            Conductor.getInstance().setActivo(true);
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonConfirmar.setText("Terminar");
                    textViewEstadoValor.setText("Conectado");
                }
            });
        }*/
    }

}