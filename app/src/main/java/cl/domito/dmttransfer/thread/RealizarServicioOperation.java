package cl.domito.dmttransfer.thread;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.ServicioDetalleActivity;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.http.RequestConductor;

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
                if(!context.get().isDestroyed()) {
                    dialog.show();
                }
            }
        });
    }

    @Override
    protected void onPostExecute(String aString) {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.get(),"Servicio aceptado",Toast.LENGTH_LONG).show();
                CambiarEstadoNotificacionOperation cambiarEstadoNotificacionOperation = new CambiarEstadoNotificacionOperation();
                cambiarEstadoNotificacionOperation.execute(aString);
                //dialog.dismiss();
                context.get().finish();
            }
        });
    }
}
