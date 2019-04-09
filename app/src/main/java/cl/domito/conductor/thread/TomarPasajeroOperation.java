package cl.domito.conductor.thread;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cl.domito.conductor.activity.MainActivity;
import cl.domito.conductor.activity.PasajeroActivity;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;

public class TomarPasajeroOperation extends AsyncTask<Void, Void, Void> {

    private WeakReference<PasajeroActivity> context;
    private Conductor conductor;

    public TomarPasajeroOperation(PasajeroActivity activity)
    {
        context = new WeakReference<PasajeroActivity>(activity);
        conductor = Conductor.getInstance();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        RequestConductor.cambiarEstadoPasajero("1");
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(conductor.servicioActualRuta.contains("RG"))
                {
                    Toast.makeText(context.get(),"Pasajero Recogido",Toast.LENGTH_SHORT).show();
                }
                else if(conductor.servicioActualRuta.contains("ZP")) {
                    Toast.makeText(context.get(), "Pasajero Entregado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}