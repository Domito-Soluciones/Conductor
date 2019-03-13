package cl.domito.conductor.activity.adapter;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.utils.ActivityUtils;

public class ReciclerViewDetalleAdapter extends RecyclerView.Adapter<ReciclerViewDetalleAdapter.MyViewHolder> {

    Activity activity;
    private String[] mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewNombre;
        public TextView textViewDireccion;
        public ImageView imageViewLlamar;
        public MyViewHolder(View v) {
            super(v);
            textViewNombre = v.findViewById(R.id.textviewId);
            textViewDireccion = v.findViewById(R.id.textviewDireccion);
            imageViewLlamar = v.findViewById(R.id.imageView2);
        }
    }

    public ReciclerViewDetalleAdapter(Activity activity,String[] myDataset) {
        this.activity = activity;
        mDataset = myDataset;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_detalle, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(v);
        vh. imageViewLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ActivityUtils.llamar(activity,"tel:"+v.getTag().toString());
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
        if(data[1].equals(""))
        {
            myViewHolder.imageViewLlamar.setVisibility(View.GONE);
        }
        else {
            myViewHolder.imageViewLlamar.setTag(data[1]);
        }
        myViewHolder.textViewNombre.setText(data[0]);
        myViewHolder.textViewDireccion.setText(data[2]);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}
