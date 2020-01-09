package cl.domito.dmttransfer.thread;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cl.domito.dmttransfer.activity.utils.StringBuilderServiceUtil;
import cl.domito.dmttransfer.activity.utils.StringBuilderUtil;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.RequestConductor;
import cl.domito.dmttransfer.http.Utilidades;

public class ObtenerServiciosOperation extends AsyncTask<Void, Void, JSONArray> {



    @Override
    protected JSONArray doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        StringBuilder builder = StringBuilderServiceUtil.getInstance();
        builder.append(Utilidades.URL_BASE_SERVICIO).append("GetServiciosProgramados.php");
        String url =  builder.toString();
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("conductor",conductor.id));
        JSONArray jsonArray = RequestConductor.getServicios(url,params);
        return jsonArray;
    }
}
