package cl.domito.dmttransfer.thread;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.ProduccionActivity;
import cl.domito.dmttransfer.activity.adapter.ReciclerViewProduccionAdapter;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.RequestConductor;
import cl.domito.dmttransfer.http.Utilidades;

public class ObtenerProduccionOperation extends AsyncTask<Void, Void, JSONArray> {

    private WeakReference<ProduccionActivity> context;
    private RecyclerView recyclerView;
    private AlertDialog dialog;
    private TextView textViewTotal;

    public ObtenerProduccionOperation(ProduccionActivity activity) {
            context = new WeakReference<ProduccionActivity>(activity);
            recyclerView = this.context.get().findViewById(R.id.recyclerViewProduccion);
            textViewTotal = this.context.get().findViewById(R.id.textViewTotal);
            dialog = ActivityUtils.setProgressDialog(context.get());
    }

    @Override
    protected JSONArray doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        Calendar c = Calendar.getInstance();
        String fechaDesde = "01/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        c.add(Calendar.MONTH,1);
        String fechaHasta = "01/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
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
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        int total = 0;
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
            lista.add( servicioFecha + "%"+ servicioHora + "%" + servicioTarifa + "%" + servicioId);
        } catch (Exception e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(Conductor.getInstance().id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
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
                }
            });
        }
        else
        {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Toast.makeText(context.get(), "No hay producción registrada", Toast.LENGTH_LONG).show();
                }
            });
        }

        textViewTotal.setText(total+"");

        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
    }

}
