package br.ufla.lavrasinforma.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import br.ufla.lavrasinforma.UtilNulls;

/**
 * Modelo da uma busca de comentario no sistema.
 * Created by paulo on 25/07/16.
 */
public class BuscaComentario implements Parcelable {
    private Integer id_usuario;
    private Integer id_relato;

    public BuscaComentario() {

    }

    protected BuscaComentario(Parcel in) {
        byte[] nulls = in.createByteArray();
        if (UtilNulls.decodeNulls(nulls, 0)) {
            id_usuario = in.readInt();
        }
        if (UtilNulls.decodeNulls(nulls, 1)) {
            id_relato = in.readInt();
        }
    }

    public static final Creator<BuscaComentario> CREATOR = new Creator<BuscaComentario>() {
        @Override
        public BuscaComentario createFromParcel(Parcel in) {
            return new BuscaComentario(in);
        }

        @Override
        public BuscaComentario[] newArray(int size) {
            return new BuscaComentario[size];
        }
    };

    public int getIdUsuario() {
        return id_usuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.id_usuario = idUsuario;
    }

    public int getIdRelato() {
        return id_relato;
    }

    public void setIdRelato(int idRelato) {
        this.id_relato = idRelato;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        byte[] nulls = UtilNulls.encodeNulls(id_usuario, id_relato);
        parcel.writeByteArray(nulls);

        if (id_usuario != null) {
            parcel.writeInt(id_usuario);
        }
        if (id_relato != null) {
            parcel.writeInt(id_relato);
        }
    }

    public Map<String, String> toParams(Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }

        if (id_usuario != null) {
            params.put("id_usuario", id_usuario.toString());
        }

        if (id_relato != null) {
            params.put("id_relato", id_relato.toString());
        }

        return params;
    }
}
