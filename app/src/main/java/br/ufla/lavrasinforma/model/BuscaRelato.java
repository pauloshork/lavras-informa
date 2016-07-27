package br.ufla.lavrasinforma.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.ufla.lavrasinforma.UtilNulls;

/**
 * Modelo de uma busca de relatos no sistema.
 * Created by paulo on 18/07/16.
 */
public class BuscaRelato implements Parcelable {

    private String titulo;
    private String autor;
    private Date data;
    private Status status;
    private Classificacao classificacao;
    private Boolean foto;
    private Boolean meus;

    public BuscaRelato() {

    }

    protected BuscaRelato(Parcel in) {
        byte[] nulls = in.createByteArray();

        if (UtilNulls.decodeNulls(nulls, 0)) {
            setTitulo(in.readString());
        }
        if (UtilNulls.decodeNulls(nulls, 1)) {
            setAutor(in.readString());
        }
        if (UtilNulls.decodeNulls(nulls, 2)) {
            setData(new Date(in.readLong()));
        }
        if (UtilNulls.decodeNulls(nulls, 3)) {
            setStatus(Status.fromValor(in.readByte()));
        }
        if (UtilNulls.decodeNulls(nulls, 4)) {
            setClassificacao(Classificacao.fromValor(in.readByte()));
        }
        if (UtilNulls.decodeNulls(nulls, 5)) {
            boolean foto[] = in.createBooleanArray();
            setFoto(foto[0]);
        }
        if (UtilNulls.decodeNulls(nulls, 6)) {
            boolean meus[] = in.createBooleanArray();
            setMeus(meus[0]);
        }
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

    public Boolean getFoto() {
        return foto;
    }

    public void setFoto(Boolean foto) {
        this.foto = foto;
    }

    public Boolean getMeus() {
        return meus;
    }

    public void setMeus(Boolean meus) {
        this.meus = meus;
    }

    public static final Creator<BuscaRelato> CREATOR = new Creator<BuscaRelato>() {
        @Override
        public BuscaRelato createFromParcel(Parcel in) {
            return new BuscaRelato(in);
        }

        @Override
        public BuscaRelato[] newArray(int size) {
            return new BuscaRelato[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        byte[] nulls = UtilNulls.encodeNulls(titulo, autor, data, status, classificacao, foto, meus);
        out.writeByteArray(nulls);

        if (titulo != null) {
            out.writeString(getTitulo());
        }
        if (autor != null) {
            out.writeString(getAutor());
        }
        if (data != null) {
            out.writeLong(getData().getTime());
        }
        if (status != null) {
            out.writeByte(getStatus().getValor());
        }
        if (classificacao != null) {
            out.writeByte(getClassificacao().getValor());
        }
        if (foto != null) {
            boolean foto[] = {getFoto()};
            out.writeBooleanArray(foto);
        }
        if (meus != null) {
            boolean[] meus = {getMeus()};
            out.writeBooleanArray(meus);
        }
    }

    public Map<String, String> toParams(Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        if (titulo != null) {
            params.put("titulo", getTitulo());
        }
        if (autor != null) {
            params.put("autor", getAutor());
        }
        if (data != null) {
            params.put("data", getData().toString());
        }
        if (status != null) {
            params.put("status", getStatus().toString());
        }
        if (classificacao != null) {
            params.put("classificacao", getClassificacao().toString());
        }
        if (foto != null) {
            params.put("foto", getFoto().toString());
        }
        if (meus != null) {
            params.put("meus", getMeus().toString());
        }
        return params;
    }
}
