package cl.domito.conductor.activity.adapter;

import android.app.Activity;
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

import org.json.JSONArray;
import org.json.JSONObject;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.ServicioDetalleActivity;
import cl.domito.conductor.dominio.Conductor;

public class ReciclerViewProgramadoAdapter extends RecyclerView.Adapter<ReciclerViewProgramadoAdapter.MyViewHolder> {

    Activity activity;
    private String[] mDataset;

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
            textViewCliente = v.findViewById(R.id.textViewCliente);
            relativeLayout3 = v.findViewById(R.id.relativeLayout3);
        }
    }

    public ReciclerViewProgramadoAdapter(Activity activity, String[] myDataset) {
        this.activity = activity;
        mDataset = myDataset;
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
                JSONArray servicios = conductor.getServicios();
                if (servicios != null) {
                    for (int i = 0; i < servicios.length(); i++) {
                        try {
                            JSONObject servicioAux = conductor.getServicios().getJSONObject(i);
                            String idServicio = vh.textView.getText().toString();
                            String idAux = servicioAux.getString("servicio_id");
                            if (idAux.equals(idServicio)) {
                                servicio.put(servicioAux);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    conductor.setServicio(servicio);
                    Intent intent = new Intent(activity, ServicioDetalleActivity.class);
                    intent.putExtra("fecha", vh.textViewFecha.getText().toString());
                    intent.putExtra("id", vh.textView.getText().toString());
                    activity.startActivity(intent);
                    activity.finish();
                }
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
            imagen = resources.getDrawable(R.drawable.confirmar);
            color = resources.getColor(R.color.naranjo);
        }
        else if (estado.equals("3"))
        {
            imagen = resources.getDrawable(R.drawable.arriba);
            color = resources.getColor(R.color.verde);
        }
        else if (estado.equals("4"))
        {
            imagen = resources.getDrawable(R.drawable.furgon);
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
                imagen = resources.getDrawable(R.drawable.arriba);
                color = resources.getColor(R.color.verde);
            }
            else if (estado.equals("3"))
            {
                imagen = resources.getDrawable(R.drawable.confirmar);
                color = resources.getColor(R.color.naranjo);
            }
            else if (estado.equals("4"))
            {
                imagen = resources.getDrawable(R.drawable.furgon);
                color = resources.getColor(R.color.azul);
            }
        }
        myViewHolder.imageView.setImageDrawable(imagen);
        myViewHolder.textView.setText(data[0]);
        myViewHolder.textView.setTextColor(color);
        myViewHolder.textViewFecha.setText(data[1] + " " + data[2]);
        myViewHolder.textViewCliente.setText(data[3]);



    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}
