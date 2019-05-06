package cl.domito.conductor.thread;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.ServicioDetalleActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.http.RequestConductor;

public class RealizarServicioOperation extends AsyncTask<Void, Void, String> {

    private WeakReference<ServicioDetalleActivity> context;
    private TextView textView;
    Button buttonConfirmar;
    private AlertDialog dialog;

    public RealizarServicioOperation(ServicioDetalleActivity activity) {
        context = new WeakReference<ServicioDetalleActivity>(activity);
        buttonConfirmar = context.get().findViewById(R.id.buttonFinalizar);
        textView = context.get().findViewById(R.id.textViewIdServicioValor);
        dialog = ActivityUtils.setProgressDialog(activity);
    }

    @Override
    protected String doInBackground(Void... voids) {
        RequestConductor.cambiarEstadoServicio(textView.getText().toString(),"3","");
        return textView.getText().toString();
    }
    @Override
    protected void onPreExecute() {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }

    @Override
    protected void onPostExecute(String aString) {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                context.get().finish();
                Toast.makeText(context.get(),"Servicio aceptado",Toast.LENGTH_LONG).show();
                CambiarEstadoNotificacionOperation cambiarEstadoNotificacionOperation = new CambiarEstadoNotificacionOperation();
                cambiarEstadoNotificacionOperation.execute(aString);
            }
        });
    }
}
