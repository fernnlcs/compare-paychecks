package com.example;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Money implements Comparable<Money> {
    // Conjunto de formatadores 
    private static final Map<Currency, Map<Boolean, DecimalFormat>> formatters = new HashMap<>();

    // Todas as instâncias de dinheiro criadas
    private static final List<Money> instances = new ArrayList<>();

    // Padrões para a moeda
    private static final Locale defaultLocale = new Locale("pt", "BR");
    private static final Currency defaultCurrency = Currency.getInstance(defaultLocale);
    private static final String defaultFormatterPattern = "¤ #,###,##0.00";

    // Valor
    public final double value;

    // Moeda
    public final Currency currency;

    // Formatador do dinheiro para texto
    public DecimalFormat formatter;

    // Obter a instância do dinheiro
    public static Money findOrCreate(final double value, final Currency currency) {
        // Criar uma nova instância para buscar através de comparação
        final Money moneyToFind = new Money(value, currency);

        // Procurar se a instância já existe
        for (final Money money : instances) {
            if (money.equals(moneyToFind)) {
                // Retornar a instância existente
                return money;
            }
        }

        // Adicionar nova instância às existentes
        instances.add(moneyToFind);

        // Retornar nova instância
        return moneyToFind;
    }

    // Obter a instância do dinheiro sem informar a moeda
    public static Money findOrCreate(final double value) {
        // Invocar mesmo método, passando a moeda padrão como argumento
        return Money.findOrCreate(value, defaultCurrency);
    }

    // Obter uma instância de formatador
    private DecimalFormat findOrCreateFormatter() {
        return findOrCreateFormatter(false);
    }

    // Obter uma instância de formatador
    private DecimalFormat findOrCreateFormatter(final boolean isDifference) {

        // Adaptador para colocar ou remover o "+ "
        final Consumer<DecimalFormat> positivePrefixAdapter = new Consumer<DecimalFormat>() {
            @Override
            public void accept(final DecimalFormat formatter) {
                // Guardar o prefixo já existente
                final String currentPattern = formatter.getPositivePrefix();

                if (isDifference) {
                    // Se o dinheiro se tratar de uma diferença de valores
                    if (!currentPattern.contains("+ ")) {
                        // Se o prefixo existente ainda não tiver o "+ "
                        // Adicionar o "+ "
                        formatter.setPositivePrefix("+ " + currentPattern);
                    }
                } else {
                    // Se o dinheiro NÃO se tratar de uma diferença de valores
                    if (currentPattern.contains("+ ")) {
                        // Se o prefixo existente tiver o "+ "
                        // Remover o "+ "
                        formatter.setPositivePrefix(currentPattern.substring(2));
                    }
                }
            }
        };

        // Criador de formatadores
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
                // Se ainda não tiver formatador pra essa modalidade
                // Criar um formatador para essa moeda e SEM ser diferença
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
