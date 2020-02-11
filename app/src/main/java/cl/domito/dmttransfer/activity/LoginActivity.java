package cl.domito.dmttransfer.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.Utilidades;
import cl.domito.dmttransfer.thread.LoginOperation;

public class LoginActivity extends AppCompatActivity {

    private EditText mUserView;
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    private CheckBox checkBoxRec;
    private TextView textViewError;
    private Conductor conductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.cambiarColorBarra(this);
        setContentView(R.layout.activity_login);
        this.getSupportActionBar().hide();
        SharedPreferences pref = getApplicationContext().getSharedPreferences
                ("preferencias", Context.MODE_PRIVATE);
        String idConductor = pref.getString("idUsuario", "");
        String nickConductor = pref.getString("nickUsuario", "");
        String clave = pref.getString("claveUsuario","");
        String recordar = pref.getString("recordar","");
        mUserView = findViewById(R.id.usuario);
        mPasswordView = findViewById(R.id.password);
        mEmailSignInButton = findViewById(R.id.login_button);
        checkBoxRec = findViewById(R.id.checkBox);
        textViewError = findViewById(R.id.textViewError);

        conductor = Conductor.getInstance();

        conductor.context = LoginActivity.this;
        if(Utilidades.validarConexion())
        {
            textViewError.setVisibility(View.GONE);
        }
        else
        {
            textViewError.setVisibility(View.VISIBLE);
        }

        if(recordar.equals("1")) {
            mUserView.setText(nickConductor);
            mPasswordView.setText(clave);
            conductor.recordarSession = true;
            checkBoxRec.setChecked(true);
        }
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginConductor();
            }
        });
        checkBoxRec.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBoxRec.isChecked())
                {
                    recordarInicioSesion();
                }
                else
                {
                    olvidarInicioSesion();
                }
            }
        });
    }

    private void loginConductor() {
        String usuario = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();
        if(!usuario.equals("") && !password.equals(""))
        {
            LoginOperation loginOperation = new LoginOperation(this);
            loginOperation.execute(usuario,password);
            ActivityUtils.hideSoftKeyBoard(this);
        }
        else
        {
            Toast t = Toast.makeText(this, "Ingrese tanto usuario como password", Toast.LENGTH_SHORT);
            t.show();
        }
    }

    private void recordarInicioSesion() {
        conductor.recordarSession = true;
    }

    private void olvidarInicioSesion() {
        conductor.recordarSession = false;
    }

}

