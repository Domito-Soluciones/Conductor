package cl.domito.dmttransfer.activity.adapter;

import android.app.Activity;
import android.content.res.Resources;
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

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.http.Utilidades;

public class ReciclerViewProduccionAdapter extends RecyclerView.Adapter<ReciclerViewProduccionAdapter.MyViewHolder> {

    Activity activity;
    TextView textViewTotal;
    private String[] mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        public TextView textView;
        public TextView textViewFecha;
        public TextView textViewProduccion;
        public ConstraintLayout relativeLayout3;
        public MyViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.imageView2);
            textView = v.findViewById(R.id.textviewId);
            textViewFecha = v.findViewById(R.id.textviewNombre);
            textViewProduccion = v.findViewById(R.id.textViewProduccion);
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
                .inflate(R.layout.recycler_view_produccion, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Spanned texto = Html.fromHtml(mDataset[i]);
        String[] data = mDataset[i].split("%");
        String fecha = data[0] + " " + data[1];
        Resources resources = myViewHolder.textView.getContext().getResources();
        //Drawable imagen = resources.getDrawable(R.drawable.arriba);
        Bundle bundle = activity.getIntent().getExtras();
        String intentId = null;
        String tipo = null;
        if(bundle != null) {
            intentId = bundle.getString("idServicio");
            tipo = activity.getIntent().getExtras().getString("accion");
        }
        myViewHolder.textView.setText(data[3]);
        myViewHolder.textViewFecha.setText(fecha);
        String aux = data[2];
        myViewHolder.textViewProduccion.setText("$ "+Utilidades.formatoMoneda(aux+""));
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}
