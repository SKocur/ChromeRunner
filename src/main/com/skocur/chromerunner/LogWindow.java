package com.skocur.chromerunner;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
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
        int initialWidth = 300;
        int initialHeight= 800;

        Stage stage = new Stage();
        stage.setTitle("Log Window");
        stage.setMaxWidth(initialWidth);
        stage.setMaxHeight(initialHeight);

        Rectangle2D stageBounds = Screen.getPrimary().getVisualBounds();

        stage.setX(stageBounds.getWidth() - initialWidth);
        stage.setY(0);

        VBox box = new VBox();
        box.setSpacing(10);

        ObservableList list = box.getChildren();
        list.addAll(label);

        Scene scene = new Scene(box, initialWidth, initialHeight);
        stage.setScene(scene);

        stage.show();
    }

    public static void addLog(String log) {
        stringBuilder.append(log);
        stringBuilder.append("\n");

        Platform.runLater(() -> label.setText(stringBuilder.toString()) );
    }
}
