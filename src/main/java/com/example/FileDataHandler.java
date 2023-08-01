package com.example;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class FileDataHandler {
    private static final Map<Integer, String> FORMATTED_MONTHS = new HashMap<>(12);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("MM. MMMM", Env.DEFAULT_LOCALE);

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
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month - 1);

        // Formatar
        final String formattedMonth = formatter.format(calendar.getTime());

        // Converter a primeira letra para maiúscula
        final char[] chars = formattedMonth.toCharArray();
        chars[4] = Character.toUpperCase(chars[4]);

        return String.valueOf(chars);
    }

    // Método para extrair os dias trabalhados
    public static void extractPaycheckDetails(final Paycheck paycheck, final String pageText) {
        final int startIndex = pageText.lastIndexOf(Env.DETAILS_TITLE);
        final String textAdapt = pageText.substring(startIndex);

        final Function<String, Integer> getReference = new Function<String, Integer>() {
            @Override
            public Integer apply(final String description) {
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

        final Function<String, Double> getValue = new Function<String, Double>() {
            @Override
            public Double apply(final String description) {
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

        paycheck.setWorkedDays(getReference.apply(Env.WORKED_DAYS_LABEL));
        paycheck.setNightShiftHours(getReference.apply(Env.NIGHT_SHIFT_HOURS_LABEL));
        paycheck.setOvertimeValue(Money.findOrCreate(getValue.apply(Env.OVERTIME_LABEL)));
        paycheck.setClosedSectorValue(Money.findOrCreate(getValue.apply(Env.CLOSED_SECTOR_LABEL)));
        paycheck.setHealthCarePlanActive(textAdapt.contains(Env.HEALTH_CARE_PLAN_LABEL));
        paycheck.setHealthCarePlanForDependentActive(textAdapt.contains(Env.HEALTH_CARE_PLAN_FOR_DEPENDENT_LABEL));

        paycheck.getWorkedDays();
    }
}
