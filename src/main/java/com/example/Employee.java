package com.example;

import java.util.HashSet;
import java.util.Set;

public class Employee {
    private static final HashSet<Employee> all = new HashSet<>();

    private final int registration;
    private final String name;
    private final HashSet<Paycheck> paychecks = new HashSet<>();

    private Employee(final int registration, final String name) {
        this.registration = registration;
        this.name = name;

        all.add(this);
    }

    public static Employee findOrCreate(final int registration, final String name) {
        final Employee employeeToFind = new Employee(registration, name);

        for (final Employee employee : all) {
            if (employee.equals(employeeToFind)) {
                return employee;
            }
        }

        return employeeToFind;
    }

    public static Set<Employee> getAll() {
        return new HashSet<>(all);
    }

    public int getRegistration() {
        return registration;
    }

    public String getName() {
        return name;
    }

    public HashSet<Paycheck> getPaychecks() {
        return new HashSet<>(paychecks);
    }

    public void addPaycheck(Paycheck paycheck) {
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
    public boolean equals(Object obj) {
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
