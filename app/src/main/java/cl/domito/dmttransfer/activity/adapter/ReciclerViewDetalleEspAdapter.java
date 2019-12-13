package cl.domito.dmttransfer.activity.adapter;

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

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;

public class ReciclerViewDetalleEspAdapter extends RecyclerView.Adapter<ReciclerViewDetalleEspAdapter.MyViewHolder> {

    Activity activity;
    private String[] mDataset;
    private static String nombre;
    private static String celular;
    private static String partida;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewNombre;
        public TextView textViewDireccion;
        public TextView textViewDestino;
        public ImageView imageViewLlamar;
        public MyViewHolder(View v) {
            super(v);
            textViewNombre = v.findViewById(R.id.textviewId);
            textViewDireccion = v.findViewById(R.id.textviewDireccion);
            textViewDestino = v.findViewById(R.id.textViewDestino);
            imageViewLlamar = v.findViewById(R.id.imageView2);
        }
    }

    public ReciclerViewDetalleEspAdapter(Activity activity, String[] myDataset) {
        this.activity = activity;
        mDataset = myDataset;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_detalle_esp, viewGroup, false);
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
        if (data[0].split("-").length > 1 && data[0].split("-")[1].equals("")) {
        myViewHolder.imageViewLlamar.setVisibility(View.GONE);
    } else {
        myViewHolder.imageViewLlamar.setTag(data[1]);
    }
        myViewHolder.textViewNombre.setText(data[0].split("-")[0]);
        myViewHolder.textViewDireccion.setText(data[2]);
        if(data.length == 4) {
            myViewHolder.textViewDestino.setText(data[3]);
        }
        else{
            myViewHolder.textViewDestino.setText("");
        }

    String nombre = activity.getComponentName().getClassName();
        if(nombre.equals("cl.domito.dmttransfer.activity.HistoricoDetalleActivity"))
    {
        myViewHolder.imageViewLlamar.setVisibility(View.GONE);
    }
}

    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}
