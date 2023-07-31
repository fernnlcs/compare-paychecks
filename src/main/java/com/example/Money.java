package com.example;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Money implements Comparable<Money> {
    private static final Map<Currency, Map<Boolean, DecimalFormat>> formatters = new HashMap<>();
    private static final Set<Money> all = new HashSet<>();
    private static final Locale defaultLocale = new Locale("pt", "BR");
    private static final Currency defaultCurrency = Currency.getInstance(defaultLocale);
    private static final String defaultFormatterPattern = "¤ #,###,##0.00";

    public final double value;
    public final Currency currency;
    public DecimalFormat formatter;

    public static Money findOrCreate(final double value, final Currency currency) {
        final Money moneyToFind = new Money(value, currency);

        for (final Money money : all) {
            if (money.equals(moneyToFind)) {
                return money;
            }
        }

        all.add(moneyToFind);
        return moneyToFind;
    }

    public static Money findOrCreate(final double value) {
        return Money.findOrCreate(value, defaultCurrency);
    }

    private DecimalFormat findOrCreateFormatter() {
        return findOrCreateFormatter(false);
    }

    private DecimalFormat findOrCreateFormatter(final boolean isDifference) {

        final Consumer<DecimalFormat> positivePrefixAdapter = new Consumer<DecimalFormat>() {
            @Override
            public void accept(final DecimalFormat formatter) {
                final String currentPattern = formatter.getPositivePrefix();
                if (isDifference) {
                    if (!currentPattern.contains("+ ")) {
                        formatter.setPositivePrefix("+ " + currentPattern);
                    }
                } else {
                    if (currentPattern.contains("+ ")) {
                        formatter.setPositivePrefix(currentPattern.substring(2));
                    }
                }
            }
        };

        final Supplier<DecimalFormat> formatterCreator = new Supplier<DecimalFormat>() {
            @Override
            public DecimalFormat get() {
                // Criar novo formatador
                final DecimalFormat formatter = new DecimalFormat(defaultFormatterPattern);

                // Definir moeda
                formatter.setCurrency(currency);

                // Definir novo prefixo
                // Substituir o "-" por "- "
                formatter.setNegativePrefix(formatter.getNegativePrefix().replace("-", "- "));
                return formatter;
            }
        };

        if (formatters.containsKey(currency)) {
            // Se já tiver algum formatador pra essa moeda
            final Map<Boolean, DecimalFormat> subMap = formatters.get(currency);

            if (!subMap.containsKey(isDifference)) {
                // Se não tiver formatador pra essa modalidade
                // Clonar o formatador da outra modalidade

                subMap.put(isDifference, formatterCreator.get());
            }

            final DecimalFormat formatter = subMap.get(isDifference);

            // Ajustar o formatador à modalidade
            positivePrefixAdapter.accept(formatter);

            // Retornar o formatador
            return formatter;
        } else {
            // Se não tiver nenhum formatador pra essa moeda
            // Criar um formatador com as regras genéricas
            final DecimalFormat formatter = formatterCreator.get();

            // Adicionar o formatador a um novo submapa
            final Map<Boolean, DecimalFormat> subMap = new HashMap<Boolean, DecimalFormat>();
            subMap.put(isDifference, formatter);

            // Adicionar o submapa ao mapa principal
            formatters.put(currency, subMap);

            // Ajustar o formatador à modalidade
            positivePrefixAdapter.accept(formatter);

            // Retornar o novo formatador
            return formatter;
        }
    }

    private Money(final double value, final Currency currency) {
        this.value = value;
        this.currency = currency;
        this.formatter = findOrCreateFormatter();
    }

    public String getValue() {
        final String format = formatter.format(value);
        return format;
    }

    public boolean isPositive() {
        return value > 0;
    }

    public boolean isNegative() {
        return value < 0;
    }

    public boolean isZero() {
        return value == 0;
    }

    public Money asNormal() {
        formatter = findOrCreateFormatter(false);
        return this;
    }

    public Money asDifference() {
        formatter = findOrCreateFormatter(true);
        return this;
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public int compareTo(final Money o) {
        return Double.compare(value, o.value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Money other = (Money) obj;
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
            return false;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.equals(other.currency))
            return false;
        return true;
    }
}
