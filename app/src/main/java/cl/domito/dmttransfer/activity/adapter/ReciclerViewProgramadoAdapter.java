package cl.domito.dmttransfer.activity.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.ServicioDetalleActivity;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.activity.utils.StringBuilderUtil;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.thread.EnviarLogOperation;

public class ReciclerViewProgramadoAdapter extends RecyclerView.Adapter<ReciclerViewProgramadoAdapter.MyViewHolder> {

    Activity activity;
    private String[] mDataset;
    private AlertDialog dialog;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        public TextView textView;
        public TextView textViewFecha;
        public TextView textViewCliente;
        public ConstraintLayout relativeLayout3;
        public MyViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.imageView2);
            textView = v.findViewById(R.id.textviewId);
            textViewFecha = v.findViewById(R.id.textviewNombre);
            textViewCliente = v.findViewById(R.id.textViewProduccion);
            relativeLayout3 = v.findViewById(R.id.relativeLayout3);
        }
    }

    public ReciclerViewProgramadoAdapter(Activity activity, String[] myDataset) {
        this.activity = activity;
        mDataset = myDataset;
        dialog = ActivityUtils.setProgressDialog(activity);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_programado, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(v);
        vh.relativeLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray servicio = new JSONArray();
                Conductor conductor = Conductor.getInstance();
                JSONArray servicios = conductor.servicios;
                if (servicios != null) {
                    for (int i = 0; i < servicios.length(); i++) {
                        try {
                            JSONObject servicioAux = conductor.servicios.getJSONObject(i);
                            String idServicio = vh.textView.getText().toString();
                            String idAux = servicioAux.getString("servicio_id");
                            if (idAux.equals(idServicio)) {
                                servicio.put(servicioAux);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
                        }
                    }
                    conductor.servicio = servicio;
                    if(conductor.servicio.length() == 0){
                        Toast.makeText(activity,"Este servicio ya no se encuentra disponible",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(!activity.isDestroyed()) {
                            try {
                                dialog.show();
                            }
                            catch(Exception e){

                            }

                        }
                        Intent intent = new Intent(activity, ServicioDetalleActivity.class);
                        intent.putExtra("fecha", vh.textViewFecha.getText().toString());
                        intent.putExtra("id", vh.textView.getText().toString());
                        intent.putExtra("activity", activity.getComponentName().getClassName());
                        dialog.dismiss();
                        activity.startActivity(intent);
                    }
                    //activity.finish();
                }
            }
        });
        vh.relativeLayout3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(activity, "Funco", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Spanned texto = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            texto = Html.fromHtml(mDataset[i],Html.FROM_HTML_MODE_LEGACY);
        } else {
            texto = Html.fromHtml(mDataset[i]);
        }
        String[] data = mDataset[i].split("%");
        String estado = data[4];
        int color = 0;
        Drawable imagen = null;
        Resources resources = myViewHolder.textView.getContext().getResources();
        if(estado.equals("1"))
        {
            imagen = resources.getDrawable(R.drawable.oknaranjo);
            color = resources.getColor(R.color.naranjo);
        }
        else if (estado.equals("3"))
        {
            imagen = resources.getDrawable(R.drawable.okverde);
            color = resources.getColor(R.color.verde);
        }
        else if (estado.equals("4"))
        {
            imagen = resources.getDrawable(R.drawable.okazul);
            color = resources.getColor(R.color.azul);
        }
        Bundle bundle = activity.getIntent().getExtras();
        String intentId = null;
        String tipo = null;
        if(bundle != null) {
            intentId = bundle.getString("idServicio");
            tipo = activity.getIntent().getExtras().getString("accion");
        }
        if(intentId != null && intentId.equals(data[0]))
        {
            if(tipo.equals("0")) {
                imagen = resources.getDrawable(R.drawable.okverde);
                color = resources.getColor(R.color.verde);
            }
            else if (estado.equals("3"))
            {
                imagen = resources.getDrawable(R.drawable.oknaranjo);
                color = resources.getColor(R.color.naranjo);
            }
            else if (estado.equals("4"))
            {
                imagen = resources.getDrawable(R.drawable.okazul);
                color = resources.getColor(R.color.azul);
            }
        }
        myViewHolder.imageView.setImageDrawable(imagen);
        myViewHolder.textView.setText(data[0]);
        myViewHolder.textView.setTextColor(color);
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(data[1]).append(" ").append(data[2]);
        myViewHolder.textViewFecha.setText(builder.toString());
        myViewHolder.textViewCliente.setText(data[3]);



    }

    @Override
    public int getItemCount() {
        if(mDataset == null){
            return 0;
        }
        return mDataset.length;
    }


}
