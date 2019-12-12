package cl.domito.dmttransfer.thread;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.ServicioDetalleActivity;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.http.RequestConductor;

public class DesAsignarServicioOperation  extends AsyncTask<Void, Void, String> {

    private WeakReference<ServicioDetalleActivity> context;
    private TextView textView;
    private AlertDialog dialog;

    public DesAsignarServicioOperation(ServicioDetalleActivity activity) {
        context = new WeakReference<ServicioDetalleActivity>(activity);
        dialog = ActivityUtils.setProgressDialog(activity);
    }

    @Override
    protected String doInBackground(Void... voids) {
        textView = context.get().findViewById(R.id.textViewIdServicioValor);
        RequestConductor.cambiarEstadoServicio(textView.getText().toString(),"1","");
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
                //dialog.dismiss();
                context.get().finish();
                CambiarEstadoNotificacionOperation cambiarEstadoNotificacionOperation = new CambiarEstadoNotificacionOperation();
                cambiarEstadoNotificacionOperation.execute(aString);
                ActivityUtils.eliminarNotificacion(context.get(),aString);
                Toast.makeText(context.get(),"Servicio cancelado",Toast.LENGTH_LONG).show();
            }
        });
    }
}




