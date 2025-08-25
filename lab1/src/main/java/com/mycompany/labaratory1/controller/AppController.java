/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.labaratory1.controller;

import com.mycompany.labaratory1.model.DataModel;
import com.mycompany.labaratory1.view.AppView;
import com.mycompany.labaratory1.view.Error;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class AppController {
    private final AppView view;
    private final DataModel model;

    public AppController(AppView view, DataModel model) {
        this.view = view;
        this.model = model;
        attachEvents();
    }

    private void attachEvents() {
        view.getImportButton().addActionListener(e -> importData());
        view.getCalculationButton().addActionListener(e -> calculate());
        view.getExportButton().addActionListener(e -> exportData());
        view.getExitButton().addActionListener(e -> System.exit(0));
    }

    private void importData() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                model.importFromExcel(chooser.getSelectedFile());
                view.getOutputArea().setText("Данные успешно загружены.");
            } catch (Exception e) {
                Error.showError("Ошибка импорта, необходимо выбрать файл Excel: ");
            }
        }
    }

    private void calculate() {
        try {
            boolean[] options = view.showCalculationOptions();
            if (options == null) return;

            double confidence = view.getConfidenceLevel();
            String result = model.getStatistics().generateReport(options, confidence);
            view.getOutputArea().setText(result);
        } catch (IllegalArgumentException e) {
            Error.showError(e.getMessage());
        } catch (Exception e) {
            Error.showError("Ошибка расчета: " + e.getMessage());
        }
    }

    private void exportData() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".xlsx")) path += ".xlsx";
            try {
                model.exportResults(path, 0.95);
                JOptionPane.showMessageDialog(view, "Экспорт завершен.");
            } catch (IOException e) {
                Error.showError("Ошибка записи в файл: " + e.getMessage());
            } catch (Exception e) {
                Error.showError("Ошибка экспорта: " + e.getMessage());
            }
        }
    }
}
