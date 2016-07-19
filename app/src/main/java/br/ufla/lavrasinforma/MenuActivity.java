package br.ufla.lavrasinforma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void relatarProblema(View view) {
        Toast.makeText(this, "TODO Criar tela de relato de problemas", Toast.LENGTH_LONG).show();
        // TODO Criar tela de relato de problemas
    }

    public void meusRelatos(View view) {
        Intent lista = new Intent(this, ListaRelatosActivity.class);
        lista.setAction(ListaRelatosActivity.ACTION_MEUS_RELATOS);
        startActivity(lista);
    }

    public void todosRelator(View view) {
        Intent lista = new Intent(this, ListaRelatosActivity.class);
        lista.setAction(ListaRelatosActivity.ACTIOIN_TODOS_RELATOS);
        startActivity(lista);
    }

    public void mapaRelatos(View view) {
        Toast.makeText(this, "TODO Criar tela de mapa de relatos", Toast.LENGTH_LONG).show();
        // TODO Criar tela de mapa de relatos
    }

    public void logout(View view) {
        Intent logout = new Intent(this, MainActivity.class);
        logout.setAction(MainActivity.ACTION_LOGOUT);
        startActivity(logout);
        finish();
    }
}
