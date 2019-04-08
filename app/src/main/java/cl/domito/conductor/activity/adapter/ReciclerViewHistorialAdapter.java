package cl.domito.conductor.activity.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.HistoricoActivity;
import cl.domito.conductor.activity.HistoricoDetalleActivity;
import cl.domito.conductor.dominio.Conductor;

public class ReciclerViewHistorialAdapter extends RecyclerView.Adapter<ReciclerViewHistorialAdapter.MyViewHolder> {

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

    public ReciclerViewHistorialAdapter(Activity activity, String[] myDataset) {
        this.activity = activity;
        mDataset = myDataset;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
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
        String estado = data[4];
        int color = 0;
        Resources resources = myViewHolder.textView.getContext().getResources();
        if(estado.equals("1"))
        {
            color = resources.getColor(R.color.naranjo);
        }
        else if (estado.equals("3"))
        {
            color = resources.getColor(R.color.verde);
        }
        else if(estado.equals("4"))
        {
            color = resources.getColor(R.color.azul);
        }
        else if(estado.equals("5"))
        {
            color = resources.getColor(R.color.negro);
        }
        myViewHolder.textView.setText(data[0]);
        myViewHolder.textView.setTextColor(color);
        myViewHolder.textViewFecha.setText(data[1] + " " + data[2]);
        myViewHolder.textViewCliente.setText(data[3]);

        myViewHolder.relativeLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity,HistoricoDetalleActivity.class);
                i.putExtra("idServicio",data[0]);
                activity.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}
