package br.ufla.lavrasinforma.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modelo com os dados do usuário.
 * Created by paulo on 18/07/16.
 */
public class Usuario implements Parcelable {

    private int id;
    private String email;
    private String nome;
    private boolean admin;

    public Usuario() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    protected Usuario(Parcel in) {
        setId(in.readInt());
        setEmail(in.readString());
        setNome(in.readString());
        boolean[] a = new boolean[1];
        in.readBooleanArray(a);
        setAdmin(a[0]);
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(getId());
        parcel.writeString(getEmail());
        parcel.writeString(getNome());
        boolean[] a = {isAdmin()};
        parcel.writeBooleanArray(a);
    }
}
