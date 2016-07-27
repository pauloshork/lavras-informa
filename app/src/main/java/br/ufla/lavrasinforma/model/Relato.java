package br.ufla.lavrasinforma.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.ufla.lavrasinforma.UtilNulls;

/**
 * Modelo de um relato no sistema.
 * Created by paulo on 18/07/16.
 */
public class Relato implements Parcelable{

    private Integer id;
    private Integer id_usuario;
    private Date data;
    private String titulo;
    private String descricao;
    private Status status;
    private Classificacao classificacao;
    private Bitmap foto;
    private double latitude;
    private double longitude;
    private String nome_usuario;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdUsuario() {
        return id_usuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.id_usuario = idUsuario;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Classificacao getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(Classificacao classificacao) {
        this.classificacao = classificacao;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNomeUsuario() {
        return nome_usuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nome_usuario = nomeUsuario;
    }

    public static final Parcelable.Creator<Relato> CREATOR
            = new Parcelable.Creator<Relato>() {

        @Override
        public Relato createFromParcel(Parcel parcel) {
            Relato relato = new Relato();

            byte[] nulls = parcel.createByteArray();

            if (UtilNulls.decodeNulls(nulls, 0)) {
                relato.setId(parcel.readInt());
            }

            if (UtilNulls.decodeNulls(nulls, 1)) {
                relato.setIdUsuario(parcel.readInt());
            }

            if (UtilNulls.decodeNulls(nulls, 2)) {
                relato.setData(new Date(parcel.readLong()));
            }
            relato.setTitulo(parcel.readString());
            relato.setDescricao(parcel.readString());
            relato.setStatus(Status.fromValor(parcel.readByte()));
            relato.setClassificacao(Classificacao.fromValor(parcel.readByte()));

            if (UtilNulls.decodeNulls(nulls, 3)) {
                relato.setFoto((Bitmap) parcel.readParcelable(getClass().getClassLoader()));
            }
            relato.setLatitude(parcel.readDouble());
            relato.setLongitude(parcel.readDouble());

            if (UtilNulls.decodeNulls(nulls, 4)) {
                relato.setNomeUsuario(parcel.readString());
            }
            return relato;
        }

        @Override
        public Relato[] newArray(int i) {
            return new Relato[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        byte[] nulls = UtilNulls.encodeNulls(id, id_usuario, data, foto, nome_usuario);
        parcel.writeByteArray(nulls);

        if (getId() != null) {
            parcel.writeInt(getId());
        }

        if (getIdUsuario() != null) {
            parcel.writeInt(getIdUsuario());
        }

        if (getData() != null) {
            parcel.writeLong(getData().getTime());
        }

        parcel.writeString(getTitulo());
        parcel.writeString(getDescricao());
        parcel.writeByte(getStatus().getValor());
        parcel.writeByte(getClassificacao().getValor());
        if (getFoto() != null) {
            parcel.writeParcelable(getFoto(), i);
        }
        parcel.writeDouble(getLatitude());
        parcel.writeDouble(getLongitude());
        if (getNomeUsuario() != null) {
            parcel.writeString(getNomeUsuario());
        }
    }

    public Map<String,String> toParams(Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }

        if (getId() != null) {
            params.put("id", getId().toString());
        }

        params.put("id_usuario", String.valueOf(getIdUsuario()));

        if (getData() != null) {
            params.put("data", getData().toString());
        }

        params.put("titulo", getTitulo());
        params.put("descricao", getDescricao());
        params.put("status", getStatus().toString());
        params.put("classificacao", getClassificacao().toString());
        if (getFoto() != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            foto.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            String base64 = "data:image/jpeg;base64," + Base64.encodeToString(baos.toByteArray(), Base64.URL_SAFE);
            params.put("foto", base64);
        }
        params.put("latitude", String.valueOf(getLatitude()));
        params.put("longitude", String.valueOf(getLongitude()));

        return params;
    }
}
