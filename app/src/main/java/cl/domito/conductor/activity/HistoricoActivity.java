package cl.domito.conductor.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import cl.domito.conductor.R;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.thread.ObtenerHistorialOperation;

public class HistoricoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView imageViewAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);
        imageViewAtras = findViewById(R.id.imageViewAtras);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewHistorial);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Conductor.getInstance().setContext(getApplicationContext());

        ObtenerHistorialOperation obtenerHistorialOperation = new ObtenerHistorialOperation(this);
        obtenerHistorialOperation.execute();

        imageViewAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Conductor.getInstance().setVolver(true);
        super.onBackPressed();
    }

    private void volver()
    {
        Conductor.getInstance().setVolver(true);
        this.finish();
    }

}
