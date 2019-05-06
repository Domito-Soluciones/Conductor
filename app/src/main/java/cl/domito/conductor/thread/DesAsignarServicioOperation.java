package cl.domito.conductor.thread;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.ServicioDetalleActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.http.RequestConductor;

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
                dialog.show();
            }
        });
    }

    @Override
    protected void onPostExecute(String aString) {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                context.get().finish();
                CambiarEstadoNotificacionOperation cambiarEstadoNotificacionOperation = new CambiarEstadoNotificacionOperation();
                cambiarEstadoNotificacionOperation.execute(aString);
                dialog.dismiss();
                Toast.makeText(context.get(),"Servicio cancelado",Toast.LENGTH_LONG).show();
            }
        });
    }
}




