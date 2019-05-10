package com.skocur.chromerunner;

import com.sun.javafx.binding.SelectBinding;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LogWindow {

    private static StringBuilder stringBuilder = new StringBuilder();
    private static Label label = new Label();
    private static boolean isRun = false;

    public static void run() {
        if (!isRun) {
            isRun = true;

            setWindow();
        }
    }

    private static void setWindow() {
        Stage stage = new Stage();

        VBox box = new VBox();
        box.setSpacing(10);

        ObservableList list = box.getChildren();
        list.addAll(label);

        Scene scene = new Scene(box, 500, 800);
        stage.setScene(scene);
        stage.setMaxWidth(500);
        stage.setMaxHeight(800);
        stage.show();
    }

    public static void addLog(String log) {
        stringBuilder.append(log);
        stringBuilder.append("\n");
        System.out.println(stringBuilder.toString());


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                label.setText(stringBuilder.toString());
            }
        });
    }
}
