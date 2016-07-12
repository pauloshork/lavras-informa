package br.ufla.lavrasinforma.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by paulo on 11/07/16.
 */
public class Usuario implements Parcelable{

    private String token;

    public Usuario() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static final Parcelable.Creator<Usuario> CREATOR
            = new Parcelable.Creator<Usuario>() {
        public Usuario createFromParcel(Parcel parcel) {
            Usuario u = new Usuario();
            u.setToken(parcel.readString());
            return u;
        }

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
        parcel.writeString(getToken());
    }
}
