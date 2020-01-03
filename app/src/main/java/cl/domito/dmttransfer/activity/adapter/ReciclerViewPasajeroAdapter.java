    package cl.domito.dmttransfer.activity.adapter;

    import android.app.Activity;
    import android.app.AlertDialog;
    import android.content.ActivityNotFoundException;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.content.res.Resources;
    import android.graphics.drawable.Drawable;
    import android.location.Address;
    import android.location.Geocoder;
    import android.location.Location;
    import android.net.Uri;
    import android.support.annotation.NonNull;
    import android.support.constraint.ConstraintLayout;
    import android.support.v7.widget.RecyclerView;
    import android.text.Html;
    import android.text.Spanned;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import org.json.JSONObject;

    import java.util.ArrayList;
    import java.util.List;

    import cl.domito.dmttransfer.R;
    import cl.domito.dmttransfer.activity.MainActivity;
    import cl.domito.dmttransfer.activity.PasajeroActivity;
    import cl.domito.dmttransfer.activity.utils.ActivityUtils;
    import cl.domito.dmttransfer.dominio.Conductor;
    import cl.domito.dmttransfer.service.BurbujaService;
    import cl.domito.dmttransfer.thread.CambiarEstadoServicioOperation;
    import cl.domito.dmttransfer.thread.CancelarRutaPasajeroOperation;
    import cl.domito.dmttransfer.thread.EnviarLogOperation;
    import cl.domito.dmttransfer.thread.FinalizarRutaPasajeroOperation;
    import cl.domito.dmttransfer.thread.FinalizarRutaPasajerosOperation;
    import cl.domito.dmttransfer.thread.IniciarServicioOperation;
    import cl.domito.dmttransfer.thread.ObtenerServicioOperation;
    import cl.domito.dmttransfer.thread.TomarPasajeroOperation;

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
            public ImageButton buttonManual;
            public ImageButton buttonCancelar;
            public ConstraintLayout constraintLayoutPasajero;
            public MyViewHolder(View v) {
                super(v);
                textViewNombre = v.findViewById(R.id.textviewId);
                textViewDireccion = v.findViewById(R.id.textviewDireccion);
                imageViewLlamar = v.findViewById(R.id.imageLlamar2);
                buttonIniciar = v.findViewById(R.id.imageButtonAceptar);
                buttonManual = v.findViewById(R.id.imageButtonManual);
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
            String nombrePasajero = data[0].split("-")[0].replace("_"," ");
            String celularPasajero = data[1];
            String direccionPasajero = data[2];
            String estadoPasajero = data[3];
            String idPasajero = data[4];
            myViewHolder.textViewNombre.setText(nombrePasajero);
            myViewHolder.textViewDireccion.setText(direccionPasajero);
            Resources resources = myViewHolder.textViewNombre.getContext().getResources();
            if(idPasajero.equals("0"))
            {
                myViewHolder.imageViewLlamar.setVisibility(View.INVISIBLE);
            }

            if(!celularPasajero.equals(""))
            {
                Drawable imagen = resources.getDrawable(R.drawable.telefono);
                myViewHolder.imageViewLlamar.setImageDrawable(imagen);
            }

            if(conductor.servicioActualRuta.contains("RG"))
            {
                if(i == 0)
                {
                    Drawable imagen = null;
                    if(conductor.pasajeroRecogido)
                    {
                        imagen = resources.getDrawable(R.drawable.confirmar);
                    }
                    else
                    {
                        imagen = resources.getDrawable(R.drawable.navegar);
                    }
                    myViewHolder.buttonIniciar.setImageDrawable(imagen);
                    myViewHolder.buttonManual.setVisibility(View.VISIBLE);

                    myViewHolder.buttonIniciar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.setVisibility(View.INVISIBLE);
                            conductor.pasajeroActual = idPasajero;
                            if (conductor.pasajeroRecogido){
                                if (!idPasajero.equals("0")) {
                                    conductor.pasajeroRecogido = false;
                                    TomarPasajeroOperation tomarPasajeroOperation = new TomarPasajeroOperation((PasajeroActivity) activity);
                                    tomarPasajeroOperation.execute();
                                } else {
                                    ActivityUtils.finalizar(activity);
                                }
                            } else {
                                navegar(direccionPasajero);
                            }
                        }

                    });

                    myViewHolder.buttonManual.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            conductor.pasajeroActual = idPasajero;
                            if (!idPasajero.equals("0")) {
                                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(activity);
                                dialogo1.setTitle("Dejar Pasajero");
                                dialogo1.setMessage("¿ Esta seguro que desea dejar al pasajero aquí ?");
                                dialogo1.setCancelable(false);
                                dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogo1, int id) {
                                        dialogo1.dismiss();
                                        conductor.pasajeroRecogido = false;
                                        TomarPasajeroOperation tomarPasajeroOperation = new TomarPasajeroOperation((PasajeroActivity) activity);
                                        tomarPasajeroOperation.execute("");
                                    }
                                });
                                dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogo1, int id) {
                                        dialogo1.dismiss();
                                    }
                                });
                                dialogo1.show();
                            } else {
                                ActivityUtils.finalizar(activity);
                            }
                        }

                    });

                }
                else
                {
                    myViewHolder.buttonIniciar.setVisibility(View.GONE);
                }

                myViewHolder.buttonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conductor.pasajeroRecogido = false;
                        conductor.pasajeroActual = idPasajero;
                        if (idPasajero.equals("0")) {
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(activity);
                            dialogo1.setTitle("Cancelar Servicio");
                            dialogo1.setMessage("¿ Esta seguro que desea cancelar el servicio ?");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    AlertDialog.Builder dialogo2 = new AlertDialog.Builder(activity);
                                    dialogo2.setTitle("Motivo Cancelación");
                                    dialogo2.setMessage("Ingrese motivo de cancelación");
                                    dialogo2.setCancelable(false);
                                    final EditText input = new EditText(activity);
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.MATCH_PARENT);
                                    input.setLayoutParams(lp);
                                    dialogo2.setView(input);
                                    dialogo2.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(!input.getText().toString().equals("")) {
                                                CambiarEstadoServicioOperation cambiarEstadoServicioOperation = new CambiarEstadoServicioOperation();
                                                cambiarEstadoServicioOperation.execute(conductor.servicioActual,"6",input.getText().toString());
                                                FinalizarRutaPasajerosOperation finalizarRutaPasajerosOperation = new FinalizarRutaPasajerosOperation(activity);
                                                finalizarRutaPasajerosOperation.execute("2");
                                                Toast.makeText(activity, "Servicio cancelado", Toast.LENGTH_SHORT).show();
                                                activity.finish();
                                            }
                                            else
                                            {
                                                Toast.makeText(activity,"Debe ingresar un motivo de cancelación",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    dialogo2.show();
                                }
                            });
                            dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    dialogo1.dismiss();
                                }
                            });
                            dialogo1.show();
                        } else {
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(activity);
                            dialogo1.setTitle("Cancelar Usuario");
                            dialogo1.setMessage("¿ Esta seguro que este pasajero no abordara ?");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                    final String[] items = new String[3];
                                    items[0] = "Pasajero no contactado";
                                    items[1] = "Pasajero enfermo";
                                    items[2] = "Otro motivo";
                                    builder.setTitle("Opciones").setSingleChoiceItems(items, -1,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if(!activity.isDestroyed()) {
                                                        dialog.dismiss();
                                                    }
                                                    if(which != 2) {
                                                        CancelarRutaPasajeroOperation cancelarRutaPasajeroOperation = new CancelarRutaPasajeroOperation(activity);
                                                        cancelarRutaPasajeroOperation.execute(items[which].toString());
                                                    }
                                                    else if(which == 2) {
                                                        AlertDialog.Builder dialogo2 = new AlertDialog.Builder(activity);
                                                        dialogo2.setTitle("Motivo Cancelación");
                                                        dialogo2.setMessage("Ingrese motivo de cancelación");
                                                        dialogo2.setCancelable(false);
                                                        final EditText input = new EditText(activity);
                                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                                LinearLayout.LayoutParams.MATCH_PARENT);
                                                        input.setLayoutParams(lp);
                                                        dialogo2.setView(input);
                                                        dialogo2.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if(!input.getText().toString().equals("")) {
                                                                    CancelarRutaPasajeroOperation cancelarRutaPasajeroOperation = new CancelarRutaPasajeroOperation(activity);
                                                                    cancelarRutaPasajeroOperation.execute(input.getText().toString());
                                                                }
                                                            }
                                                        });
                                                        dialogo2.show();
                                                    }
                                                }
                                            });
                                    builder.show();
                                }
                            });
                            dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    dialogo1.dismiss();
                                }
                            });
                            dialogo1.show();
                        }
                    }
                });

            }
            else if(conductor.servicioActualRuta.contains("ZP"))
            {
                Drawable imagen = null;
                if(estadoPasajero.equals("0"))
                {
                    imagen = resources.getDrawable(R.drawable.confirmar);
                }
                else if(estadoPasajero.equals("1"))
                {
                    myViewHolder.buttonCancelar.setVisibility(View.GONE);
                    if(i == 0)
                    {
                        myViewHolder.buttonManual.setVisibility(View.VISIBLE);
                    }
                    if(i > 0)
                    {
                        myViewHolder.buttonIniciar.setVisibility(View.GONE);
                    }
                    else if(conductor.pasajeroRepartido) {
                        imagen = resources.getDrawable(R.drawable.terminar);
                    }
                    else if(!conductor.pasajeroRepartido)
                    {
                        imagen = resources.getDrawable(R.drawable.navegar);
                    }
                }
                myViewHolder.buttonIniciar.setImageDrawable(imagen);
                myViewHolder.buttonIniciar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.INVISIBLE);
                        conductor.pasajeroActual = idPasajero;
                        if(idPasajero.equals("0")) {
                            conductor.zarpeIniciado = true;
                            IniciarServicioOperation iniciarServicioOperation = new IniciarServicioOperation(activity);
                            iniciarServicioOperation.execute();
                            CambiarEstadoServicioOperation cambiarEstadoServicioOperation = new CambiarEstadoServicioOperation();
                            cambiarEstadoServicioOperation.execute(conductor.servicioActual,"4","");
                        }
                        else
                        {
                            if(estadoPasajero.equals("0")) {
                                TomarPasajeroOperation tomarPasajeroOperation = new TomarPasajeroOperation(activity);
                                tomarPasajeroOperation.execute();
                                conductor.pasajeroRepartido = false;
                            }
                            else if(!estadoPasajero.equals("0") && i == 0 && !conductor.pasajeroRepartido)
                            {
                                navegar(direccionPasajero);
                            }
                            else if(!estadoPasajero.equals("0") && i == 0 && conductor.pasajeroRepartido)
                            {
                                conductor.pasajeroRepartido = false;
                                FinalizarRutaPasajeroOperation finalizarRutaPasajeroOperation = new FinalizarRutaPasajeroOperation(activity,i,getItemCount()-1);
                                finalizarRutaPasajeroOperation.execute();
                            }
                        }
                    }

                });

                myViewHolder.buttonManual.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conductor.pasajeroActual = idPasajero;
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(activity);
                        dialogo1.setTitle("Dejar Pasajero");
                        dialogo1.setMessage("¿ Esta seguro que desea dejar al pasajero aquí ?");
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                conductor.pasajeroRepartido = false;
                                FinalizarRutaPasajeroOperation finalizarRutaPasajeroOperation = new FinalizarRutaPasajeroOperation(activity,i,getItemCount()-1);
                                finalizarRutaPasajeroOperation.execute("");
                            }
                        });
                        dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                dialogo1.dismiss();
                            }
                        });
                        dialogo1.show();
                    }

                });

                myViewHolder.buttonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conductor.pasajeroActual = idPasajero;
                        if (idPasajero.equals("0")) {
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(activity);
                            dialogo1.setTitle("Cancelar Servicio");
                            dialogo1.setMessage("¿ Esta seguro que desea cancelar el servicio ?");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    AlertDialog.Builder dialogo2 = new AlertDialog.Builder(activity);
                                    dialogo2.setTitle("Motivo Cancelación");
                                    dialogo2.setMessage("Ingrese motivo de cancelación");
                                    dialogo2.setCancelable(false);
                                    final EditText input = new EditText(activity);
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.MATCH_PARENT);
                                    input.setLayoutParams(lp);
                                    dialogo2.setView(input);
                                    dialogo2.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(!input.getText().toString().equals("")) {
                                                CambiarEstadoServicioOperation cambiarEstadoServicioOperation = new CambiarEstadoServicioOperation();
                                                cambiarEstadoServicioOperation.execute(conductor.servicioActual,"6",input.getText().toString());
                                                FinalizarRutaPasajerosOperation finalizarRutaPasajerosOperation = new FinalizarRutaPasajerosOperation(activity);
                                                finalizarRutaPasajerosOperation.execute("2");
                                                activity.finish();
                                            }
                                            else
                                            {
                                                Toast.makeText(activity,"Debe ingresar un motivo de cancelación",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    dialogo2.show();
                                }
                            });
                            dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    dialogo1.dismiss();
                                }
                            });
                            dialogo1.show();
                        } else {
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(activity);
                            dialogo1.setTitle("Cancelar Usuario");
                            dialogo1.setMessage("¿ Esta seguro que este pasajero no abordara ?");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                    final CharSequence[] items = new CharSequence[3];
                                    items[0] = "Pasajero no contactado";
                                    items[1] = "Pasajero enfermo";
                                    items[2] = "Otro motivo";
                                    builder.setTitle("Opciones").setSingleChoiceItems(items, -1,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if(!activity.isDestroyed()) {
                                                        dialog.dismiss();
                                                    }
                                                    if(which != 2)
                                                    {
                                                        CancelarRutaPasajeroOperation cancelarRutaPasajeroOperation = new CancelarRutaPasajeroOperation(activity);
                                                        cancelarRutaPasajeroOperation.execute(items[which].toString());
                                                    }
                                                    if(which == 2) {
                                                        AlertDialog.Builder dialogo2 = new AlertDialog.Builder(activity);
                                                        dialogo2.setTitle("Motivo Cancelación");
                                                        dialogo2.setMessage("Ingrese motivo de cancelación");
                                                        dialogo2.setCancelable(false);
                                                        final EditText input = new EditText(activity);
                                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                                LinearLayout.LayoutParams.MATCH_PARENT);
                                                        input.setLayoutParams(lp);
                                                        dialogo2.setView(input);
                                                        dialogo2.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if(!input.getText().toString().equals("")) {
                                                                    CancelarRutaPasajeroOperation cancelarRutaPasajeroOperation = new CancelarRutaPasajeroOperation(activity);
                                                                    cancelarRutaPasajeroOperation.execute(input.getText().toString());
                                                                }
                                                            }
                                                        });

                                                        if(!activity.isDestroyed()) {
                                                            dialogo2.show();
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                }
                                            });
                                    builder.show();
                                }
                            });
                            dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    dialogo1.dismiss();
                                }
                            });
                            dialogo1.show();
                        }
                    }
                });
            }
            else if(conductor.servicioActualRuta.contains("ESP"))
            {
                if(i == 0)
                {
                    Drawable imagen = null;
                    if(conductor.pasajeroRecogido)
                    {
                        imagen = resources.getDrawable(R.drawable.confirmar);
                    }
                    else
                    {
                        imagen = resources.getDrawable(R.drawable.navegar);
                    }
                    myViewHolder.buttonIniciar.setImageDrawable(imagen);
                    myViewHolder.buttonManual.setVisibility(View.VISIBLE);

                    myViewHolder.buttonIniciar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.setVisibility(View.INVISIBLE);
                            conductor.pasajeroActual = idPasajero;
                            if (conductor.pasajeroRecogido){
                                conductor.pasajeroRecogido = false;
                                TomarPasajeroOperation tomarPasajeroOperation = new TomarPasajeroOperation((PasajeroActivity) activity);
                                tomarPasajeroOperation.execute();
                                if (i == getItemCount() -1) {
                                    ActivityUtils.finalizar(activity);
                                    activity.finish();
                                }
                            } else {
                                navegar(direccionPasajero);
                            }
                        }

                    });

                }
                else
                {
                    myViewHolder.buttonIniciar.setVisibility(View.GONE);
                }

                myViewHolder.buttonManual.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conductor.pasajeroActual = idPasajero;
                        if (!idPasajero.equals("0")) {
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(activity);
                            dialogo1.setTitle("Dejar Pasajero");
                            dialogo1.setMessage("¿ Esta seguro que desea dejar al pasajero aquí ?");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    conductor.pasajeroRecogido = false;
                                    TomarPasajeroOperation tomarPasajeroOperation = new TomarPasajeroOperation((PasajeroActivity) activity);
                                    tomarPasajeroOperation.execute();
                                    if (i == getItemCount() -1) {
                                        ActivityUtils.finalizar(activity);
                                        activity.finish();
                                    }
                                }
                            });
                            dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    dialogo1.dismiss();
                                }
                            });
                            dialogo1.show();
                        } else {
                            ActivityUtils.finalizar(activity);
                        }
                    }

                });

                myViewHolder.buttonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conductor.pasajeroRecogido = false;
                        conductor.pasajeroActual = idPasajero;
                        if (idPasajero.equals("0")) {
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(activity);
                            dialogo1.setTitle("Cancelar Servicio");
                            dialogo1.setMessage("¿ Esta seguro que desea cancelar el servicio ?");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    AlertDialog.Builder dialogo2 = new AlertDialog.Builder(activity);
                                    dialogo2.setTitle("Motivo Cancelación");
                                    dialogo2.setMessage("Ingrese motivo de cancelación");
                                    dialogo2.setCancelable(false);
                                    final EditText input = new EditText(activity);
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.MATCH_PARENT);
                                    input.setLayoutParams(lp);
                                    dialogo2.setView(input);
                                    dialogo2.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(!input.getText().toString().equals("")) {
                                                CambiarEstadoServicioOperation cambiarEstadoServicioOperation = new CambiarEstadoServicioOperation();
                                                cambiarEstadoServicioOperation.execute(conductor.servicioActual,"6",input.getText().toString());
                                                FinalizarRutaPasajerosOperation finalizarRutaPasajerosOperation = new FinalizarRutaPasajerosOperation(activity);
                                                finalizarRutaPasajerosOperation.execute("2");
                                                Toast.makeText(activity, "Servicio cancelado", Toast.LENGTH_SHORT).show();
                                                activity.finish();
                                            }
                                        }
                                    });
                                    dialogo2.show();
                                }
                            });
                            dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    dialogo1.dismiss();
                                }
                            });
                            dialogo1.show();
                        } else {
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(activity);
                            dialogo1.setTitle("Cancelar Usuario");
                            dialogo1.setMessage("¿ Esta seguro que este pasajero no abordara ?");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                    final String[] items = new String[3];
                                    items[0] = "Pasajero no contactado";
                                    items[1] = "Pasajero enfermo";
                                    items[2] = "Otro motivo";
                                    builder.setTitle("Opciones").setSingleChoiceItems(items, -1,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if(!activity.isDestroyed()) {
                                                        dialog.dismiss();
                                                    }
                                                    if(which != 2) {
                                                        CancelarRutaPasajeroOperation cancelarRutaPasajeroOperation = new CancelarRutaPasajeroOperation(activity);
                                                        cancelarRutaPasajeroOperation.execute(items[which].toString());
                                                    }
                                                    else if(which == 2) {
                                                        AlertDialog.Builder dialogo2 = new AlertDialog.Builder(activity);
                                                        dialogo2.setTitle("Motivo Cancelación");
                                                        dialogo2.setMessage("Ingrese motivo de cancelación");
                                                        dialogo2.setCancelable(false);
                                                        final EditText input = new EditText(activity);
                                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                                LinearLayout.LayoutParams.MATCH_PARENT);
                                                        input.setLayoutParams(lp);
                                                        dialogo2.setView(input);
                                                        dialogo2.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if(!input.getText().toString().equals("")) {
                                                                    CancelarRutaPasajeroOperation cancelarRutaPasajeroOperation = new CancelarRutaPasajeroOperation(activity);
                                                                    cancelarRutaPasajeroOperation.execute(input.getText().toString());
                                                                }
                                                            }
                                                        });
                                                        if(!activity.isDestroyed()) {
                                                            dialogo2.show();
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                }
                                            });
                                    builder.show();
                                }
                            });
                            dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    dialogo1.dismiss();
                                }
                            });
                            dialogo1.show();
                        }
                    }
                });

            }

            myViewHolder.imageViewLlamar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.llamar(activity, "tel:" + celularPasajero);
                }
            });
    }

        @Override
        public int getItemCount() {
            return mDataset.length;
        }

        private void navegar(String destino) {
            activity.startService(new Intent(activity, BurbujaService.class));
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
                    String paquete = "";
                    if(tipoNav.equals("google"))
                    {
                        uri = "google.navigation:q="+addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude();
                        paquete = "com.google.android.apps.maps";
                    }
                    else if(tipoNav.equals("") || tipoNav.equals("waze"))
                    {
                        uri = "geo: " + addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude();
                        paquete = "com.waze";
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage(paquete);
                activity.startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                    activity.startActivity(intent);
                    EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                    enviarLogOperation.execute(conductor.id,ex.getMessage(),ex.getClass().getName(),ex.getStackTrace()[0].getLineNumber()+"");
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
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
                EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
            }
            if(conductor.servicio != null) {
                try {
                    JSONObject primero = conductor.servicio.getJSONObject(0);
                    String ruta = primero.getString("servicio_truta").split("-")[0];
                    if (primero.getString("servicio_estado").equals("4"))
                    {
                        conductor.zarpeIniciado = true;
                    }
                    if ((ruta.equals("ZP") && !conductor.zarpeIniciado)){
                        String cliente = primero.getString("servicio_cliente");
                        String destino = primero.getString("servicio_cliente_direccion");
                        lista.add(cliente + "%%" + destino + "%0%0");
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                    enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
                }
            }
            if(conductor.servicio != null) {
                for (int i = 0; i < conductor.servicio.length(); i++) {
                    try {
                        JSONObject servicio = conductor.servicio.getJSONObject(i);
                        if (servicio.getString("servicio_id").equals(idServicio)) {
                            String id = servicio.getString("servicio_pasajero_id");
                            String nombre = servicio.getString("servicio_pasajero_nombre");
                            String celular = servicio.getString("servicio_pasajero_celular");
                            String destino = servicio.getString("servicio_destino");
                            String estado = servicio.getString("servicio_pasajero_estado");
                            if (servicio.getString("servicio_truta").contains("ZP")) {
                                if (!estado.equals("3") && !estado.equals("2")) {
                                    lista.add(nombre + "%" + celular + "%" + destino + "%" + estado + "%" + id);
                                }
                            } else if (servicio.getString("servicio_truta").contains("RG")) {
                                if (!estado.equals("3") && !estado.equals("2") && !estado.equals("1")) {
                                    lista.add(nombre + "%" + celular + "%" + destino + "%" + estado + "%" + id);
                                }
                            }
                            else if(servicio.getString("servicio_truta").contains("XX"))
                            {
                                if (!estado.equals("3") && !estado.equals("2") && !estado.equals("1")) {
                                    if(!destino.equals("")) {
                                        lista.add(nombre + "%" + celular + "%" + destino + "%" + estado + "%" + id);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                        enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
                    }
                }
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
                    EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                    enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
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
                        recyclerView.setItemViewCacheSize(20);
                        recyclerView.setDrawingCacheEnabled(true);
                        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                        recyclerView.setAdapter(mAdapter);
                    }
                });
            }
            else if(!conductor.servicioActualRuta.contains("XX"))
            {
                /*AlertDialog.Builder dialogo2 = new AlertDialog.Builder(activity);
                dialogo2.setTitle("Motivo Cancelación");
                dialogo2.setMessage("Ingrese motivo de cancelación");
                dialogo2.setCancelable(false);
                final EditText input = new EditText(activity);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                dialogo2.setView(input);
                dialogo2.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!input.getText().toString().equals("")) {
                            activity.finish();
                            CambiarEstadoServicioOperation cambiarEstadoServicioOperation = new CambiarEstadoServicioOperation();
                            cambiarEstadoServicioOperation.execute(conductor.servicioActual,"6",input.getText().toString());
                            conductor.zarpeIniciado = false;
                            Toast.makeText(activity,"Servicio cancelado",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialogo2.show();*/
            }
        }

    }
