package com.skocur.chromerunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class Website implements Runnable {

    String url;
    String PROXY;
    String PORT;
    int id;

    public Website(String proxy, String port) {
        this.PROXY = proxy;
        this.PORT = port;
    }

    public Website(String url, String proxy, String port, int counter) {
        this.url = url;
        this.PROXY = proxy;
        this.PORT = port;
        this.id = counter;
    }

    @Override
    public void run() {
        String proxyServer = PROXY + ":" + PORT;

        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyServer);
        proxy.setSslProxy(proxyServer);

        ChromeOptions options = new ChromeOptions();
        options.setCapability("proxy", proxy);
        options.setCapability("chrome.switches", Arrays.asList("--proxy-server=" + proxyServer));

        WebDriver driver = new ChromeDriver(options);
        driver.get(url);

        System.out.println("Successfully opened");
    }
}

public class ChromeRunner extends Application {

    public static void main(String[] args) {

        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("ChromeRunner v.1.0");

        VBox vbox = new VBox();
        Button startButton = new Button("Start!");
        Label threadNumberLabel = new Label("Liczba przegl¹darek:");
        Label urlLabel = new Label("PELNY adres URL strony docelowej");
        Label infoLabel = new Label("Nale¿y pamietac o liœcie dzialaj¹cych serwerów Proxy z pliku proxy_list.txt, "
                + "których liczba powinna byc wieksza lub równa liczbie uruchamianych przegl¹darek. "
                + "Sprawne serwery Proxy zawiera strona: https://www.sslproxies.org/");
        TextField threadNumberField = new TextField();
        TextField websiteURL = new TextField();
        ObservableList list;

        final int HEIGHT = 300;
        final int WIDTH = 100;

        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                int threadNumber = Integer.parseInt(threadNumberField.getText());
                execute(threadNumber, websiteURL.getText());
            }
        });

        infoLabel.setWrapText(true);

        threadNumberField.setPromptText("Liczba przegl¹darek");
        threadNumberField.setText("5");
        threadNumberField.setPrefColumnCount(10);

        websiteURL.setPromptText("PELNY adres url strony");
        websiteURL.setText("http://www.google.pl");
        websiteURL.setPrefColumnCount(30);

        vbox.setSpacing(10);
        vbox.setMargin(infoLabel, new Insets(20, 20, 5, 20));
        vbox.setMargin(threadNumberLabel, new Insets(20, 20, 0, 20));
        vbox.setMargin(threadNumberField, new Insets(0, 20, 20, 20));
        vbox.setMargin(urlLabel, new Insets(20, 20, 0, 20));
        vbox.setMargin(websiteURL, new Insets(0, 20, 20, 20));
        vbox.setMargin(startButton, new Insets(20, 20, 20, 20));

        list = vbox.getChildren();
        list.addAll(infoLabel, threadNumberLabel, threadNumberField, urlLabel, websiteURL, startButton);

        Scene myScene = new Scene(vbox, HEIGHT, WIDTH);
        stage.setScene(myScene);
        stage.setMinHeight(550);
        stage.setMinWidth(300);
        stage.setMaxHeight(550);
        stage.setMaxWidth(300);

        stage.show();
    }

    private void execute(int threadsNumber, String url) {
        File proxiesFile = new File("proxy_list.txt");
        Scanner sc;

        List<String> proxies = new ArrayList<>();

        try {
            sc = new Scanner(proxiesFile);

            while (sc.hasNext()) {
                proxies.add(sc.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.setProperty("webdriver.chrome.driver",
                "chromedriver.exe");

        ExecutorService executor = Executors.newFixedThreadPool(threadsNumber);

        int counter = 0;

        for (String proxy : proxies) {
            if (counter == threadsNumber) {
                break;
            }

            String[] data = proxy.replaceAll("\\s+", "").split(":");
            executor.execute(new Website(url, data[0], data[1], counter));

            counter++;
        }

        System.out.println(counter);

        executor.shutdown();
    }
}
