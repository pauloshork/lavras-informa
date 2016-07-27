package br.ufla.lavrasinforma.model;

/**
 * Valores de status do sistema.
 * Created by paulo on 18/07/16.
 */
public enum Status {
    Desconhecido(0),
    Pendente(1),
    EmAndamento(2),
    Finalizado(3);

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

    @Override
    public String toString() {
        switch (getValor()) {
            case 1:
                return "pendente";
            case 2:
                return "em-andamento";
            case 3:
                return "finalizado";
            default:
                return "null";
        }
    }

    public static Status fromValor(byte valor) {
        switch (valor) {
            case 1:
                return Pendente;
            case 2:
                return EmAndamento;
            case 3:
                return Finalizado;
            default:
                return Desconhecido;
        }
    }
}
