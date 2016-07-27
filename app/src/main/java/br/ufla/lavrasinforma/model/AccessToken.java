package br.ufla.lavrasinforma.model;

import android.os.Parcel;
import android.os.Parcelable;

import br.ufla.lavrasinforma.UtilNulls;

/**
 * Modelo de um token do sistema.
 * Created by paulo on 11/07/16.
 */
public class AccessToken implements Parcelable {

    private String access_token;
    private Integer expires_in;
    private String token_type;
    private String scope;

    public AccessToken() {
        this((String) null);
    }

    public AccessToken(String accessToken) {
        this.access_token = accessToken;
    }

    protected AccessToken(Parcel in) {
        byte[] nulls = in.createByteArray();
        setAccessToken(in.readString());
        if (UtilNulls.decodeNulls(nulls, 0)) {
            setExpiresIn(in.readInt());
        }
        if (UtilNulls.decodeNulls(nulls, 1)) {
            setTokenType(in.readString());
        }
        if (UtilNulls.decodeNulls(nulls, 2)) {
            setScope(in.readString());
        }
    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String accessToken) {
        this.access_token = accessToken;
    }

    public Integer getExpiresIn() {
        return expires_in;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expires_in = expiresIn;
    }

    public String getTokenType() {
        return token_type;
    }

    public void setTokenType(String tokenType) {
        this.token_type = tokenType;
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
        byte[] nulls = UtilNulls.encodeNulls(expires_in, token_type, scope);
        parcel.writeByteArray(nulls);
        parcel.writeString(getAccessToken());
        if (getExpiresIn() != null) {
            parcel.writeInt(getExpiresIn());
        }
        if (getTokenType() != null) {
            parcel.writeString(getTokenType());
        }
        if (getScope() != null) {
            parcel.writeString(getScope());
        }
    }
}
