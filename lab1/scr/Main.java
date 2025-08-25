/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.labaratory1;

import com.mycompany.labaratory1.controller.AppController;
import com.mycompany.labaratory1.model.DataModel;
import com.mycompany.labaratory1.view.AppView;

public class Main {
    public static void main(String[] args) {
        DataModel model = new DataModel();
        AppView view = new AppView();
        AppController controller = new AppController(view, model);
        view.setVisible(true);
    }
}
