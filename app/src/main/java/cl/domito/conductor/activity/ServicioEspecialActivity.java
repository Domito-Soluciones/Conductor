package cl.domito.conductor.activity;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.adapter.ReciclerViewEspecialAdapter;
import cl.domito.conductor.activity.adapter.ReciclerViewProgramadoAdapter;
import cl.domito.conductor.dominio.Conductor;

public class ServicioEspecialActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView imageViewAtras;
    private ImageView imageViewAtrasInt;

    ConstraintLayout constraintLayoutEspecial;
    ConstraintLayout constraintLayoutDetalle;
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicio_especial);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        imageViewAtras = findViewById(R.id.imageViewAtras);
        imageViewAtrasInt = findViewById(R.id.imageViewAtrasInt);
        constraintLayoutEspecial = findViewById(R.id.constrainLayoutEspecial);
        constraintLayoutDetalle = findViewById(R.id.constrainLayoutServicio);
        JSONArray jsonArray = Conductor.getInstance().getServiciosEspeciales();
        ArrayList<String> lista = new ArrayList();
        String ant = "";
        for(int i = 0; i < jsonArray.length(); i++)
        {
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String servicioId = jsonObject.getString("servicio_id");
                String servicioPasajero = jsonObject.getString("servicio_pasajero");
                String servicioFecha = jsonObject.getString("servicio_fecha");
                String servicioEstado = jsonObject.getString("servicio_estado");
                Date date = format1.parse(servicioFecha);
                String servicioHora = jsonObject.getString("servicio_hora");
                lista.add( servicioId + "%" + servicioPasajero + "%" + format2.format(date) +" "+ servicioHora + "%" + servicioEstado );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(lista.size() > 0 ) {
            String[] array = new String[lista.size()];
            array = lista.toArray(array);
            mAdapter = new ReciclerViewEspecialAdapter(this, array);

            recyclerView.setAdapter(mAdapter);
        }
        else
        {
            Toast.makeText(this,"No hay servicios especiales",Toast.LENGTH_LONG).show();
        }
        imageViewAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });


    }

    private void volver()
    {
        this.finish();
    }


}
