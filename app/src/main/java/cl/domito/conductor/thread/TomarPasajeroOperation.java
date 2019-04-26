package cl.domito.conductor.thread;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

import cl.domito.conductor.activity.MainActivity;
import cl.domito.conductor.activity.PasajeroActivity;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;

public class TomarPasajeroOperation extends AsyncTask<String, Void, Void> {

    private WeakReference<Activity> context;
    private Conductor conductor;

    public TomarPasajeroOperation(Activity activity)
    {
        context = new WeakReference<Activity>(activity);
        conductor = Conductor.getInstance();
    }

    @Override
    protected Void doInBackground(String... strings) {
        RequestConductor.cambiarEstadoPasajero("1","");
        if(strings != null)
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

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(conductor.servicioActualRuta.contains("RG"))
                {
                    Toast.makeText(context.get(),"Pasajero Recogido",Toast.LENGTH_SHORT).show();
                }
                else if(conductor.servicioActualRuta.contains("ZP")) {
                    Toast.makeText(context.get(), "Pasajero Abordado", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context.get(), "Pasajero Recogido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}