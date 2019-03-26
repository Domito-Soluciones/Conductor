package cl.domito.conductor.activity.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.FinServicioActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.thread.CambiarEstadoServicioOperation;
import cl.domito.conductor.thread.CancelarRutaPasajeroOperation;
import cl.domito.conductor.thread.FinalizarRutaPasajeroOperation;
import cl.domito.conductor.thread.TomarPasajeroOperation;

public class ReciclerViewPasajeroAdapter extends RecyclerView.Adapter<ReciclerViewPasajeroAdapter.MyViewHolder> {

    Activity activity;
    private String[] mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewNombre;
        public TextView textViewDireccion;
        public ImageView imageViewLlamar;
        public ImageButton buttonIniciar;
        public ImageButton buttonCancelar;
        public ConstraintLayout constraintLayoutPasajero;
        public MyViewHolder(View v) {
            super(v);
            textViewNombre = v.findViewById(R.id.textviewId);
            textViewDireccion = v.findViewById(R.id.textviewDireccion);
            imageViewLlamar = v.findViewById(R.id.imageLlamar2);
            buttonIniciar = v.findViewById(R.id.imageButton);
            buttonCancelar = v.findViewById(R.id.imageButton2);
            constraintLayoutPasajero = v.findViewById(R.id.constraintLayoutTVPasajero);
        }
    }

    public ReciclerViewPasajeroAdapter(Activity activity, String[] myDataset) {
        this.activity = activity;
        mDataset = myDataset;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_pasajero, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(v);
        vh.buttonIniciar.setVisibility(View.GONE);
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
        myViewHolder.textViewNombre.setText(data[0]);
        myViewHolder.textViewDireccion.setText(data[2]);
        String celular = data[1];
        String estado = data[3];
        Resources resources = myViewHolder.textViewNombre.getContext().getResources();
        if(estado.equals("1"))
        {
            Drawable imagen = resources.getDrawable(R.drawable.navegar);
            myViewHolder.buttonIniciar.setImageDrawable(imagen);
        }
        String idPasajero = data[4];
        int index = Conductor.getInstance().getIndiceViaje();
        if(index+1 > mDataset.length)
        {
            CambiarEstadoServicioOperation cambiarEstadoServicioOperation = new CambiarEstadoServicioOperation();
            cambiarEstadoServicioOperation.execute(Conductor.getInstance().getServicioActual(),"5");
            Conductor.getInstance().getListaDestinos().clear();
            Intent intent = activity.getIntent();
            activity.finish();
            activity.startActivity(intent);
            Intent mainIntent = new Intent(Conductor.getInstance().getContext(), FinServicioActivity.class);
            activity.startActivity(mainIntent);
        }
        if(index == myViewHolder.getAdapterPosition())
        {
            myViewHolder.buttonIniciar.setVisibility(View.VISIBLE);
            myViewHolder.buttonIniciar.setOnClickListener(new View.OnClickListener() {
               @Override
                public void onClick(View v) {
                   if(estado.equals("0")) {
                       TomarPasajeroOperation tomarPasajeroOperation = new TomarPasajeroOperation();
                       tomarPasajeroOperation.execute();
                   }
                   else if(estado.equals("1"))
                   {
                       navegar(myViewHolder.getAdapterPosition());
                       String pasajero = Conductor.getInstance().getPasajeroActual();
                       if (pasajero != null) {
                           FinalizarRutaPasajeroOperation finalizarRutaPasajeroOperation = new FinalizarRutaPasajeroOperation();
                           finalizarRutaPasajeroOperation.execute();
                       }
                       Conductor.getInstance().setPasajeroActual(idPasajero);
                       if (myViewHolder.getAdapterPosition() == 0) {
                           CambiarEstadoServicioOperation cambiarEstadoServicioOperation = new CambiarEstadoServicioOperation();
                           cambiarEstadoServicioOperation.execute(Conductor.getInstance().getServicioActual(), "4");
                       }
                       if (myViewHolder.getAdapterPosition() == Conductor.getInstance().getCantidadPasajeros() - 1) {
                           Conductor.getInstance().setNavegando(false);
                       }
                   }
                   else if (estado.equals("2"))
                   {

                   }
                }
            });

            myViewHolder.imageViewLlamar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.llamar(activity,"tel:"+celular);
                }
            });

            myViewHolder.buttonCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myViewHolder.constraintLayoutPasajero.setVisibility(View.GONE);
                    Conductor.getInstance().setPasajeroActual(idPasajero);
                    CancelarRutaPasajeroOperation cancelarRutaPasajeroOperation = new CancelarRutaPasajeroOperation();
                    cancelarRutaPasajeroOperation.execute();
                }
            });
        }
        else
        {
            if(i < Conductor.getInstance().getIndiceViaje()) {
                myViewHolder.constraintLayoutPasajero.setVisibility(View.GONE);
            }
        }
}

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    private void navegar(int index) {
        Conductor conductor = Conductor.getInstance();
        conductor.setNavegando(true);
        String destino = conductor.getListaDestinos().get(index);
        conductor.setIndiceViaje(index + 1);
        try {
            Geocoder geocoder = new Geocoder(activity);
            List<Address> addresses = geocoder.getFromLocationName(destino, 1);
            try {
                String uri = null;
                SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                String tipoNav = pref.getString("nav", "");
                if(tipoNav.equals("google"))
                {
                    uri = "google.navigation:q="+addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude();
                }
                else if(tipoNav.equals("") || tipoNav.equals("waze"))
                {
                    uri = "geo: " + addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                activity.startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                activity.startActivity(intent);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
