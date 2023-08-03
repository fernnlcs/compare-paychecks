package com.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class Payroll {
    private final PDFTextStripper textStripper;

    private final PDDocument document;
    private final int totalPages;
    private final Competence competence;
    private final List<Paycheck> paychecks = new ArrayList<>();

    public Payroll(final File file) throws IOException {
        // Definir o TextStripper
        textStripper = new PDFTextStripper();

        // Definir a data (competência)
        competence = Competence.findOrCreate(FileDataHandler.extractFileDate(file.getName()));

        // Abrir o arquivo PDF
        document = PDDocument.load(file);

        // Obter o número total de páginas no documento
        totalPages = document.getNumberOfPages();

        // Criar uma janela de progresso
        final JFrame progressFrame = new JFrame("Lendo os valores...");
        final JProgressBar progressBar = new JProgressBar(0, totalPages);
        progressBar.setStringPainted(true);
        progressFrame.add(progressBar);
        progressFrame.setSize(600, 100);
        progressFrame.setLocationRelativeTo(null);
        progressFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        progressFrame.setVisible(true);

        // Iterar sobre cada página
        for (int i = 0; i < totalPages; i++) {
            textStripper.setStartPage(i + 1);
            textStripper.setEndPage(i + 1);

            final StringBuilder pageText = new StringBuilder(textStripper.getText(document));

            // Função para obter a informação da linha abaixo
            final Function<String, String> getLineUnder = new Function<String, String>() {

                @Override
                public String apply(final String label) {
                    return pageText.substring(pageText.lastIndexOf(label)).split("\n")[1].trim();
                }

            };

            // Definir funcionário
            final String employeeLine = getLineUnder.apply(Env.EMPLOYEE_LABEL);
            final Employee employee = Employee.findOrCreate(
                    Integer.parseInt(employeeLine.substring(0, employeeLine.indexOf(" "))), // Registration
                    employeeLine.substring(employeeLine.indexOf(" ") + 1).trim()); // Name

            // Definir valor
            final double value = Double.parseDouble(getLineUnder.apply(Env.VALUE_LABEL)
                    .replace(".", "")
                    .replace(",", "."));

            // Definir competência
            final String competenceDescription = getLineUnder.apply(Env.COMPETENCE_LABEL).replace(" de ", "_");
            final String[] competenceParts = competenceDescription.split("_");
            final Competence competence = Competence.findOrCreate(
                    Integer.parseInt(competenceParts[1]), // Year
                    FileDataHandler.getMonthNumber(competenceParts[0])); // Month

            // Criar contracheque
            final Paycheck paycheck = new Paycheck(employee, competence, value,
                    FileDataHandler.extractPaycheckReference(pageText, Env.WORKED_DAYS_LABEL),
                    FileDataHandler.extractPaycheckReference(pageText, Env.NIGHT_SHIFT_HOURS_LABEL),
                    FileDataHandler.extractPaycheckValue(pageText, Env.OVERTIME_LABEL),
                    FileDataHandler.extractPaycheckValue(pageText, Env.CLOSED_SECTOR_LABEL),
                    FileDataHandler.checkIfPaycheckLabelExists(pageText, Env.HEALTH_CARE_PLAN_LABEL),
                    FileDataHandler.checkIfPaycheckLabelExists(pageText, Env.HEALTH_CARE_PLAN_FOR_DEPENDENT_LABEL));
            // FileDataHandler.extractPaycheckDetails(paycheck, pageText);

            paychecks.add(paycheck);

            // Atualizar a barra de progresso
            progressBar.setValue(progressBar.getValue() + 1);
        }

        // Fechar a janela de progresso
        progressFrame.dispose();

        // Fechar o documento original
        document.close();
    }

    public static Future<Payroll> read(final ExecutorService threadpool, final File file) {
        return threadpool.submit(new Callable<Payroll>() {
            @Override
            public Payroll call() throws Exception {
                return new Payroll(file);
            }
        });
    }

    public Competence getCompetence() {
        return competence;
    }
}
