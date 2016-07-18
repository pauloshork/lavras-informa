package br.ufla.lavrasinforma.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by paulo on 11/07/16.
 */
public class Usuario implements Parcelable{

    private String accessToken;
    private int expiresIn;
    private String tokenType;
    private String scope;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public static final Parcelable.Creator<Usuario> CREATOR
            = new Parcelable.Creator<Usuario>() {
        public Usuario createFromParcel(Parcel parcel) {
            Usuario u = new Usuario();
            u.setAccessToken(parcel.readString());
            u.setExpiresIn(parcel.readInt());
            u.setTokenType(parcel.readString());
            u.setScope(parcel.readString());
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
        parcel.writeString(getAccessToken());
        parcel.writeInt(getExpiresIn());
        parcel.writeString(getTokenType());
        parcel.writeString(getScope());
    }
}
