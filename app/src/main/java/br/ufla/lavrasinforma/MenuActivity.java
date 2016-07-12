package br.ufla.lavrasinforma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;

public class MenuActivity extends AppCompatActivity {

    public static final String EXTRA_USUARIO = "usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void relatarProblema(View view) {
        Toast.makeText(this, "Não implementado", Toast.LENGTH_LONG).show();
    }

    public void meusRelatos(View view) {
        Toast.makeText(this, "Não implementado", Toast.LENGTH_LONG).show();
    }

    public void todosRelator(View view) {
        Toast.makeText(this, "Não implementado", Toast.LENGTH_LONG).show();
    }

    public void mapaRelatos(View view) {
        Toast.makeText(this, "Não implementado", Toast.LENGTH_LONG).show();
    }

    public void logout(View view) {
        Intent logout = new Intent(this, MainActivity.class);
        logout.setAction(MainActivity.ACTION_LOGOUT);
        startActivity(logout);
        finish();
    }
}
