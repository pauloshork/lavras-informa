package br.ufla.lavrasinforma;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utilitário de converções do sistema.
 * Created by paulo on 28/07/16.
 */
public class UtilConvert {
    public static final String DATE_PATTERN = "yyyy-MM-dd hh:mm:ss";
    private static final DateFormat instance = new SimpleDateFormat(DATE_PATTERN);

    public static Date fromString(String date) throws ParseException {
        return instance.parse(date);
    }

    public static String fromDate(Date date) {
        if (date == null) {
            return null;
        } else {
            return instance.format(date);
        }
    }

    public static String statusFromPosition(int position) {
        switch (position) {
            case 1:
                return "pendente";
            case 2:
                return "em-andamento";
            case 3:
                return "finalizado";
            default:
                return null;
        }
    }

    public static String classificacaoFromPosition(int position) {
        switch (position) {
            case 1:
                return "infraestrutura";
            case 2:
                return "saude";
            case 3:
                return "seguranca";
            default:
                return null;
        }
    }

    public static int positionFromClassificacao(String classificacao) {
        if (classificacao == null) {
            classificacao = "desconhecido";
        }
        switch (classificacao) {
            case "infraestrutura":
                return 1;
            case "saude":
                return 2;
            case "seguranca":
                return 3;
            default:
                return 0;
        }
    }

    public static int positionFromStatus(String status) {
        if (status == null) {
            status = "desconhecido";
        }
        switch (status) {
            case "pendente":
                return 1;
            case "em-andamento":
                return 2;
            case "finalizado":
                return 3;
            default:
                return 0;
        }
    }
}
