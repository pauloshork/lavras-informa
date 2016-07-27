package br.ufla.lavrasinforma.model;

/**
 * Valores de classificação do sistema.
 * Created by paulo on 18/07/16.
 */
public enum Classificacao {
    Desconhecido(0),
    Infraestrutura(1),
    Saude(2),
    Seguranca(3);

    private byte valor;

    Classificacao(byte valor) {
        this.valor = valor;
    }

    Classificacao(int valor) {
        this((byte) valor);
    }

    public byte getValor() {
        return valor;
    }

    @Override
    public String toString() {
        switch (getValor()) {
            case 1:
                return "infraestrutura";
            case 2:
                return "saude";
            case 3:
                return "seguranca";
            default:
                return "null";
        }
    }

    public static Classificacao fromValor(byte valor) {
        switch (valor) {
            case 1:
                return Infraestrutura;
            case 2:
                return Saude;
            case 3:
                return Seguranca;
            default:
                return Desconhecido;
        }
    }
}
