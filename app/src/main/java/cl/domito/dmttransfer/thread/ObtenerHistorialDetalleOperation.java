package cl.domito.dmttransfer.thread;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.HistoricoActivity;
import cl.domito.dmttransfer.activity.HistoricoDetalleActivity;
import cl.domito.dmttransfer.activity.adapter.ReciclerViewDetalleAdapter;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.activity.utils.StringBuilderUtil;
import cl.domito.dmttransfer.dominio.Conductor;

public class ObtenerHistorialDetalleOperation extends AsyncTask<String, Void, Void> {

    private WeakReference<HistoricoActivity> context;
    private RecyclerView recyclerView;
    private AlertDialog dialog;
    private TextView textviewServicioValor;
    private TextView textviewFechaValor;
    private TextView textviewClienteValor;
    private TextView textviewRutaValor;
    private TextView textviewTarifaValor;
    private TextView textviewCantidadValor;
    private TextView textviewObservacionValor;
    private RecyclerView recyclerViewDetalle;

    public ObtenerHistorialDetalleOperation(HistoricoActivity activity) {
        context = new WeakReference<HistoricoActivity>(activity);
        recyclerView = this.context.get().findViewById(R.id.recyclerViewHistorial);
        dialog = ActivityUtils.setProgressDialog(context.get());
        textviewServicioValor = activity.findViewById(R.id.textViewIdServicioValor);
        textviewFechaValor = activity.findViewById(R.id.textViewFechaValor);
        textviewClienteValor = activity.findViewById(R.id.textViewClienteValor);
        textviewRutaValor = activity.findViewById(R.id.textViewRutaValor);
        textviewTarifaValor = activity.findViewById(R.id.textViewTarifaValor);
        textviewCantidadValor = activity.findViewById(R.id.textViewCantidadValor);
        textviewObservacionValor = activity.findViewById(R.id.textViewObservacionValor);
        recyclerViewDetalle = (RecyclerView) activity.findViewById(R.id.recyclerViewDetalle);

    }

    @Override
    protected Void doInBackground(String... strings) {
        if(!context.get().isDestroyed()) {
            try {
                dialog.show();
            }
            catch(Exception e){

            }
        }
        ObtenerServicioHistoricoOperation obtenerServicioHistoricoOperation = new ObtenerServicioHistoricoOperation();
        try
        {
            String idServicio = strings[0];
            JSONArray historico = obtenerServicioHistoricoOperation.execute(idServicio).get();
            int cantidad = 0;
            ArrayList<String> lista = new ArrayList();
            for (int i = 0; i < historico.length(); i++) {
                JSONObject servicio = historico.getJSONObject(i);
                if (i == 0) {
                    textviewServicioValor.setText(servicio.getString("servicio_id"));
                    StringBuilder builder = StringBuilderUtil.getInstance();
                    builder.append(servicio.getString("servicio_fecha")).append(" ").append(servicio.getString("servicio_hora"));
                    textviewFechaValor.setText(builder.toString());
                    textviewClienteValor.setText(servicio.getString("servicio_cliente"));
                    textviewRutaValor.setText(servicio.getString("servicio_ruta"));
                    textviewTarifaValor.setText(servicio.getString("servicio_tarifa"));
                    textviewObservacionValor.setText(servicio.getString("servicio_observacion").equals("") ? "Sin observaciones" : servicio.getString("servicio_observacion"));
                    cantidad++;
                }
                String nombre = servicio.getString("servicio_pasajero_nombre");
                String celular = servicio.getString("servicio_pasajero_celular");
                String destino = servicio.getString("servicio_destino");
                StringBuilder builder = StringBuilderUtil.getInstance();
                builder.append(nombre).append("%").append(celular).append("%").append(destino);
                lista.add(builder.toString());
            }
            textviewCantidadValor.setText(Integer.toString(cantidad));
            if (lista.size() > 0) {
                String[] array = new String[lista.size()];
                array = lista.toArray(array);
                ReciclerViewDetalleAdapter mAdapter = new ReciclerViewDetalleAdapter(context.get(), array);
                recyclerViewDetalle.setAdapter(mAdapter);
            }

        } catch (Exception e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(Conductor.getInstance().id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!context.get().isDestroyed()) {

                }
            }
        });
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void voids) {
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
