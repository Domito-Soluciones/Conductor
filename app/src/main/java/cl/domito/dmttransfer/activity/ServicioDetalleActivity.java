package cl.domito.dmttransfer.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.adapter.ReciclerViewDetalleAdapter;
import cl.domito.dmttransfer.activity.adapter.ReciclerViewDetalleEspAdapter;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.activity.utils.StringBuilderUtil;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.Utilidades;
import cl.domito.dmttransfer.thread.CambiarEstadoServicioOperation;
import cl.domito.dmttransfer.thread.DesAsignarServicioOperation;
import cl.domito.dmttransfer.thread.EnviarLogOperation;
import cl.domito.dmttransfer.thread.RealizarServicioOperation;

public class ServicioDetalleActivity extends AppCompatActivity {

    private ImageView imageViewAtrasInt;
    private RecyclerView recyclerViewDetalle;
    private RecyclerView.Adapter mAdapterDetalle;
    private RecyclerView.LayoutManager layoutManagerDetalle;
    private ImageButton buttonConfirmar;
    private ImageButton buttonCancelar;
    private TextView textviewServicioValor;
    private TextView textviewFechaValor;
    private TextView textViewHoraValor;
    private TextView textviewClienteValor;
    private TextView textviewRutaValor;
    private TextView textviewTRutaValor;
    private TextView textviewTarifaValor;
    private TextView textviewCantidadValor;
    private TextView textviewObservacionValor;
    private String estado = "";
    private Conductor conductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicio_detalle);

        recyclerViewDetalle = (RecyclerView) findViewById(R.id.recyclerViewDetalle);
        recyclerViewDetalle.setHasFixedSize(true);
        layoutManagerDetalle = new LinearLayoutManager(this);
        recyclerViewDetalle.setLayoutManager(layoutManagerDetalle);
        imageViewAtrasInt = findViewById(R.id.imageViewAtrasInt);
        buttonConfirmar = findViewById(R.id.imageButton);
        buttonCancelar = findViewById(R.id.imageButton2);
        textviewServicioValor = findViewById(R.id.textViewIdServicioValor);
        textviewFechaValor = findViewById(R.id.textViewFechaValor);
        textViewHoraValor = findViewById(R.id.textViewHoraValor);
        textviewClienteValor = findViewById(R.id.textViewClienteValor);
        textviewRutaValor = findViewById(R.id.textViewRutaValor);
        textviewTRutaValor = findViewById(R.id.textViewTRutaValor);
        textviewTarifaValor = findViewById(R.id.textViewTarifaValor);
        textviewCantidadValor = findViewById(R.id.textViewCantidadValor);
        textviewObservacionValor = findViewById(R.id.textViewObservacionValor);

        conductor = Conductor.getInstance();

        conductor.context = ServicioDetalleActivity.this;

        buttonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aceptarServicio();
            }
        });

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desasignarServicio();
            }
        });

        imageViewAtrasInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
    }

    @Override
    public void onBackPressed() {
        volver();
        super.onBackPressed();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        obtenerServicios();
        super.onPostCreate(savedInstanceState);
    }

    private void volver() {
        String activity = getIntent().getExtras().getString("activity");
        if(!activity.equals("cl.domito.conductor.activity.MainActivity"))
        {
            conductor.volver = true;
        }
        this.finish();
    }

    private void aceptarServicio() {
        NotificationManager notificationManager = ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.cancelAll();
        if (estado.equals("1")) {
            RealizarServicioOperation realizarServicioOperation = new RealizarServicioOperation(this);
            realizarServicioOperation.execute();
        } else if (estado.equals("3")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date date = sdf.parse(textviewFechaValor.getText().toString()+" "+textViewHoraValor.getText());
                Long l = date.getTime();
                Date dateNow = new Date();
                Long lNow = dateNow.getTime();
                Long data = Math.abs(lNow - l);
                if (data <= 3.6e+6 || dateNow.after(date)) {
                    if(conductor.servicioActualRuta.contains("RG") || conductor.servicioActualRuta.contains("ESP")) {
                        CambiarEstadoServicioOperation cambiarEstadoServicioOperation = new CambiarEstadoServicioOperation();
                        cambiarEstadoServicioOperation.execute(conductor.servicioActual,"4","");
                    }
                    Intent mainIntent = new Intent(this, PasajeroActivity.class);
                    startActivity(mainIntent);
                    this.finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Falta mas de 1 hora para el inicio del servicio", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
            }
        }else if (estado.equals("4")) {
            Intent mainIntent = new Intent(this, PasajeroActivity.class);
            startActivity(mainIntent);
            this.finish();
        }
    }

    private void desasignarServicio() {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Rechazar Servicio");
        dialogo1.setMessage("¿ Esta seguro que desea rechazar este servicio?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                desasignar();
            }
        });
        dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.dismiss();
            }
        });
        dialogo1.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    // aqui no
                } else {

                }
                return;
            }
        }
    }

    private void llamar(String numero) {
        if (numero.equals("")) {
            Toast.makeText(this, "No se suministro un número de telefono", Toast.LENGTH_LONG);
        } else {
            ActivityUtils.llamar(this, numero);
        }
    }

    private void desasignar() {
        DesAsignarServicioOperation desAsignarServicioOperation = new DesAsignarServicioOperation(this);
        desAsignarServicioOperation.execute();
    }

    private void obtenerServicios() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = sdf.parse(getIntent().getExtras().getString("fecha"));
            Long l = date.getTime();
            Date dateNow = new Date();
            Long lNow = dateNow.getTime();
            Long data = Math.abs(lNow - l);
            try {
                int cantidad = 0;
                String nombreAux = "";
                String ruta = "";
                ArrayList<String> lista = new ArrayList();
                if (conductor.servicio != null) {
                    JSONObject primero = conductor.servicio.getJSONObject(0);
                   ruta = primero.getString("servicio_truta").split("-")[0];
                    if (ruta.equals("ZP")) {
                        String cliente = primero.getString("servicio_cliente");
                        String destino = primero.getString("servicio_cliente_direccion");
                        StringBuilder builder = StringBuilderUtil.getInstance();
                        builder.append(cliente).append("%%").append(destino);
                        lista.add(builder.toString());

                    }
                }
                for (int i = 0; i < conductor.servicio.length(); i++) {
                    JSONObject servicio = conductor.servicio.getJSONObject(i);
                    conductor.servicioActual = servicio.getString("servicio_id");
                    textviewServicioValor.setText(servicio.getString("servicio_id"));
                    textviewFechaValor.setText(servicio.getString("servicio_fecha"));
                    textViewHoraValor.setText(servicio.getString("servicio_hora"));
                    textviewClienteValor.setText(servicio.getString("servicio_cliente"));
                    textviewRutaValor.setText(servicio.getString("servicio_ruta"));
                    textviewTRutaValor.setText(servicio.getString("servicio_truta"));
                    textviewTarifaValor.setText(Utilidades.formatoMoneda(servicio.getString("servicio_tarifa")));
                    textviewObservacionValor.setText(servicio.getString("servicio_observacion").equals("") ? "Sin observaciones" : servicio.getString("servicio_observacion"));
                    estado = servicio.getString("servicio_estado");
                    String nombre = servicio.getString("servicio_pasajero_nombre");
                    String celular = servicio.getString("servicio_pasajero_celular");
                    String destino = servicio.getString("servicio_destino");
                    String cliente = servicio.getString("servicio_cliente_direccion");
                    conductor.servicioActual = servicio.getString("servicio_id");
                    conductor.servicioActualRuta = servicio.getString("servicio_truta");
                    String nombreComp = nombre.replace("_par","").replace("_des","");
                    if(!nombreComp.equals(nombreAux) && ruta.equals("XX")) {
                        nombreAux = nombreComp;
                        cantidad++;
                    }
                    else if(!ruta.equals("XX")){
                        cantidad++;
                    }
                    if(!destino.equals("")) {
                        StringBuilder builder = StringBuilderUtil.getInstance();
                        builder.append(nombre).append("%").append(celular).append("%").append(destino);
                        lista.add(builder.toString());
                    }

                }
                if (conductor.servicio != null) {
                    JSONObject ultimo = conductor.servicio.getJSONObject(conductor.servicio.length() - 1);
                    ruta = ultimo.getString("servicio_truta").split("-")[0];
                    if (ruta.equals("RG")) {
                        String cliente = ultimo.getString("servicio_cliente");
                        String destino = ultimo.getString("servicio_cliente_direccion");
                        StringBuilder builder = StringBuilderUtil.getInstance();
                        builder.append(cliente).append("%%").append(destino);
                        lista.add(builder.toString());
                    }
                }
                textviewCantidadValor.setText(Integer.toString(cantidad));
                conductor.cantidadPasajeros = cantidad;
                if (lista.size() > 0) {
                    if(conductor.servicioActualRuta.equals("XX-ESP")){
                        ArrayList<String> listaEspecial = new ArrayList();
                        int i = 0;
                        for(String dato : lista) {
                            String idAux = dato.split("%")[0];
                            String id2Aux = dato.split("%")[0].replace("_par","").replace("_des","");
                            if(idAux.endsWith("_par")){
                                listaEspecial.add(dato);
                                i++;
                            }
                            else if(idAux.endsWith("_des")){
                                if(listaEspecial.size() == 0){
                                    listaEspecial.add(dato);
                                }
                                else{
                                    StringBuilder builder = StringBuilderUtil.getInstance();
                                    builder.append(listaEspecial.get(i-1)).append("%%%").append(dato);
                                    listaEspecial.set(i-1,builder.toString());
                                }
                            }
                        }
                        String[] array = new String[listaEspecial.size()];
                        array = listaEspecial.toArray(array);
                        ReciclerViewDetalleEspAdapter mAdapter = new ReciclerViewDetalleEspAdapter(this, array);
                        recyclerViewDetalle.setAdapter(mAdapter);
                    }
                    else {
                        String[] array = new String[lista.size()];
                        array = lista.toArray(array);
                        ReciclerViewDetalleAdapter mAdapter = new ReciclerViewDetalleAdapter(this, array);
                        recyclerViewDetalle.setAdapter(mAdapter);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
            }

            if (estado.equals("1")) {
                buttonConfirmar.setImageResource(R.drawable.oknaranjo);
            } else if (estado.equals("3")) {
                buttonConfirmar.setImageResource(R.drawable.okverde);
            } else if (estado.equals("4")) {
                buttonConfirmar.setImageResource(R.drawable.okazul);
                buttonCancelar.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
    }

}
