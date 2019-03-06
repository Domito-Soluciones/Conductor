package cl.domito.conductor.thread;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.ServicioActivity;
import cl.domito.conductor.activity.ServicioDetalleActivity;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.Utilidades;

public class DesAsignarServicioOperation  extends AsyncTask<Void, Void, Void> {

    private WeakReference<ServicioDetalleActivity> context;
    private TextView textView;


    public DesAsignarServicioOperation(ServicioDetalleActivity activity) {
        context = new WeakReference<ServicioDetalleActivity>(activity);
    }

    public DesAsignarServicioOperation() {

    }

    @Override
    protected Void doInBackground(Void... voids) {
        String url = Utilidades.URL_BASE_SERVICIO + "ModEstadoServicio.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        textView = context.get().findViewById(R.id.textViewIdServicioValor);

        params.add(new BasicNameValuePair("id",textView.getText().toString()));
        params.add(new BasicNameValuePair("estado","1"));
        try {
            Utilidades.enviarPost(url,params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        if(context != null) {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Button buttonCancelar = context.get().findViewById(R.id.buttonCancelar);
                    buttonCancelar.setText("Cancelando...");
                }
            });
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(context != null) {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context.get(), ServicioActivity.class);
                    intent.putExtra("idServicio",textView.getText().toString());
                    intent.putExtra("accion","1");
                    context.get().startActivity(intent);
                    context.get().finish();
                    Toast.makeText(context.get(),"Servicio cancelado",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}




