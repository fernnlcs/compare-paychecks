package com.example;

import java.io.File;
import java.nio.file.Path;
import java.util.Scanner;

public class DriveFinder {
    private static final String identifier = Env.driveIdentifier;

    public static Path findDrive() {
        final File[] drives = File.listRoots();
        Path defaultDrive = new File("").toPath();

        for (final File drive : drives) {
            final String driveLabel = getDriveLabel(drive);

            if (driveLabel.contains(identifier)) {
                return drive.toPath();
            } else if (driveLabel.contains("Google")) {
                defaultDrive = drive.toPath();
            }
        }

        return defaultDrive;
    }

    public static String getDriveLabel(final File drive) {
        final String drivePath = drive.getAbsolutePath();
        final String driveName = drivePath.substring(0, 2);

        String driveLabel = "";

        try {
            final Process process = Runtime.getRuntime().exec("cmd /c vol " + driveName);
            final Scanner scanner = new java.util.Scanner(process.getInputStream());
            
            if (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                driveLabel = line.substring(line.lastIndexOf("\\") + 1);
            }

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return driveLabel;
    }
}
