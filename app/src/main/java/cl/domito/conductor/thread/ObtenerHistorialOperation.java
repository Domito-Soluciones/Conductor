package cl.domito.conductor.thread;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.HistoricoActivity;
import cl.domito.conductor.activity.adapter.ReciclerViewHistorialAdapter;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class ObtenerHistorialOperation extends AsyncTask<Void, Void, JSONArray> {

    private WeakReference<HistoricoActivity> context;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public ObtenerHistorialOperation(HistoricoActivity activity) {
        context = new WeakReference<HistoricoActivity>(activity);
        recyclerView = this.context.get().findViewById(R.id.recyclerViewHistorial);
        progressBar = this.context.get().findViewById(R.id.progressBarHistorial);
    }

    @Override
    protected JSONArray doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        Calendar c = Calendar.getInstance();
        String fechaHasta = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        c.add(Calendar.MONTH,-2);
        String fechaDesde = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        String url = Utilidades.URL_BASE_SERVICIO + "GetServicios.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("desde",fechaDesde));
        params.add(new BasicNameValuePair("hasta",fechaHasta));
        params.add(new BasicNameValuePair("estado","5"));
        params.add(new BasicNameValuePair("conductor",conductor.getId()));
        JSONArray jsonObject = RequestConductor.getServicios(url,params);
        return jsonObject;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
        ArrayList<String> lista = new ArrayList();
        String ant = "";
        if(jsonArray == null)
        {
            return;
        }
        for(int i = 0; i < jsonArray.length(); i++)
        {
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String servicioId = jsonObject.getString("servicio_id");
                String servicioFecha = jsonObject.getString("servicio_fecha");
                String servicioHora = jsonObject.getString("servicio_hora");
                String servicioCliente = jsonObject.getString("servicio_cliente");
                String servicioEstado = jsonObject.getString("servicio_estado");
                String fecha = format2.format(format1.parse(servicioFecha.replace("/","-")));
                lista.add( servicioId + "%" + fecha + "%"+ servicioHora + "%" + servicioCliente + "%" + servicioEstado);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(lista.size() > 0 ) {
            String[] array = new String[lista.size()];
            array  = lista.toArray(array);
            ReciclerViewHistorialAdapter mAdapter = new ReciclerViewHistorialAdapter(context.get(),array);
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setAdapter(mAdapter);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        else
        {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context.get(), "No hay servicios historicos", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
