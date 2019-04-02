package cl.domito.conductor.thread;

import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cl.domito.conductor.activity.MainActivity;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;

public class CambiarEstadoOperation extends AsyncTask<Void, Void, Void> {

    private WeakReference<MainActivity> context;
    private Button buttonConfirmar;
    private TextView textViewEstadoValor;

    public CambiarEstadoOperation(MainActivity activity) {
        context = new WeakReference<MainActivity>(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        JSONObject jsonObject = null;
        if(conductor.estado == 0)
        {
            jsonObject = RequestConductor.cambiarEstadoMovil("1");
            conductor.estado = 1;
        }
        else if(conductor.estado == 1)
        {
            jsonObject = RequestConductor.cambiarEstadoMovil("0");
            conductor.estado = 0;
        }
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

}