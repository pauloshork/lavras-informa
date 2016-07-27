package br.ufla.lavrasinforma.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.ufla.lavrasinforma.UtilNulls;
import br.ufla.lavrasinforma.UtilSession;

/**
 * Modelo do comentário do sistema.
 * Created by paulo on 25/07/16.
 */
public class Comentario implements Parcelable {
    private Integer id;
    private Integer id_usuario;
    private int id_relato;
    private Date data;
    private String texto;
    private String nome_usuario;

    public Comentario() {

    }

    protected Comentario(Parcel in) {
        byte[] nulls = in.createByteArray();
        if (UtilNulls.decodeNulls(nulls, 0)) {
            id = in.readInt();
        }
        if (UtilNulls.decodeNulls(nulls, 1)) {
            id_usuario = in.readInt();
        }
        id_relato = in.readInt();
        if (UtilNulls.decodeNulls(nulls, 2)) {
            data = new Date(in.readLong());
        }
        texto = in.readString();
        if (UtilNulls.decodeNulls(nulls, 3)) {
            nome_usuario = in.readString();
        }
    }

    public static final Creator<Comentario> CREATOR = new Creator<Comentario>() {
        @Override
        public Comentario createFromParcel(Parcel in) {
            return new Comentario(in);
        }

        @Override
        public Comentario[] newArray(int size) {
            return new Comentario[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getIdUsuario() {
        return id_usuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.id_usuario = idUsuario;
    }

    public int getIdRelato() {
        return id_relato;
    }

    public void setIdRelato(int idRelato) {
        this.id_relato = idRelato;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getNomeUsuario() {
        return nome_usuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nome_usuario = nomeUsuario;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        byte[] nulls = UtilNulls.encodeNulls(id, id_usuario, data, nome_usuario);
        parcel.writeByteArray(nulls);

        if (id != null) {
            parcel.writeInt(id);
        }
        if (id_usuario != null) {
            parcel.writeInt(id_usuario);
        }
        parcel.writeInt(id_relato);
        if (data != null) {
            parcel.writeLong(data.getTime());
        }
        parcel.writeString(texto);
        if (nome_usuario != null) {
            parcel.writeString(nome_usuario);
        }
    }

    public Map<String,String> toParams(Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }

        if (id != null) {
            params.put("id", String.valueOf(id));
        }
        params.put("id_usuario", String.valueOf(id_usuario));
        params.put("id_relato", String.valueOf(id_relato));
        if (data != null) {
            params.put("data", data.toString());
        }
        params.put("texto", texto);

        return params;
    }
}
