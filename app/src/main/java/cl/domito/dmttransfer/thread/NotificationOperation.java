package cl.domito.dmttransfer.thread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.MainActivity;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.RequestConductor;

public class NotificationOperation extends AsyncTask<Void, Void, String[]> {

    private Context context;

    public NotificationOperation(Context context) {
        this.context = context;
    }

    @Override
    protected String[] doInBackground(Void... voids) {
        String[] respuesta = new String[2];
        Conductor conductor = Conductor.getInstance();
        JSONArray jsonArray = RequestConductor.obtenerNotificaciones();
        if(jsonArray == null)
        {
            return null;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("notificacion_id");
                String tipo = jsonObject.getString("notificacion_tipo");
                respuesta[0] = id;
                respuesta[1] = tipo;
                if(tipo.equals("0")) {
                    ActivityUtils.enviarNotificacion(Integer.parseInt(id),context, "", jsonObject.getString("notificacion_texto"), R.drawable.furgoneta, MainActivity.class);
                }
                else if(tipo.equals("1"))
                {
                    String fecha = jsonObject.getString("notificacion_fecha");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date date = sdf.parse(fecha);
                    Date dateNow = new Date();
                    if(Math.abs(date.getTime() - dateNow.getTime()) < 1.8e+6) {
                        ActivityUtils.enviarNotificacion(Integer.parseInt(id),context, "", jsonObject.getString("notificacion_texto"), R.drawable.furgoneta,MainActivity.class);
                    }
                }
                else if(tipo.equals("2")){
                    ActivityUtils.enviarNotificacion(Integer.parseInt(id),context, "", jsonObject.getString("notificacion_texto"), R.drawable.furgoneta,MainActivity.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
                EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
            }
        }
        return respuesta;
    }

    @Override
    protected void onPostExecute(String[] aString) {
        if (aString != null) {
            if (aString[0] != null && aString[1] != null) {
                if (aString[1].equals("1") || aString[1].equals("2")) {
                    CambiarEstadoNotificacionOperation cambiarEstadoNotificacionOperation = new CambiarEstadoNotificacionOperation();
                    cambiarEstadoNotificacionOperation.execute(aString[0]);
                }
            }
        }
    }

    private void sendMessage(String message, String value) {
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", message);
        intent.putExtra("value", value);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


}