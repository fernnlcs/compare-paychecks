package com.example;

import java.util.HashSet;
import java.util.Set;

public class Currency {
    public static final Set<Currency> all = new HashSet<>();
    public final String symbol;

    public static Currency findOrCreate(final String symbol) {
        final Currency currencyToFind = new Currency(symbol);

        for (final Currency currency : all) {
            if (currency.equals(currencyToFind)) {
                return currency;
            }
        }

        all.add(currencyToFind);
        return currencyToFind;
    }

    private Currency(final String symbol) {
        this.symbol = symbol;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
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
        final Currency other = (Currency) obj;
        if (symbol == null) {
            if (other.symbol != null)
                return false;
        } else if (!symbol.equals(other.symbol))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
