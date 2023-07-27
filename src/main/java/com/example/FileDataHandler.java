package com.example;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javafx.util.Callback;

public abstract class FileDataHandler {
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
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);

        final SimpleDateFormat format = new SimpleDateFormat("MM. MMMM", new Locale("pt", "BR"));
        final String formattedMonth = format.format(calendar.getTime());

        // Converter a primeira letra para maiúscula
        final char[] chars = formattedMonth.toCharArray();
        chars[4] = Character.toUpperCase(chars[4]);

        return String.valueOf(chars);
    }

    // Método para extrair os dias trabalhados
    public static void extractPaycheckDetails(final Paycheck paycheck, final String pageText) {
        final int startIndex = pageText.lastIndexOf("Discriminação das Verbas");
        final String textAdapt = pageText.substring(startIndex);

        final Callback<String, Integer> getReference = new Callback<String, Integer>() {
            @Override
            public Integer call(final String description) {
                try {
                    final int end = textAdapt.indexOf(description);
                    final String part = textAdapt.substring(0, end);
                    final int start = part.lastIndexOf(",");

                    return Integer.parseInt(textAdapt.substring(start + 3, end));
                } catch (Exception e) {
                    return 0;
                }
            }
        };

        final Callback<String, Double> getValue = new Callback<String, Double>() {
            @Override
            public Double call(final String description) {
                try {
                    int end = textAdapt.indexOf(description);
                    String part = textAdapt.substring(0, end);
                    final int start = part.lastIndexOf("\n");
                    end = part.lastIndexOf(",");
                    part = part.substring(start + 1, end + 3);

                    return Double.parseDouble(part.replace(",", "."));
                } catch (Exception e) {
                    return 0.0;
                }
            }
        };

        paycheck.setWorkedDays(getReference.call(" dia(s)Salário-Base011"));
        paycheck.setNightShiftHours(getReference.call("hAdicional Noturno"));
        paycheck.setOvertimeValue(Money.findOrCreate(getValue.call("Cl 4")));
        paycheck.setClosedSectorValue(Money.findOrCreate(getValue.call("Gratifica")));
        paycheck.setHealthCarePlanActive(textAdapt.contains("Plano de Saúde do Titular"));
        paycheck.setHealthCarePlanForDependentActive(textAdapt.contains("Plano de Saúde do Dependente"));

        paycheck.getWorkedDays();
    }
}
