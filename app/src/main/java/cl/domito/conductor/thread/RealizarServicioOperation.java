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

public class RealizarServicioOperation extends AsyncTask<Void, Void, String> {

    private WeakReference<ServicioDetalleActivity> context;
    private TextView textView;
    Button buttonConfirmar;


    public RealizarServicioOperation(ServicioDetalleActivity activity) {
        context = new WeakReference<ServicioDetalleActivity>(activity);
        buttonConfirmar = context.get().findViewById(R.id.buttonFinalizar);
        textView = context.get().findViewById(R.id.textViewIdServicioValor);
    }

    @Override
    protected String doInBackground(Void... voids) {
        RequestConductor.cambiarEstadoServicio(textView.getText().toString(),"3","");
        return textView.getText().toString();
    }
    @Override
    protected void onPreExecute() {
        if(context != null) {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonConfirmar.setText("En Proceso...");
                }
            });
        }
    }

    @Override
    protected void onPostExecute(String aString) {
        if(context != null) {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    context.get().finish();
                    CambiarEstadoNotificacionOperation cambiarEstadoNotificacionOperation = new CambiarEstadoNotificacionOperation();
                    cambiarEstadoNotificacionOperation.execute(aString);
                    Toast.makeText(context.get(),"Servicio aceptado",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
