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

public class TomarPasajeroOperation extends AsyncTask<String, Void, Void> {

    private WeakReference<Activity> context;
    private Conductor conductor;
    private AlertDialog dialog;

    public TomarPasajeroOperation(Activity activity)
    {
        context = new WeakReference<Activity>(activity);
        conductor = Conductor.getInstance();
        dialog = ActivityUtils.setProgressDialog(activity);
    }

    @Override
    protected Void doInBackground(String... strings) {
        RequestConductor.cambiarEstadoPasajero("1","");
        if(strings.length > 0)
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
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        Activity activity = context.get();
        if(!activity.isDestroyed()) {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        ActivityUtils.recargarPasajeros(context.get());
        Activity activity = context.get();
        if(!activity.isDestroyed()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (conductor.servicioActualRuta.contains("RG")) {
                        Toast.makeText(context.get(), "Pasajero Recogido", Toast.LENGTH_SHORT).show();
                    } else if (conductor.servicioActualRuta.contains("ZP")) {
                        Toast.makeText(context.get(), "Pasajero Abordado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context.get(), "Pasajero Recogido", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }
            });
        }
    }

}