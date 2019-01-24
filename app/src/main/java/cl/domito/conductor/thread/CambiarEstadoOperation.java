package cl.domito.conductor.thread;

import android.content.ContentValues;
import android.os.AsyncTask;
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
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.Utilidades;

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
        String url = Utilidades.URL_BASE_CONDUCTOR + "ModEstadoConductor.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("usuario",Conductor.getInstance().getNick()));
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
    protected void onPostExecute(Void aVoid) {
        buttonConfirmar = context.get().findViewById(R.id.buttonConfirmar);
        textViewEstadoValor = context.get().findViewById(R.id.textViewEstadoValor);
        if(Conductor.getInstance().getEstado() == 1) {
            buttonConfirmar.setText("Terminar");
            textViewEstadoValor.setText("Desconectado");
        }
        else if(Conductor.getInstance().getEstado() == 0)
        {
            buttonConfirmar.setText("Iniciar");
            textViewEstadoValor.setText("Conectado");
        }
    }

}
