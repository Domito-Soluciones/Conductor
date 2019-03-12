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

import cl.domito.conductor.R;
import cl.domito.conductor.activity.ServicioDetalleActivity;

public class ReciclerViewProduccionAdapter extends RecyclerView.Adapter<ReciclerViewProduccionAdapter.MyViewHolder> {

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

    public ReciclerViewProduccionAdapter(Activity activity, String[] myDataset) {
        this.activity = activity;
        mDataset = myDataset;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_programado, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(v);
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
        String fecha = data[0] + " " + data[1];
        Resources resources = myViewHolder.textView.getContext().getResources();
        int color = resources.getColor(R.color.verde);
        Drawable imagen = resources.getDrawable(R.drawable.arriba);
        Bundle bundle = activity.getIntent().getExtras();
        String intentId = null;
        String tipo = null;
        if(bundle != null) {
            intentId = bundle.getString("idServicio");
            tipo = activity.getIntent().getExtras().getString("accion");
        }
        myViewHolder.imageView.setImageDrawable(imagen);
        myViewHolder.textViewFecha.setText(fecha);
        myViewHolder.textViewCliente.setText(data[2]);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}
