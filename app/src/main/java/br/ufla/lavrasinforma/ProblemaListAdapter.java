package br.ufla.lavrasinforma;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import br.ufla.lavrasinforma.model.Relato;

/**
 * Classe que armazena os relatos e os exibe em uma ListView.
 * Created by paulo on 18/07/16.
 */
public class ProblemaListAdapter extends ArrayAdapter<Relato> {

    public ProblemaListAdapter(Context context) {
        super(context, R.layout.item_problema);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflated = super.getView(i, view, viewGroup);

        Relato relato = getItem(i);

        TextView lblTitulo = (TextView) inflated.findViewById(R.id.item_problema_titulo);
        TextView lblStatus = (TextView) inflated.findViewById(R.id.item_problema_status);

        lblTitulo.setText(relato.getTitulo());
        lblStatus.setText(relato.getStatus().toString());

        return inflated;
    }
}
