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
import android.location.Location;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.FinServicioActivity;
import cl.domito.conductor.activity.PasajeroActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.thread.CambiarEstadoServicioOperation;
import cl.domito.conductor.thread.CancelarRutaPasajeroOperation;
import cl.domito.conductor.thread.FinalizarRutaPasajeroOperation;
import cl.domito.conductor.thread.IniciarServicioOperation;
import cl.domito.conductor.thread.ObtenerServicioOperation;
import cl.domito.conductor.thread.ObtenerServiciosOperation;
import cl.domito.conductor.thread.TomarPasajeroOperation;

public class ReciclerViewPasajeroAdapter extends RecyclerView.Adapter<ReciclerViewPasajeroAdapter.MyViewHolder> {

    Activity activity;
    private String[] mDataset;
    Conductor conductor;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewNombre;
        public TextView textViewDireccion;
        public ImageView imageViewLlamar;
        public ImageButton buttonIniciar;
        public ImageButton buttonTerminar;
        public ImageButton buttonCancelar;
        public ConstraintLayout constraintLayoutPasajero;
        public MyViewHolder(View v) {
            super(v);
            textViewNombre = v.findViewById(R.id.textviewId);
            textViewDireccion = v.findViewById(R.id.textviewDireccion);
            imageViewLlamar = v.findViewById(R.id.imageLlamar2);
            buttonIniciar = v.findViewById(R.id.imageButtonAceptar);
            buttonTerminar = v.findViewById(R.id.imageButtonTerminar);
            buttonCancelar = v.findViewById(R.id.imageButtonCancelar);
            constraintLayoutPasajero = v.findViewById(R.id.constraintLayoutTVPasajero);
        }
    }

    public ReciclerViewPasajeroAdapter(Activity activity, String[] myDataset) {
        this.activity = activity;
        mDataset = myDataset;
        conductor = Conductor.getInstance();
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_pasajero, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Spanned texto = Html.fromHtml(mDataset[i]);
        String[] data = mDataset[i].split("%");
        String nombrePasajero = data[0];
        String celularPasajero = data[1];
        String direccionPasajero = data[2];
        String estadoPasajero = data[3];
        String idPasajero = data[4];
        myViewHolder.textViewNombre.setText(nombrePasajero);
        myViewHolder.textViewDireccion.setText(direccionPasajero);
        Resources resources = myViewHolder.textViewNombre.getContext().getResources();
        if(estadoPasajero.equals("1") || (conductor.servicioActualRuta.contains("RG") && idPasajero.equals("0")))
        {
            Drawable imagen = resources.getDrawable(R.drawable.navegar);
            myViewHolder.buttonIniciar.setImageDrawable(imagen);
            myViewHolder.buttonTerminar.setVisibility(View.VISIBLE);
        }

        if(idPasajero.equals("0"))
        {
            myViewHolder.buttonCancelar.setVisibility(View.GONE);
            myViewHolder.imageViewLlamar.setVisibility(View.INVISIBLE);
        }

        myViewHolder.buttonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (estadoPasajero.equals("0")) {
                    if(idPasajero.equals("0"))
                    {
                        String ruta = conductor.servicioActualRuta;
                        if(ruta.contains("RG-"))
                        {
                            navegar(direccionPasajero);
                        }
                        else if(ruta.contains("ZP-"))
                        {
                            CambiarEstadoServicioOperation cambiarEstadoServicioOperation = new CambiarEstadoServicioOperation();
                            cambiarEstadoServicioOperation.execute(conductor.servicioActual, "4");

                        }
                    }
                    conductor.pasajeroActual = idPasajero;
                    TomarPasajeroOperation tomarPasajeroOperation = new TomarPasajeroOperation((PasajeroActivity) activity);
                    tomarPasajeroOperation.execute();
                    recargarPasajeros();
                } else if (estadoPasajero.equals("1")) {
                    myViewHolder.buttonTerminar.setVisibility(View.VISIBLE);
                    navegar(direccionPasajero);
                    if (myViewHolder.getAdapterPosition() == 0) {
                        CambiarEstadoServicioOperation cambiarEstadoServicioOperation = new CambiarEstadoServicioOperation();
                        cambiarEstadoServicioOperation.execute(conductor.servicioActual, "4");
                    }
                    if (myViewHolder.getAdapterPosition() == conductor.cantidadPasajeros - 1) {
                        conductor.navegando = false;
                    }
                } else if (estadoPasajero.equals("2")) {
                    Resources resources = myViewHolder.textViewDireccion.getContext().getResources();
                    int color = resources.getColor(R.color.naranjo);
                    myViewHolder.constraintLayoutPasajero.setBackgroundColor(color);
                } else if (estadoPasajero.equals("3")) {
                    Resources resources = myViewHolder.textViewDireccion.getContext().getResources();
                    int color = resources.getColor(R.color.naranjo);
                    myViewHolder.constraintLayoutPasajero.setBackgroundColor(color);
                }
            }
        });

        myViewHolder.buttonTerminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conductor.pasajeroActual = idPasajero;
                FinalizarRutaPasajeroOperation finalizarRutaPasajeroOperation = new FinalizarRutaPasajeroOperation((PasajeroActivity) activity);
                finalizarRutaPasajeroOperation.execute();
                String ruta = conductor.servicioActualRuta;
                if(ruta.contains("ZP") && i == getItemCount()- 1)
                {
                    finalizar();
                }
                else if(ruta.contains("RG") && i == getItemCount()- 1)
                {
                    finalizar();
                }
                else
                {
                    recargarPasajeros();
                }
            }
        });

        myViewHolder.imageViewLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.llamar(activity, "tel:" + celularPasajero);
            }
        });

        myViewHolder.buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(i == getItemCount()- 1) {
                    CancelarRutaPasajeroOperation cancelarRutaPasajeroOperation = new CancelarRutaPasajeroOperation();
                    cancelarRutaPasajeroOperation.execute();
                    Toast.makeText(activity, "Pasajero cancelado", Toast.LENGTH_SHORT).show();
                    recargarPasajeros();
                //}
                //else
                //{
                //    Toast.makeText(activity,"Priemr debe terminar con los pasajeros anteriores",Toast.LENGTH_SHORT).show();
                //}
            }
        });

}

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    private void navegar(String destino) {
        conductor.navegando = true;
        try {
            Geocoder geocoder = new Geocoder(activity);
            List<Address> addresses = geocoder.getFromLocationName(destino, 1);
            Location location = new Location("");
            location.setLatitude(addresses.get(0).getLatitude());
            location.setLongitude(addresses.get(0).getLongitude());
            conductor.locationDestino = location;
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

    public void recargarPasajeros()
    {
        ArrayList<String> lista = new ArrayList();
        String idServicio = conductor.servicioActual;
        try {
            ObtenerServicioOperation obtenerServicioOperation = new ObtenerServicioOperation();
            conductor.servicio = obtenerServicioOperation.execute(conductor.servicioActual).get();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(conductor.servicio != null) {
            try {
                JSONObject primero = conductor.servicio.getJSONObject(0);
                String ruta = primero.getString("servicio_truta").split("-")[0];
                if (ruta.equals("ZP")) {
                    String cliente = primero.getString("servicio_cliente");
                    String destino = primero.getString("servicio_cliente_direccion");
                    lista.add(cliente + "%%" + destino + "%0%0");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        for(int i =  0; i < conductor.servicio.length();i++ ) {
            try {
                JSONObject servicio = conductor.servicio.getJSONObject(i);
                if (servicio.getString("servicio_id").equals(idServicio)) {
                    String id = servicio.getString("servicio_pasajero_id");
                        String nombre = servicio.getString("servicio_pasajero_nombre");
                        String celular = servicio.getString("servicio_pasajero_celular");
                        String destino = servicio.getString("servicio_destino");
                        String estado = servicio.getString("servicio_pasajero_estado");
                        if (!estado.equals("3") && !estado.equals("2"))
                        {
                            lista.add(nombre + "%" + celular + "%" + destino + "%" + estado + "%" + id);
                        }
                }
            }
            catch(Exception e){e.printStackTrace();}
        }
        if(conductor.servicio != null) {
            try {
                JSONObject ultimo = conductor.servicio.getJSONObject(conductor.servicio.length() - 1);
                String ruta = ultimo.getString("servicio_truta").split("-")[0];
                if (ruta.equals("RG")) {
                    String cliente = ultimo.getString("servicio_cliente");
                    String destino = ultimo.getString("servicio_cliente_direccion");
                    lista.add(cliente + "%%" + destino + "%0%0");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if(lista.size() > 0 ) {
            String[] array = new String[lista.size()];
            array  = lista.toArray(array);
            ReciclerViewPasajeroAdapter mAdapter = new ReciclerViewPasajeroAdapter(activity,array);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RecyclerView recyclerView = activity.findViewById(R.id.recyclerViewPasajero);
                    recyclerView.setAdapter(mAdapter);
                }
            });
        }
    }

    private void finalizar()
    {
        try {
            JSONObject json = conductor.servicio.getJSONObject(0);
            Intent intent = new Intent(conductor.context, FinServicioActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("cliente", json.getString("servicio_cliente"));
            bundle.putString("fecha", json.getString("servicio_fecha"));
            bundle.putString("tarifa", json.getString("servicio_tarifa"));
            intent.putExtras(bundle);
            CambiarEstadoServicioOperation cambiarEstadoServicioOperation = new CambiarEstadoServicioOperation();
            cambiarEstadoServicioOperation.execute(conductor.servicioActual, "5");
            activity.finish();
            activity.startActivity(intent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
