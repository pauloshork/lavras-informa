package br.ufla.lavrasinforma.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by paulo on 11/07/16.
 */
public class AccessToken implements Parcelable {

    private String accessToken;
    private int expiresIn;
    private String tokenType;
    private String scope;

    public AccessToken() {
        this((String) null);
    }

    public AccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    protected AccessToken(Parcel in) {
        setAccessToken(in.readString());
        setExpiresIn(in.readInt());
        setTokenType(in.readString());
        setScope(in.readString());
    }

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

    public static final Parcelable.Creator<AccessToken> CREATOR
            = new Parcelable.Creator<AccessToken>() {
        public AccessToken createFromParcel(Parcel parcel) {
            return new AccessToken(parcel);
        }

        public AccessToken[] newArray(int size) {
            return new AccessToken[size];
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
