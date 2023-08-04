package com.example.views;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.example.Comparator;
import com.example.Competence;
import com.example.Employee;
import com.example.FileDataHandler;
import com.example.Money;
import com.example.Paycheck;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;

public class IndividualView {
    private static final Money initialValue = Money.findOrCreate(0);
    private static final String individualPaychecksFolder = "Por nome";
    private static final Desktop desktop = Desktop.getDesktop();

    private final Paycheck previousPaycheck;
    private final Paycheck currentPaycheck;
    private final String registration;
    private final String name;
    private final Money previousValue;
    private final String previousSummary;
    private final Money currentValue;
    private final String currentSummary;
    private final String previousDetails;
    private final String currentDetails;
    private final Money difference;
    private final File previousPdf;
    private final File currentPdf;
    private final List<Hyperlink> hyperlinks = new ArrayList<>();

    public IndividualView(final Employee employee, final Competence previousCompetence,
            final Competence currentCompetence) {
        // Definir o nome e matrícula do funcionário
        registration = String.format("%06d", employee.getRegistration());
        name = employee.getName();

        Paycheck previousPaycheck = new Paycheck(employee, previousCompetence, 0.0);
        Paycheck currentPaycheck = new Paycheck(employee, currentCompetence, 0.0);

        // Buscar os valores
        for (final Paycheck paycheck : employee.getPaychecks()) {
            if (paycheck.equals(previousPaycheck) && previousPaycheck.getValue().equals(initialValue)) {
                previousPaycheck = paycheck;
            }
            if (paycheck.equals(currentPaycheck) && currentPaycheck.getValue().equals(initialValue)) {
                currentPaycheck = paycheck;
            }
        }

        this.previousPaycheck = previousPaycheck;
        this.currentPaycheck = currentPaycheck;

        // Definindo as informações que vão no resumo
        final boolean withWorkedDays = previousPaycheck.getWorkedDays() != currentPaycheck.getWorkedDays();
        final boolean withNightShiftHours = previousPaycheck.getNightShiftHours() != currentPaycheck
                .getNightShiftHours();
        final boolean withOvertimeValue = previousPaycheck.getOvertimeValue() != currentPaycheck.getOvertimeValue();
        final boolean withClosedSectorValue = previousPaycheck.getClosedSectorValue() != currentPaycheck
                .getClosedSectorValue();
        final boolean withHealthCarePlanActive = previousPaycheck.isHealthCarePlanActive() != currentPaycheck
                .isHealthCarePlanActive();
        final boolean withHealthCarePlanForDependentActive = previousPaycheck
                .isHealthCarePlanForDependentActive() != currentPaycheck.isHealthCarePlanForDependentActive();

        // Definir os valores, garantindo que serão inicializados
        previousValue = previousPaycheck.getValue();
        previousSummary = previousPaycheck.getSummary(withWorkedDays, withNightShiftHours, withOvertimeValue,
                withClosedSectorValue, withHealthCarePlanActive, withHealthCarePlanForDependentActive);
        currentValue = currentPaycheck.getValue();
        currentSummary = currentPaycheck.getSummary(withWorkedDays, withNightShiftHours, withOvertimeValue,
                withClosedSectorValue, withHealthCarePlanActive, withHealthCarePlanForDependentActive);
        previousDetails = previousPaycheck.getDetails();
        currentDetails = currentPaycheck.getDetails();

        difference = Money.findOrCreate(currentPaycheck.getNumericValue() - previousPaycheck.getNumericValue())
                .asDifference();

        // Definir caminhos para os PDFs
        final Path basePath = Comparator.getInputFolderPath().getParent().resolve(individualPaychecksFolder);
        previousPdf = basePath.resolve(String.valueOf(previousCompetence.year))
                .resolve(FileDataHandler.formatMonth(previousCompetence.month))
                .resolve(previousCompetence.toString() + " " + name + ".pdf").toFile();
        currentPdf = basePath.resolve(String.valueOf(currentCompetence.year))
                .resolve(FileDataHandler.formatMonth(currentCompetence.month))
                .resolve(currentCompetence.toString() + " " + name + ".pdf").toFile();

        if (previousPdf.exists()) {
            final Hyperlink previousHyperlink = new Hyperlink(previousCompetence.toString());

            previousHyperlink.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent event) {
                    try {
                        desktop.open(previousPdf);
                    } catch (IOException e) {
                        //
                    }
                }
            });

            hyperlinks.add(previousHyperlink);
        }

        if (currentPdf.exists()) {
            final Hyperlink currentHyperlink = new Hyperlink(currentCompetence.toString());

            currentHyperlink.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent event) {
                    try {
                        desktop.open(currentPdf);
                    } catch (IOException e) {
                        //
                    }
                }
            });

            hyperlinks.add(currentHyperlink);
        }
    }

    public boolean isValid() {
        return previousSummary != null || currentSummary != null;
    }

    public static Money getInitialvalue() {
        return initialValue;
    }

    public String getRegistration() {
        return registration;
    }

    public String getName() {
        return name;
    }

    public String getPreviousSummary() {
        return previousSummary;
    }

    public String getCurrentSummary() {
        return currentSummary;
    }

    public Money getDifference() {
        return difference;
    }

    public String getPreviousDetails() {
        return previousDetails;
    }

    public String getCurrentDetails() {
        return currentDetails;
    }

    public Money getPreviousValue() {
        return previousValue;
    }

    public Money getCurrentValue() {
        return currentValue;
    }

    public static String getIndividualpaychecksfolder() {
        return individualPaychecksFolder;
    }

    public Hyperlink[] getHyperlinks() {
        final Hyperlink[] result = new Hyperlink[hyperlinks.size()];

        for (int i = 0; i < result.length; i++) {
            result[i] = hyperlinks.get(i);
        }

        return result;
    }

    public File getPreviousPdf() {
        return previousPdf;
    }

    public File getCurrentPdf() {
        return currentPdf;
    }

    public Paycheck getPreviousPaycheck() {
        return previousPaycheck;
    }

    public Paycheck getCurrentPaycheck() {
        return currentPaycheck;
    }
}
