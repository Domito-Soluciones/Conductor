package cl.domito.conductor.thread;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;
import cl.domito.conductor.service.AsignacionServicioService;

public class CambiarEstadoOperation extends AsyncTask<Void, Void, Void> {

    private WeakReference<MapsActivity> context;
    private Button buttonConfirmar;
    private TextView textViewEstadoValor;

    public CambiarEstadoOperation(MapsActivity activity) {
        context = new WeakReference<MapsActivity>(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        JSONObject jsonObject = null;
        if(conductor.getEstado() == 0)
        {
            jsonObject = RequestConductor.cambiarEstadoMovil("1");
            conductor.setEstado(1);
        }
        else if(conductor.getEstado() == 1)
        {
            jsonObject = RequestConductor.cambiarEstadoMovil("0");
            conductor.setEstado(0);
        }
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

}