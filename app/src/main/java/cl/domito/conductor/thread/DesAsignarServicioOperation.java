package cl.domito.conductor.thread;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.ServicioDetalleActivity;
import cl.domito.conductor.http.RequestConductor;

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
        textView = context.get().findViewById(R.id.textViewIdServicioValor);
        RequestConductor.cambiarEstadoServicio(textView.getText().toString(),"1","");
        return null;
    }

    @Override
    protected void onPreExecute() {
        if(context != null) {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Button buttonCancelar = context.get().findViewById(R.id.buttonCancelar);
                    buttonCancelar.setText("En Progreso...");
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
                    context.get().finish();
                    Toast.makeText(context.get(),"Servicio cancelado",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}




