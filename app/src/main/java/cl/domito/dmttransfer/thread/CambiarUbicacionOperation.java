package cl.domito.dmttransfer.thread;

import android.os.AsyncTask;

import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.RequestConductor;
import cl.domito.dmttransfer.http.Utilidades;

public class CambiarUbicacionOperation extends AsyncTask<Void, Void, Void> {


    @Override
    protected Void doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        String url = Utilidades.URL_BASE_MOVIL + "ModUbicacionMovil.php";
        try {
            RequestConductor.actualizarUbicacion(url, conductor.location);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }
}
