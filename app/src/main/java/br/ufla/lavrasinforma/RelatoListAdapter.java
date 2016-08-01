package br.ufla.lavrasinforma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import br.ufla.lavrasinforma.model.Relato;
import br.ufla.lavrasinforma.model.web.WebServiceConnector;

/**
 * Classe que armazena os relatos e os exibe em uma ListView.
 * Created by paulo on 18/07/16.
 */
public class RelatoListAdapter extends ArrayAdapter<Relato> {

    public RelatoListAdapter(Context context) {
        super(context, R.layout.item_relato);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflated = view;

        if (inflated == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflated = inflater.inflate(R.layout.item_relato, null, false);
        }

        Relato relato = getItem(i);

        NetworkImageView imagem = (NetworkImageView) inflated.findViewById(R.id.item_relato_imagem);
        TextView lblTitulo = (TextView) inflated.findViewById(R.id.item_relato_titulo);
        TextView lblStatus = (TextView) inflated.findViewById(R.id.item_relato_status);

        imagem.setImageUrl(WebServiceConnector.getInstance().buscarImagem(getContext(), UtilSession.getAccessToken(getContext()), relato), WebServiceConnector.getImageLoader(getContext()));
        lblTitulo.setText(relato.getTitulo());
        lblStatus.setText(relato.getStatus());

        return inflated;
    }
}
