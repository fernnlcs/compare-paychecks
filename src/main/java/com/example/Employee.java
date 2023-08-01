package com.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Employee {
    // Todas as instâncias de funcionário criadas
    private static final HashSet<Employee> instances = new HashSet<>();

    // Matrícula e nome
    private final int registration;
    private final String name;

    // Conjunto de contracheques
    private final List<Paycheck> paychecks = new ArrayList<>();

    // Método para obter uma instância do funcionário
    public static Employee findOrCreate(final int registration, final String name) {
        // Criar uma nova instância para buscar através de comparação
        final Employee employeeToFind = new Employee(registration, name);

        // Procurar se a instância já existe
        for (final Employee employee : instances) {
            if (employee.equals(employeeToFind)) {
                // Retornar a instância existente
                return employee;
            }
        }

        // Adicionar nova instância às existentes
        instances.add(employeeToFind);

        // Retornar nova instância
        return employeeToFind;
    }

    // Construtor privado
    private Employee(final int registration, final String name) {
        // Atribuir valores
        this.registration = registration;
        this.name = name;
    }

    // Método para obter todas as instâncias de funcionário
    public static Set<Employee> getInstances() {
        // Retornar uma cópia do conjunto
        return new HashSet<>(instances);
    }

    // Método para obter a matrícula
    public int getRegistration() {
        return registration;
    }

    // Método para obter o nome
    public String getName() {
        return name;
    }

    // Método para obter os contracheques do funcionário
    public HashSet<Paycheck> getPaychecks() {
        // Retornar uma cópia do conjunto
        return new HashSet<>(paychecks);
    }

    // Método para adicionar um contracheque ao conjunto
    public void addPaycheck(final Paycheck paycheck) {
        paychecks.add(paycheck);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + registration;
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
        Employee other = (Employee) obj;
        if (registration != other.registration)
            return false;
        return true;
    }
}
