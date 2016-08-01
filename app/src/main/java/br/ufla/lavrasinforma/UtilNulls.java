package br.ufla.lavrasinforma;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Classe para codificar cadeias de bits indicando campos nulos.
 * Created by paulo on 27/07/16.
 */
public class UtilNulls implements Parcelable {

    private int objectIndex;
    private byte[] encoded;

    public UtilNulls(Object... objects) {
        int resto = objects.length % Byte.SIZE;
        int tam = objects.length / Byte.SIZE;
        if (resto > 0) {
            tam++;
        }
        byte[] bytes = new byte[tam];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = 0;
            for (int j = 0; j < Byte.SIZE && i * Byte.SIZE + j < objects.length; j++) {
                bytes[i] = encodeByte(objects[i * Byte.SIZE + j], bytes[i], j);
            }
        }

        objectIndex = 0;
        encoded = bytes;
    }

    public UtilNulls(byte[] nulls) {
        encoded = nulls.clone();
        objectIndex = 0;
    }

    protected UtilNulls(Parcel in) {
        objectIndex = 0;
        encoded = in.createByteArray();
    }

    public static final Creator<UtilNulls> CREATOR = new Creator<UtilNulls>() {
        @Override
        public UtilNulls createFromParcel(Parcel in) {
            return new UtilNulls(in);
        }

        @Override
        public UtilNulls[] newArray(int size) {
            return new UtilNulls[size];
        }
    };

    public byte[] getEncoded() {
        return encoded.clone();
    }

    public boolean isNextNotNull() {
        return decodeNulls(encoded, objectIndex++);
    }

    public void resetIndex() {
        objectIndex = 0;
    }

    private static byte encodeByte(Object object, byte b, int offset) {
        if (object == null) {
            b |= 1 << offset;
        }
        return b;
    }

    private static boolean decodeNulls(byte[] nulls, int objectIndex) {
        byte b = nulls[objectIndex / Byte.SIZE];
        objectIndex %= Byte.SIZE;
        return (b & (1 << objectIndex)) == 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByteArray(encoded);
    }
}
