package br.ufla.lavrasinforma.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.ufla.lavrasinforma.UtilConvert;
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
    private String status;
    private String classificacao;
    private Bitmap dados_foto;
    private boolean foto;
    private Double latitude;
    private Double longitude;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public boolean getFoto() {
        return foto;
    }

    public void setFoto(boolean foto) {
        this.foto = foto;
    }

    public Bitmap getDadosFoto() {
        return dados_foto;
    }

    public void setDadosFoto(Bitmap dadosFoto) {
        this.dados_foto = dadosFoto;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
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

            UtilNulls nulls = parcel.readParcelable(getClass().getClassLoader());

            if (nulls.isNextNotNull()) {
                relato.setId(parcel.readInt());
            }

            if (nulls.isNextNotNull()) {
                relato.setIdUsuario(parcel.readInt());
            }

            if (nulls.isNextNotNull()) {
                relato.setData(new Date(parcel.readLong()));
            }

            if (nulls.isNextNotNull()) {
                relato.setTitulo(parcel.readString());
            }
            relato.setDescricao(parcel.readString());
            relato.setStatus(parcel.readString());
            relato.setClassificacao(parcel.readString());

            if (nulls.isNextNotNull()) {
                relato.setDadosFoto((Bitmap) parcel.readParcelable(getClass().getClassLoader()));
            }

            relato.setFoto(parcel.createBooleanArray()[0]);

            if (nulls.isNextNotNull()) {
                relato.setLatitude(parcel.readDouble());
            }
            if (nulls.isNextNotNull()) {
                relato.setLongitude(parcel.readDouble());
            }

            if (nulls.isNextNotNull()) {
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
        UtilNulls nulls = new UtilNulls(id, id_usuario, data, titulo, dados_foto, latitude, longitude, nome_usuario);
        parcel.writeParcelable(nulls, i);

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
        parcel.writeString(getStatus());
        parcel.writeString(getClassificacao());
        if (getDadosFoto() != null) {
            parcel.writeParcelable(getDadosFoto(), i);
        }
        parcel.writeBooleanArray(new boolean[] {getFoto()});
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

        if (getIdUsuario() != null) {
            params.put("id_usuario", String.valueOf(getIdUsuario()));
        }

        if (getData() != null) {
            params.put("data", UtilConvert.fromDate(getData()));
        }

        if (getTitulo() != null) {
            params.put("titulo", getTitulo());
        }
        params.put("descricao", getDescricao());
        params.put("status", getStatus());
        params.put("classificacao", getClassificacao());
        if (getDadosFoto() != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            getDadosFoto().compress(Bitmap.CompressFormat.JPEG, 100, baos);
            String base64 = "data:image/jpeg;base64," + Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            params.put("dados_foto", base64);
        }

        params.put("foto", String.valueOf(getFoto() ? 1 : 0));
        params.put("latitude", getLatitude().toString());
        params.put("longitude", getLongitude().toString());

        return params;
    }
}
