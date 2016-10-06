package com.cadastroaluno.projeto.cadastroaluno;

import android.widget.EditText;

/**
 * Created by leobo on 9/18/2016.
 */
public class Util {

    private Util() {
        //utility class
    }

    /**
     * Devolve um objeto String não nulo para o objeto String fornecido
     * @param value String
     * @return String
     */
    public static String getText(String value) {
        return value == null ? "" : value;
    }

    /**
     * Devolve um objeto String não nulo para o objeto Integer fornecido
     * @param value Integer
     * @return String
     */
    public static String getText(Integer value) {
        return value == null ? "" : value.toString();
    }

    /**
     * Devolve um objeto Integer que represente o objeto String fornecido como parâmetro, mesmo que este seja nulo
     * @param string
     * @return
     */
    public static int getInteger(String string) {
        return string.equals("") ? 0 : Integer.valueOf(string);
    }
}
