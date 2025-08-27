/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.labaratory1.view;

import javax.swing.*;
import java.awt.*;

public class AppView extends JFrame {
    private final JButton importButton, calculationButton, exportButton, exitButton;
    private final JTextArea outputArea;
    private final JTextField confidenceField;

    public AppView() {
        setTitle("Программа расчета статистических данных");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        importButton = new JButton("Импорт данных");
        calculationButton = new JButton("Обработка данных");
        exportButton = new JButton("Экспорт данных");
        exitButton = new JButton("Выход");
        outputArea = new JTextArea();
        confidenceField = new JTextField("0.95", 5);

        JPanel controlPanel = new JPanel();
        controlPanel.add(importButton);
        controlPanel.add(calculationButton);
        controlPanel.add(exportButton);
        controlPanel.add(exitButton);
        controlPanel.add(new JLabel("Уровень доверия:"));
        controlPanel.add(confidenceField);

        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    public boolean[] showCalculationOptions() {
        String[] options = {
                "Среднее геометрическое", "Среднее арифметическое",
                "Стандартное отклонение", "Размах", "Количество элементов",
                "Коэффициент вариации", "Минимум", "Максимум",
                "Дисперсия", "Доверительный интервал", "Ковариация"
        };

        JCheckBox[] checkBoxes = new JCheckBox[options.length];
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (int i = 0; i < options.length; i++) {
            checkBoxes[i] = new JCheckBox(options[i], true);
            panel.add(checkBoxes[i]);
        }

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Выберите параметры",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            boolean[] selected = new boolean[options.length];
            for (int i = 0; i < checkBoxes.length; i++) {
                selected[i] = checkBoxes[i].isSelected();
            }
            return selected;
        }
        return null;
    }

    public double getConfidenceLevel() throws IllegalArgumentException {
        try {
            double level = Double.parseDouble(confidenceField.getText());
            if (level <= 0 || level >= 1)
                throw new IllegalArgumentException("Уровень доверия должен быть между 0 и 1");
            return level;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Необходимо ввести уровень доверия");
        }
    }

    public JButton getImportButton() {
        return importButton;
    }

    public JButton getCalculationButton() {
        return calculationButton;
    }

    public JButton getExportButton() {
        return exportButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }

    public JTextArea getOutputArea() {
        return outputArea;
    }
}