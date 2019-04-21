package cl.domito.conductor.thread;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import cl.domito.conductor.activity.ProduccionActivity;
import cl.domito.conductor.activity.adapter.ReciclerViewHistorialAdapter;
import cl.domito.conductor.activity.adapter.ReciclerViewProduccionAdapter;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class ObtenerProduccionOperation extends AsyncTask<Void, Void, JSONArray> {

    private WeakReference<ProduccionActivity> context;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
        private TextView textViewTotal;

    public ObtenerProduccionOperation(ProduccionActivity activity) {
            context = new WeakReference<ProduccionActivity>(activity);
            recyclerView = this.context.get().findViewById(R.id.recyclerViewProduccion);
            progressBar = this.context.get().findViewById(R.id.progressBarProduccion);
            textViewTotal = this.context.get().findViewById(R.id.textViewTotal);
        }

    @Override
    protected JSONArray doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        Calendar c = Calendar.getInstance();
        String fechaHasta = "01/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        c.add(Calendar.MONTH,-1);
        String fechaDesde = "01/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        String url = Utilidades.URL_BASE_LIQUIDACION + "GetProduccion.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("desde",fechaDesde));
        params.add(new BasicNameValuePair("hdesde","00:00:00"));
        params.add(new BasicNameValuePair("hasta",fechaHasta));
        params.add(new BasicNameValuePair("hhasta","00:00:00"));
        params.add(new BasicNameValuePair("estado","5"));
        params.add(new BasicNameValuePair("conductor",conductor.id));
        JSONArray jsonObject = RequestConductor.getServicios(url,params);
        return jsonObject;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        int total = 0;
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
                String servicioTarifa = jsonObject.getString("servicio_tarifa1");
                total += Integer.parseInt(servicioTarifa);
                String fecha = format2.format(format1.parse(servicioFecha.replace("/","-")));
                lista.add( fecha + "%"+ servicioHora + "%" + servicioTarifa + "%" + servicioId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(lista.size() > 0 ) {
            String[] array = new String[lista.size()];
            array  = lista.toArray(array);
            ReciclerViewProduccionAdapter mAdapter = new ReciclerViewProduccionAdapter(context.get(),array);
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
                    Toast.makeText(context.get(), "No hay producción registrada", Toast.LENGTH_LONG).show();
                }
            });
        }

        textViewTotal.setText(total+"");
    }

}
