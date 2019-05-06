package cl.domito.conductor.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
    private Conductor conductor;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);
        imageViewAtras = findViewById(R.id.imageViewAtras);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewHistorial);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        conductor = Conductor.getInstance();

        conductor.context = HistoricoActivity.this;

        imageViewAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        ObtenerHistorialOperation obtenerHistorialOperation = new ObtenerHistorialOperation(this);
        obtenerHistorialOperation.execute();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        conductor.volver = true;
        super.onBackPressed();
    }

    private void volver()
    {
        conductor.volver = true;
        this.finish();
    }

    private void refreshContent() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ObtenerHistorialOperation obtenerHistorialOperation = new ObtenerHistorialOperation(HistoricoActivity.this);
                obtenerHistorialOperation.execute();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
