package com.example;

import java.util.HashSet;

public class Competence {
    private static final HashSet<Competence> all = new HashSet<>();

    public final int year;
    public final int month;

    private Competence(final int year, final int month) {
        this.year = year;
        this.month = month;
    }

    public static Competence findOrCreate(final int year, final int month) {
        final Competence competenceToFind = new Competence(year, month);

        for (final Competence competence : all) {
            if (competence.equals(competenceToFind)) {
                return competence;
            }
        }

        all.add(competenceToFind);
        return competenceToFind;
    }

    public static Competence findOrCreate(final String fileDate) {
        final String[] parts = fileDate.split("\\.");
        final int year = Integer.parseInt(parts[0]);
        final int month = Integer.parseInt(parts[1]);

        return findOrCreate(year, month);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + year;
        result = prime * result + month;
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
        final Competence other = (Competence) obj;
        if (year != other.year)
            return false;
        if (month != other.month)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.join(".", String.valueOf(year), String.format("%02d", month));
    }
}
