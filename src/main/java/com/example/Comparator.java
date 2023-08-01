package com.example;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.example.views.IndividualView;

public class Comparator {
    private static Path inputFolderPath;
    private final Payroll previousPayroll;
    private final Payroll currentPayroll;

    public Comparator() throws Exception {
        setRootPath();
        final ExecutorService threadpool = Executors.newSingleThreadExecutor();

        Future<File> previousFile = chooseFile(threadpool, "Selecione o PDF do contracheque ANTERIOR");
        Future<File> currentFile = chooseFile(threadpool, "Selecione o PDF do contracheque ATUAL");
        previousPayroll = new Payroll(previousFile.get());
        currentPayroll = new Payroll(currentFile.get());

        threadpool.shutdown();
    }

    public static Path getInputFolderPath() {
        return inputFolderPath.toAbsolutePath();
    }

    // Método para descobrir em qual unidade os contracheques ficam
    private static void setRootPath() {
        final Path drive = DriveFinder.findDrive();

        inputFolderPath = drive.resolve(Env.myDrive).resolve(Env.paychecks);
    }

    // Método para buscar um arquivo PDF
    private static Future<File> chooseFile(final ExecutorService threadpool, final String title) throws Exception {
        return threadpool.submit(new Callable<File>() {
            @Override
            public File call() throws Exception {
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle(title);
                fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos PDF", "pdf"));
                fileChooser.setCurrentDirectory(inputFolderPath.toFile());

                final int userSelection = fileChooser.showOpenDialog(null);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    inputFolderPath = fileChooser.getSelectedFile().getParentFile().toPath();
                    return fileChooser.getSelectedFile();
                } else {
                    throw new Exception("Não foi possível escolher o arquivo");
                }
            }
        });
    }

    public HashSet<IndividualView> compare() {
        final Set<Employee> employees = Employee.getInstances();
        final HashSet<IndividualView> result = new HashSet<>();

        for (final Employee employee : employees) {
            final IndividualView view = new IndividualView(employee, previousPayroll.getCompetence(),
                    currentPayroll.getCompetence());

            if (view.isValid()) {
                result.add(view);
            }
        }

        return result;
    }

    public Payroll getPreviousPayroll() {
        return previousPayroll;
    }

    public Payroll getCurrentPayroll() {
        return currentPayroll;
    }

}
