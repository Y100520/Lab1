/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.labaratory1.view;

import javax.swing.*;

public class Error {
    public static void showError(String message) {
        JOptionPane.showMessageDialog(
            null, message, "Ошибка", JOptionPane.ERROR_MESSAGE
        );
    }
}