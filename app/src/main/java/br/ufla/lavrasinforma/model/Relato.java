package br.ufla.lavrasinforma.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by paulo on 18/07/16.
 */
public class Relato implements Parcelable{
    private Integer id;
    private int idUsuario;
    private Date data;
    private String titulo;
    private String descricao;
    private Status status;
    private Classificacao classificacao;
    private String foto;
    private double latitude;
    private double longitude;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
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

    public static final Parcelable.Creator<Relato> CREATOR
            = new Parcelable.Creator<Relato>() {

        @Override
        public Relato createFromParcel(Parcel parcel) {
            Relato relato = new Relato();
            byte hasId = parcel.readByte();
            if (hasId == 1) {
                relato.setId(parcel.readInt());
            }

            relato.setIdUsuario(parcel.readInt());
            relato.setData(new Date(parcel.readLong()));
            relato.setTitulo(parcel.readString());
            relato.setDescricao(parcel.readString());
            relato.setStatus(Status.fromValor(parcel.readByte()));
            relato.setClassificacao(Classificacao.fromValor(parcel.readByte()));
            relato.setFoto(parcel.readString());
            relato.setLatitude(parcel.readDouble());
            relato.setLongitude(parcel.readDouble());
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
        byte hasId = (byte) (getId() != null ? 1 : 0);
        parcel.writeByte(hasId);
        if (getId() != null) {
            parcel.writeInt(getId());
        }

        parcel.writeInt(getIdUsuario());
        parcel.writeLong(getData().getTime());
        parcel.writeString(getTitulo());
        parcel.writeString(getDescricao());
        parcel.writeByte(getStatus().getValor());
        parcel.writeByte(getClassificacao().getValor());
        parcel.writeString(getFoto());
        parcel.writeDouble(getLatitude());
        parcel.writeDouble(getLongitude());
    }
}
