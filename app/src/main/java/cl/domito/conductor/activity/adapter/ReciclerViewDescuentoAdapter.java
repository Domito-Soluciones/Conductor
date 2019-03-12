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

public class ReciclerViewDescuentoAdapter extends RecyclerView.Adapter<ReciclerViewDescuentoAdapter.MyViewHolder> {

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

    public ReciclerViewDescuentoAdapter(Activity activity, String[] myDataset) {
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
                Intent intent = new Intent(activity, ServicioDetalleActivity.class);
                intent.putExtra("fecha",vh.textViewFecha.getText().toString());
                intent.putExtra("id",vh.textView.getText().toString());
                activity.startActivity(intent);
                activity.finish();
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
        imagen = resources.getDrawable(R.drawable.arriba);
        color = resources.getColor(R.color.verde);
        Bundle bundle = activity.getIntent().getExtras();
        String intentId = null;
        String tipo = null;
        if(bundle != null) {
            intentId = bundle.getString("idServicio");
            tipo = activity.getIntent().getExtras().getString("accion");
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
