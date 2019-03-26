package cl.domito.conductor.thread;

import android.os.AsyncTask;

import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class CambiarUbicacionOperation extends AsyncTask<Void, Void, Void> {


    @Override
    protected Void doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        String url = Utilidades.URL_BASE_MOVIL + "ModUbicacionMovil.php";
        try {
            RequestConductor.actualizarUbicacion(url, conductor.getLocation());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }
}
