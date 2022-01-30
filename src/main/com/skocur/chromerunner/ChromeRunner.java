package com.skocur.chromerunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

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
    TableView<WebPage>  tableContent = null;
    Connection connection = null;
    Button startButton = null;

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
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:C:/ChromeRunnerDB.db");

            Initialize(stage);

            fillTable(connection);
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    private void fillTable(Connection connection)
    {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:C:/ChromeRunnerDB.db");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            ResultSet rs = statement.executeQuery("select * from pages");

            final ObservableList<WebPage> data = FXCollections.observableArrayList();

            while (rs.next()) {
                // read the result set
                String id = rs.getString("id");
                String name = rs.getString("name");
                String url = rs.getString("url");
                String instances = rs.getString("instances");

                if (name != null && url != null)
                    data.add(new WebPage(id, name, url, instances));
            }

            tableContent.getItems().clear();
            tableContent.setItems(data);
            tableContent.refresh();
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    private void Initialize(Stage stage)
    {
        stage.setTitle("ChromeRunner v.1.2");
        stage.setOnCloseRequest((e) -> {
            Platform.exit();
            System.exit(0);
        });

        VBox vbox = new VBox();
        startButton = new Button("Start!");
        Label threadNumberLabel = new Label("Number of web browsers:");
        Label nameLabel = new Label("Name of target website:");
        Label urlLabel = new Label("FULL URL address of target website");
        Label infoLabel = new Label("You ought to remember about list of working proxy servers from proxy_list.txt file, "
                + "in which total number of proxies shouldn't be smaller than number of running web browsers. "
                + "You can find working proxy servers here: https://www.sslproxies.org/");
        TextField threadNumberField = new TextField();
        TextField websiteURL = new TextField();
        TextField websiteName = new TextField();
        HBox hbox = new HBox();
        Button btnAddItem = new Button();
        Button btnRemoveItem = new Button();

        //region ButtonsEvents

        startButton.setOnAction((e) -> {
            WebPage item = tableContent.getSelectionModel().getSelectedItem();

            if(item != null)
                execute(item);
            else
                infoBox("Please select an webpage before start instances.","Information");
        });

        btnAddItem.setOnAction((e) -> {

            String name = websiteName.getText();
            String url = websiteURL.getText();
            String instances = threadNumberField.getText();

            if((name != null && !name.isEmpty()) && (url != null && !url.isEmpty()) && (instances != null && !instances.isEmpty()))
            {
                WebPage item = new WebPage("0", name, url, instances);
                int result = insert(item);
                if(result != 0)
                {

                    fillTable(connection);

                    websiteName.setText("");
                    websiteURL.setText("");
                    threadNumberField.setText("");

                    System.out.println("Record added!");
                }
                else
                {
                    System.out.println("Failure to add record!");
                }
            }
            else
            {
                System.out.println("Please fill all text fields!");
            }
        });

        btnRemoveItem.setOnAction((e) -> {
            WebPage item = tableContent.getSelectionModel().getSelectedItem();
            delete(item.getId());
            fillTable(connection);

            System.out.println("Record removed!");
        });

        //endregion

        infoLabel.setWrapText(true);

        threadNumberField.setPromptText("Number of web browsers");
        threadNumberField.setPrefColumnCount(10);

        websiteURL.setPromptText("FULL URL address of target website");
        websiteURL.setPrefColumnCount(30);

        websiteName.setPromptText("Target website name");
        websiteName.setPrefColumnCount(20);

        //region TableMenu

        hbox.setSpacing(10);

        btnAddItem.setText("Add");
        btnRemoveItem.setText("Remove");

        hbox.setMargin(btnRemoveItem, new Insets(20, 20, 5, 20));
        hbox.setMargin(btnAddItem, new Insets(20, 20, 5, 20));

        ObservableList tableMenu = hbox.getChildren();
        tableMenu.addAll(btnRemoveItem, btnAddItem);

        //endregion

        //region FillTable

        tableContent = new TableView<WebPage>();

        TableColumn idColumn = new TableColumn("id");
        idColumn.setPrefWidth(100);
        idColumn.setVisible(false);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn nameColumn = new TableColumn("name");
        nameColumn.setPrefWidth(120);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn urlColumn = new TableColumn("url");
        urlColumn.setPrefWidth(180);
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));

        TableColumn instancesColumn = new TableColumn("instances");
        instancesColumn.setPrefWidth(100);
        instancesColumn.setCellValueFactory(new PropertyValueFactory<>("instances"));

        tableContent.getColumns().addAll(idColumn, nameColumn, urlColumn, instancesColumn);

        //endregion

        vbox.setSpacing(10);
        VBox.setMargin(infoLabel, new Insets(5, 5, 5, 5));

        VBox.setMargin(threadNumberLabel, new Insets(5, 5, 0, 5));
        VBox.setMargin(threadNumberField, new Insets(0, 5, 5, 5));

        VBox.setMargin(nameLabel, new Insets(5, 5, 0, 5));
        VBox.setMargin(websiteName, new Insets(0, 5, 5, 5));

        VBox.setMargin(urlLabel, new Insets(5, 5, 0, 5));
        VBox.setMargin(websiteURL, new Insets(0, 5, 5, 5));

        VBox.setMargin(hbox, new Insets(5, 5, 5, 5));
        VBox.setMargin(startButton, new Insets(5, 5, 5, 5));

        ObservableList list = vbox.getChildren();
        list.addAll(infoLabel, threadNumberLabel, threadNumberField, nameLabel, websiteName, urlLabel, websiteURL, hbox, tableContent, startButton);

        Scene myScene = new Scene(vbox, HEIGHT, WIDTH);
        stage.setScene(myScene);
        stage.setMinHeight(550);
        stage.setMinWidth(300);
        stage.setMaxHeight(550);
        stage.setMaxWidth(300);

        stage.show();

        LogWindow.run();
    }

    private int insert(WebPage item)
    {
        int result = 0;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:C:/ChromeRunnerDB.db");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            result = statement.executeUpdate("INSERT INTO pages (name, url, instances) VALUES ('" + item.getName() + "', '" + item.getUrl() + "', '" + item.getInstances() + "')");
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
            result = 0;
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return result;
    }

    private int delete(String id)
    {
        int result = 0;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:C:/ChromeRunnerDB.db");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            result = statement.executeUpdate("DELETE FROM pages WHERE id = '" + id + "'");
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
            result = 0;
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return result;
    }

    public static void infoBox(String infoMessage, String titleBar)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }

    /**
     * Given method executes new browsers in separated threads.
     * Each running web browser has own properties such as IP and PORT.
     *
     * @param page webpage item
     */
    private void execute(WebPage page) {

        File proxiesFile = new File("proxy_list.txt");

        int threadsNumber = Integer.parseInt(page.getInstances());
        String url = page.getUrl();
        
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
