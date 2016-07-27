package br.ufla.lavrasinforma;

/**
 * Created by paulo on 27/07/16.
 */
public class UtilNulls {

    public static byte[] encodeNulls(Object... objects) {
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
        return bytes;
    }

    private static byte encodeByte(Object object, byte b, int offset) {
        if (object == null) {
            b |= 1 << offset;
        }
        return b;
    }

    public static boolean decodeNulls(byte[] nulls, int objectIndex) {
        byte b = nulls[objectIndex / Byte.SIZE];
        objectIndex %= Byte.SIZE;
        return (b & (1 << objectIndex)) == 0;
    }
}
