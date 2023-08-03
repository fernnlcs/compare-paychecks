package com.example;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public abstract class FileDataHandler {
    private static final Map<Integer, String> FORMATTED_MONTHS = new HashMap<>(12);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("MM. MMMM", Env.DEFAULT_LOCALE);
    private static final SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM", Env.DEFAULT_LOCALE);

    // Método para extrair a data do nome do arquivo original
    public static String extractFileDate(final String fileName) {
        // Remover a extensão do arquivo
        final String cleanName = fileName.replaceFirst("[.][^.]+$", "");

        // Extrair os últimos 6 dígitos do nome do arquivo (mes e ano no formato MMYYYY)
        final String monthYearDigits = cleanName.substring(cleanName.length() - 6);

        // Extrair o mês e o ano separadamente
        final String year = monthYearDigits.substring(2);
        final String month = monthYearDigits.substring(0, 2);

        // Formatar a data como YYYY.MM
        return year + "." + month;
    }

    // Método para formatar o nome do mês
    public static String formatMonth(final int month) {
        // Retorna o nome do mês se já tiver sido instanciado
        if (FORMATTED_MONTHS.containsKey(month)) {
            return FORMATTED_MONTHS.get(month);
        }

        // Para casos fora do intervalo janeiro-dezembro (ex.: 13. 13º salário)
        if (month <= 0 || month > 12) {
            return month + ". " + month + "º salário";
        }

        // Configurar um calendário para o primeiro dia do mês
        final Calendar calendar = Calendar.getInstance(Env.DEFAULT_LOCALE);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month - 1);

        // Formatar
        final String formattedMonth = formatter.format(calendar.getTime());

        // Converter a primeira letra para maiúscula
        final char[] chars = formattedMonth.toCharArray();
        chars[4] = Character.toUpperCase(chars[4]);

        return String.valueOf(chars);
    }

    public static int getMonthNumber(final String monthName) {
        final Calendar calendar = Calendar.getInstance(Env.DEFAULT_LOCALE);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        for (int i = 0; i < 11; i++) {
            calendar.set(Calendar.MONTH, i);
            if (monthName.toLowerCase().equals(monthFormatter.format(calendar.getTime()))) {
                return i + 1;
            }
        }

        return 0;
    }

    public static int extractPaycheckReference(final StringBuilder pageText, final String description) {
        try {
            final int end = pageText.indexOf(description);
            final String part = pageText.substring(0, end);
            final int start = part.lastIndexOf(",");

            return Integer.parseInt(pageText.substring(start + 3, end));
        } catch (Exception e) {
            return 0;
        }
    }

    public static double extractPaycheckValue(final StringBuilder pageText, final String description) {
        try {
            int end = pageText.indexOf(description);
            String part = pageText.substring(0, end);
            final int start = part.lastIndexOf("\n");
            end = part.lastIndexOf(",");
            part = part.substring(start + 1, end + 3);

            return Double.parseDouble(part.replace(",", "."));
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static boolean checkIfPaycheckLabelExists(final StringBuilder pageText, final String description) {
        return pageText.toString().contains(description);
    }
}
