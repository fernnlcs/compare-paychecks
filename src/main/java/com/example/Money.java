package com.example;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Money implements Comparable<Money> {
    private static final Map<Currency, DecimalFormat> formatters = new HashMap<>();
    private static final Set<Money> all = new HashSet<>();
    private static final Currency defaultCurrency = Currency.findOrCreate("R$");
    private static final Function<Money, String> defaultTypist = new Function<Money,String>() {
        @Override
        public String apply(Money money) {
            return money.getValue();
        }
    };

    private Function<Money, String> typist = defaultTypist;
    public final double value;
    public final Currency currency;
    public final DecimalFormat formatter;

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
        if (formatters.containsKey(currency)) {
            return formatters.get(currency);
        } else {
            final DecimalFormat formatter = new DecimalFormat(currency + " 0.00");
            formatters.put(currency, formatter);
            return formatter;
        }
    }

    protected Money(double value, Currency currency) {
        this.value = value;
        this.currency = currency;
        this.formatter = findOrCreateFormatter();
    }

    public String getValue() {
        return formatter.format(value);
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
        typist = defaultTypist;

        return this;
    }

    public Money asDifference() {
        typist = new Function<Money, String>() {
            @Override
            public String apply(final Money money) {
                return(money.isPositive() ? "+" : "") + money.getValue();
            }
        };

        return this;
    }

    @Override
    public String toString() {
        return typist.apply(this);
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
