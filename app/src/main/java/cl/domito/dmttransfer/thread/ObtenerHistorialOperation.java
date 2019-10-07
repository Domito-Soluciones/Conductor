package cl.domito.dmttransfer.thread;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
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
import cl.domito.dmttransfer.activity.HistoricoActivity;
import cl.domito.dmttransfer.activity.adapter.ReciclerViewHistorialAdapter;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.RequestConductor;
import cl.domito.dmttransfer.http.Utilidades;

public class ObtenerHistorialOperation extends AsyncTask<Void, Void, JSONArray> {

    private WeakReference<HistoricoActivity> context;
    private RecyclerView recyclerView;
    private AlertDialog dialog;

    public ObtenerHistorialOperation(HistoricoActivity activity) {
        context = new WeakReference<HistoricoActivity>(activity);
        recyclerView = this.context.get().findViewById(R.id.recyclerViewHistorial);
        dialog = ActivityUtils.setProgressDialog(context.get());
    }

    @Override
    protected JSONArray doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        Calendar c = Calendar.getInstance();
        String fechaHasta = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        c.add(Calendar.MONTH,-2);
        String fechaDesde = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        String url = Utilidades.URL_BASE_SERVICIO + "GetServiciosHistoricos.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("desde",fechaDesde));
        params.add(new BasicNameValuePair("hasta",fechaHasta));
        params.add(new BasicNameValuePair("conductor",conductor.id));
        JSONArray jsonObject = RequestConductor.getServicios(url,params);
        return jsonObject;
    }

    @Override
    protected void onPreExecute() {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!context.get().isDestroyed()) {
                    dialog.show();
                }
            }
        });
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
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
                lista.add( servicioId + "%" + servicioFecha + "%"+ servicioHora + "%" + servicioCliente + "%" + servicioEstado);
            } catch (Exception e) {
                e.printStackTrace();
                EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                enviarLogOperation.execute(Conductor.getInstance().id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
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
                }
            });
        }
        else
        {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context.get(), "No hay servicios historicos", Toast.LENGTH_LONG).show();
                }
            });

        }


        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!context.get().isDestroyed()) {
                    dialog.dismiss();
                }
            }
        });
    }
}
