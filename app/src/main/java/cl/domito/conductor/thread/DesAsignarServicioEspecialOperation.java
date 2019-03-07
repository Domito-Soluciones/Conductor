package cl.domito.conductor.thread;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.ServicioActivity;
import cl.domito.conductor.activity.ServicioDetalleActivity;
import cl.domito.conductor.activity.ServicioDetalleEspecialActivity;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class DesAsignarServicioEspecialOperation extends AsyncTask<Void, Void, Void> {

    private WeakReference<ServicioDetalleEspecialActivity> context;
    private TextView textView;

    public DesAsignarServicioEspecialOperation(ServicioDetalleEspecialActivity activity) {
        context = new WeakReference<ServicioDetalleEspecialActivity>(activity);
        textView = context.get().findViewById(R.id.textViewIdServicioValor);

    }

    public DesAsignarServicioEspecialOperation() {

    }

    @Override
    protected Void doInBackground(Void... voids) {
        RequestConductor.cambiarEstadoServicioEspecial(textView.getText().toString(),"1");
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




