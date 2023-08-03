package com.example;

import java.util.HashSet;

public class Competence {
    // Todas as instâncias de competência criadas
    private static final HashSet<Competence> instances = new HashSet<>();

    // Ano e mês
    public final int year;
    public final int month;

    // Método para obter a instância da competência
    public static Competence findOrCreate(final int year, final int month) {
        // Criar uma nova instância para buscar através de comparação
        final Competence competenceToFind = new Competence(year, month);

        // Procurar se a instância já existe
        for (final Competence competence : instances) {
            if (competence.equals(competenceToFind)) {
                // Retornar a instância existente
                return competence;
            }
        }

        // Adicionar nova instância às existentes
        instances.add(competenceToFind);

        // Retornar nova instância
        return competenceToFind;
    }

    // Método para obter a instância da competência
    public static Competence findOrCreate(final String fileDate) {
        // Separar mês e ano em variáveis diferentes
        final String[] parts = fileDate.split("\\.");
        final int year = Integer.parseInt(parts[0]);
        final int month = Integer.parseInt(parts[1]);

        // Invocar o método principal
        return findOrCreate(year, month);
    }

    // Método para obter a instância da competência
    public static Competence findOrCreateByPage(final String pageText) {
        // Separar mês e ano em variáveis diferentes
        final String[] parts = pageText.split("\\.");
        final int year = Integer.parseInt(parts[0]);
        final int month = Integer.parseInt(parts[1]);

        // Invocar o método principal
        return findOrCreate(year, month);
    }

    // Construtor privado
    private Competence(final int year, final int month) {
        this.year = year;
        this.month = month;
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
