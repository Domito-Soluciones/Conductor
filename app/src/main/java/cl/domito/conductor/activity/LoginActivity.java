package cl.domito.conductor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;
import cl.domito.conductor.thread.AsignacionThread;

public class LoginActivity extends AppCompatActivity {

    private EditText mUserView;
    private EditText mPasswordView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getSupportActionBar().hide();
        mUserView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        progressBar = findViewById(R.id.login_progress);
    }

    private void login() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        final Toast t = Toast.makeText(this, "login erroneo", Toast.LENGTH_LONG);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean login = RequestConductor.loginConductor(Utilidades.URL_BASE_CONDUCTOR + "LoginConductor.php?usuario=" + mUserView.getText().toString() + "&password=" + mPasswordView.getText().toString());
                if (login) {
                    Utilidades.CONDUCTOR_ACTIVO = true;
                    Utilidades.USER = mUserView.getText().toString();
                    String url = Utilidades.URL_BASE_CONDUCTOR + "HabilitarConductor.php";
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("usuario", Utilidades.USER));
                    Utilidades.enviarPost(url,params);
                    Intent mainIntent = new Intent(LoginActivity.this, MapsActivity.class);
                    AsignacionThread asignacionThread = new AsignacionThread();
                    asignacionThread.execute((Object) null);
                    LoginActivity.this.startActivity(mainIntent);
                    LoginActivity.this.finish();
                } else {
                    t.show();
                }
            }
        });
        thread.start();


    }

}

