package br.ufla.lavrasinforma.model;

/**
 * Created by paulo on 18/07/16.
 */
public enum Status {
    Desconhecido(0),
    Pendente(1),
    EmAndamento(2),
    Solucionado(3);

    private byte valor;

    Status(byte valor) {
        this.valor = valor;
    }

    Status(int valor) {
        this((byte) valor);
    }

    public byte getValor() {
        return valor;
    }

    public static Status fromValor(byte valor) {
        switch (valor) {
            case 1:
                return Pendente;
            case 2:
                return EmAndamento;
            case 3:
                return Solucionado;
            default:
                return Desconhecido;
        }
    }
}
