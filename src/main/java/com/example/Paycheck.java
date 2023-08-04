package com.example;

import java.util.ArrayList;
import java.util.List;

public class Paycheck {
    private final Employee employee;
    private final Competence competence;
    private final Money value;
    private final int workedDays;
    private final int nightShiftHours;
    private final Money overtimeValue;
    private final Money closedSectorValue;
    private final boolean healthCarePlanActive;
    private final boolean healthCarePlanForDependentActive;

    public Paycheck(final Employee employee, final Competence competence, final double value, final int workedDays,
            final int nightShiftHours, final double overtimeValue, final double closedSectorValue,
            final boolean healthCarePlanActive, final boolean healthCarePlanForDependentActive) {
        this.employee = employee;
        this.competence = competence;
        this.value = Money.findOrCreate(value);
        this.workedDays = workedDays;
        this.nightShiftHours = nightShiftHours;
        this.overtimeValue = Money.findOrCreate(overtimeValue);
        this.closedSectorValue = Money.findOrCreate(closedSectorValue);
        this.healthCarePlanActive = healthCarePlanActive;
        this.healthCarePlanForDependentActive = healthCarePlanForDependentActive;

        employee.addPaycheck(this);
    }

    public Paycheck(final Employee employee, final Competence competence, final double value) {
        this(employee, competence, value, 0, 0, 0.0,
                0.0, false, false);
    }

    public Money getValue() {
        return value;
    }

    public double getNumericValue() {
        return value.value;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Competence getCompetence() {
        return competence;
    }

    public int getWorkedDays() {
        return workedDays;
    }

    public int getNightShiftHours() {
        return nightShiftHours;
    }

    public Money getOvertimeValue() {
        return overtimeValue;
    }

    public Money getClosedSectorValue() {
        return closedSectorValue;
    }

    public String getDetails() {
        return getWorkedDays() + " dia(s) de Salário-Base\n"
                + (nightShiftHours > 0 ? (getNightShiftHours() + "h de Adicional noturno\n") : "")
                + (overtimeValue.isPositive() ? (overtimeValue + " de Hora extra (Cl 4)\n") : "")
                + (closedSectorValue.isPositive() ? (closedSectorValue + " de Setor fechado\n") : "")
                + (healthCarePlanActive ? "Plano de saúde do titular\n" : "")
                + (healthCarePlanForDependentActive ? "Plano de saúde do dependente\n" : "");
    }

    public String getSummary(final boolean withWorkedDays, final boolean withNightShiftHours,
            final boolean withOvertimeValue, final boolean withClosedSectorValue,
            final boolean withHealthCarePlanActive,
            final boolean withHealthCarePlanForDependentActive) {
        List<String> result = new ArrayList<>();

        if (withWorkedDays) {
            result.add(getWorkedDays() + " dia(s)");
        }

        if (withNightShiftHours && nightShiftHours > 0) {
            result.add(getNightShiftHours() + "h Noturno");
        }

        if (withOvertimeValue && overtimeValue.isPositive()) {
            result.add(getOvertimeValue() + " de Extra (Cl 4)");
        }

        if (withClosedSectorValue && closedSectorValue.isPositive()) {
            result.add(getClosedSectorValue() + " de Setor fechado");
        }

        if (withHealthCarePlanActive && healthCarePlanActive) {
            result.add("Plano de saúde do titular");
        }

        if (withHealthCarePlanForDependentActive && healthCarePlanForDependentActive) {
            result.add("Plano de saúde do dependente");
        }

        return String.join("\n", result);
    }

    public boolean isHealthCarePlanActive() {
        return healthCarePlanActive;
    }

    public boolean isHealthCarePlanForDependentActive() {
        return healthCarePlanForDependentActive;
    }

    public boolean hasWorkedAtLeast(final int days) {
        return workedDays >= days;
    }

    public boolean hasNightShiftHoursAtLeast(final int hours) {
        return nightShiftHours >= hours;
    }

    public boolean hasOvertimeValueAtLeast(final double value) {
        return overtimeValue.value >= value;
    }

    public boolean hasClosedSectorAtLeast(final double value) {
        return closedSectorValue.value >= value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((employee == null) ? 0 : employee.hashCode());
        result = prime * result + ((competence == null) ? 0 : competence.hashCode());
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
        Paycheck other = (Paycheck) obj;
        if (employee == null) {
            if (other.employee != null)
                return false;
        } else if (!employee.equals(other.employee))
            return false;
        if (competence == null) {
            if (other.competence != null)
                return false;
        } else if (!competence.equals(other.competence))
            return false;
        return true;
    }

    @Override
    public String toString() {

        return competence.toString() + " | " + employee.getRegistration() + " - " + employee.getName() + " | "
                + getValue();
    }
}
