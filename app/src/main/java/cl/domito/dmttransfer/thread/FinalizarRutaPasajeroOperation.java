package cl.domito.dmttransfer.thread;

import android.app.Activity;
import android.app.AlertDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;

import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.RequestConductor;

public class FinalizarRutaPasajeroOperation extends AsyncTask<String, Void, Void> {

    private WeakReference<Activity> context;
    private Conductor conductor;
    private AlertDialog dialog;
    private int index;
    private int total;

    public FinalizarRutaPasajeroOperation(Activity activity,int index,int total)
    {
        context = new WeakReference<Activity>(activity);
        conductor = Conductor.getInstance();
        this.index = index;
        this.total = total;
        dialog = ActivityUtils.setProgressDialog(activity);
    }
    @Override
    protected Void doInBackground(String... strings) {
        RequestConductor.cambiarEstadoPasajero("3","");
        if(strings!=null)
        {
            try {
                Geocoder geocoder = new Geocoder(context.get());
                List<Address> addresses = geocoder.getFromLocation(conductor.location.getLatitude(), conductor.location.getLongitude(), 1);
                String destino = addresses.get(0).getAddressLine(0);
                RequestConductor.actualizarLugarDestinoPasajero(destino);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
            }
        }
        return null;
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
    protected void onPostExecute(Void aVoid) {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(index == total)
                {
                    ActivityUtils.finalizar(context.get());
                }
                else {
                    ActivityUtils.recargarPasajeros(context.get());
                }
                if(!context.get().isDestroyed()) {
                    dialog.dismiss();
                }
                if(conductor.servicioActualRuta.contains("RG"))
                {
                    Toast.makeText(context.get(), "Pasajero Recogido", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context.get(), "Pasajero Entregado", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}