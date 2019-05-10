package com.skocur.chromerunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

import javax.swing.*;

/**
 * <h1>ChromeRunner</h1>
 * This class is responsible for creating GUI and executing certain
 * threads based on user input.
 *
 * @author Szymon Kocur
 */
public class ChromeRunner extends Application {

    private final int HEIGHT = 300;
    private final int WIDTH = 100;

    /**
     * This is "starter" method which starts process of creating GUI.
     *
     * @param args Arguments passed to Java app
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("ChromeRunner v.1.0");
        stage.setOnCloseRequest((e) -> {
            Platform.exit();
            System.exit(0);
        });

        VBox vbox = new VBox();
        Button startButton = new Button("Start!");
        Label threadNumberLabel = new Label("Number of web browsers:");
        Label urlLabel = new Label("FULL URL address of target website");
        Label infoLabel = new Label("You ought to remember about list of working proxy servers from proxy_list.txt file, "
                + "in which total number of proxies shouldn't be smaller than number of running web browsers. "
                + "You can find working proxy servers here: https://www.sslproxies.org/");
        TextField threadNumberField = new TextField();
        TextField websiteURL = new TextField();

        startButton.setOnAction((e) -> {
                int threadNumber = Integer.parseInt(threadNumberField.getText());
                execute(threadNumber, websiteURL.getText());
        });

        infoLabel.setWrapText(true);

        threadNumberField.setPromptText("Number of web browsers");
        threadNumberField.setText("5");
        threadNumberField.setPrefColumnCount(10);

        websiteURL.setPromptText("FULL URL address of target website");
        websiteURL.setText("http://www.google.pl");
        websiteURL.setPrefColumnCount(30);

        vbox.setSpacing(10);
        VBox.setMargin(infoLabel, new Insets(20, 20, 5, 20));
        VBox.setMargin(threadNumberLabel, new Insets(20, 20, 0, 20));
        VBox.setMargin(threadNumberField, new Insets(0, 20, 20, 20));
        VBox.setMargin(urlLabel, new Insets(20, 20, 0, 20));
        VBox.setMargin(websiteURL, new Insets(0, 20, 20, 20));
        VBox.setMargin(startButton, new Insets(20, 20, 20, 20));

        ObservableList list = vbox.getChildren();
        list.addAll(infoLabel, threadNumberLabel, threadNumberField, urlLabel, websiteURL, startButton);

        Scene myScene = new Scene(vbox, HEIGHT, WIDTH);
        stage.setScene(myScene);
        stage.setMinHeight(550);
        stage.setMinWidth(300);
        stage.setMaxHeight(550);
        stage.setMaxWidth(300);

        stage.show();

        LogWindow.run();
    }

    /**
     * Given method executes new browsers in separated threads.
     * Each running web browser has own properties such as IP and PORT.
     *
     * @param threadsNumber Number of threads
     * @param url           URL address of target website
     */
    private void execute(int threadsNumber, String url) {
        File proxiesFile = new File("proxy_list.txt");

        ExecutorService executor = Executors.newFixedThreadPool(threadsNumber);

        int counter = 0;

        try {
            Scanner sc = new Scanner(proxiesFile);

            while (sc.hasNext()) {
                if (counter == threadsNumber) {
                    break;
                }

                String[] data = sc.nextLine().replaceAll("\\s+", "").split(":");
                executor.execute(new Website(url, data[0], data[1]));

                counter++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "File not found", "Error occured", JOptionPane.ERROR_MESSAGE);
        }

        System.setProperty("webdriver.chrome.driver",
                "chromedriver.exe");

        LogWindow.addLog("Chrome instances: " + counter);

        executor.shutdown();
    }
}
