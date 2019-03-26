package cl.domito.conductor.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cl.domito.conductor.R;
import cl.domito.conductor.thread.IniciarServicioOperation;

public class PasajeroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasajero);
        IniciarServicioOperation iniciarServicioOperation = new IniciarServicioOperation(this);
        iniciarServicioOperation.execute();

    }

}
