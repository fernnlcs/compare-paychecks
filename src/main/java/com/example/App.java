
package com.example;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.example.views.ComparatorView;

import javafx.application.Application;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        try {
            Application.launch(ComparatorView.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
