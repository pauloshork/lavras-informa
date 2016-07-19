package br.ufla.lavrasinforma.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by paulo on 18/07/16.
 */
public class Busca implements Parcelable {

    private String titulo;
    private String autor;
    private Date data;
    private Status status;
    private Classificacao classificacao;
    private boolean comFoto;
    private boolean semFoto;

    public Busca() {

    }

    protected Busca(Parcel in) {
        setTitulo(in.readString());
        setAutor(in.readString());
        setData(new Date(in.readLong()));
        setStatus(Status.fromValor(in.readByte()));
        setClassificacao(Classificacao.fromValor(in.readByte()));
        boolean foto[] = new boolean[2];
        in.readBooleanArray(foto);
        setComFoto(foto[0]);
        setSemFoto(foto[1]);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
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

    public boolean isComFoto() {
        return comFoto;
    }

    public void setComFoto(boolean comFoto) {
        this.comFoto = comFoto;
    }

    public boolean isSemFoto() {
        return semFoto;
    }

    public void setSemFoto(boolean semFoto) {
        this.semFoto = semFoto;
    }

    public static final Creator<Busca> CREATOR = new Creator<Busca>() {
        @Override
        public Busca createFromParcel(Parcel in) {
            return new Busca(in);
        }

        @Override
        public Busca[] newArray(int size) {
            return new Busca[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(getTitulo());
        out.writeString(getAutor());
        out.writeLong(getData().getTime());
        out.writeByte(getStatus().getValor());
        out.writeByte(getClassificacao().getValor());
        boolean foto[] = {isComFoto(), isSemFoto()};
        out.writeBooleanArray(foto);
    }
}
