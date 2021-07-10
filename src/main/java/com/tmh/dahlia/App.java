package com.tmh.dahlia;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class App extends Application {
    private final static String DEFAULT_INFO_ROW_VALUE = "NA";

    private static final Logger LOGGER = LogManager.getLogger(App.class);

    private final SessionTimer sessionTimer = new SessionTimer();
    private WebDriver webDriver;

    private enum SettingKeys {
        DRIVER_PATH,
        UPDATE_TIME
    }

    private HashMap<SettingKeys, Object> settings;

    private Scene mainScene;

    @Override
    public void start(Stage stage) {
        stage.setOnCloseRequest(windowEvent -> System.exit(0));
        buildUi(stage);
    }

    public static void main(String[] args) {
        launch();
    }

    private void buildUi(Stage stage) {
        readSettings();
        mainScene = generateMainScene(stage);

        stage.setScene(mainScene);
        stage.setResizable(false);
        stage.setTitle("STOCK");
        stage.show();
    }

    private void readSettings() {
        settings = new HashMap<>();
        try {
            Scanner scanner = new Scanner(new File("settings"));
            String driverPath = scanner.nextLine();
            String updateTime = scanner.nextLine();
            settings.put(SettingKeys.DRIVER_PATH, driverPath);
            settings.put(SettingKeys.UPDATE_TIME, Integer.valueOf(updateTime));
        } catch (Exception e) {
            settings.put(SettingKeys.DRIVER_PATH, "chromedriver");
            settings.put(SettingKeys.UPDATE_TIME, 5000);
        }
    }

    private void writeSettings() {

    }

    private Scene generateMainScene(Stage stage) {
        VBox mainLayout = new VBox();
        Button buttonSettings = new Button("Settings");
        buttonSettings.setOnMouseClicked((Event) -> {
            stage.setScene(generateSettingsScene(stage));
        });
        mainLayout.getChildren().addAll(buttonSettings);

        return new Scene(mainLayout, 100, 100);
    }

    private Scene generateSettingsScene(Stage stage) {

        VBox mainLayout = new VBox();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Browsing Chrome driver");

        Label labelDriverPath = new Label((String) settings.get(SettingKeys.DRIVER_PATH));
        Button buttonBrowseDriver = new Button("Browse");

        buttonBrowseDriver.setOnMouseClicked((Event) -> {
            File driver = fileChooser.showOpenDialog(stage);
            if (driver != null) {
                labelDriverPath.setText(driver.getAbsolutePath());
            }
        });

        TextField textFieldUpdateTime = new TextField();
        textFieldUpdateTime.setText(((Integer) settings.get(SettingKeys.UPDATE_TIME)).toString());

        Node chromeDriverRow = generateSettingRow("Chrome driver", new Node[]{labelDriverPath, buttonBrowseDriver});
        Node updateTimeRow = generateSettingRow("Update time (in sec)", new Node[]{textFieldUpdateTime});

        Pane spacer = new Pane();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox actionRow = new HBox();
        Button buttonCancel = new Button("Cancel");
        buttonCancel.setOnMouseClicked((Event) -> {stage.setScene(mainScene);});
        Button buttonOK = new Button("OK");
        buttonOK.setOnMouseClicked((Event) -> {
            writeSettings();
            stage.setScene(mainScene);
        });
        actionRow.getChildren().addAll(buttonCancel, buttonOK);

        mainLayout.getChildren().addAll(chromeDriverRow, updateTimeRow, spacer, actionRow);

        return new Scene(mainLayout, 450, 300);
    }

    private Node generateSettingRow(String text, Node[] controls) {
        HBox row = new HBox();
        Label label = new Label(text);
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(label, spacer);
        for (Node control : controls) { row.getChildren().add(control); }
        return row;
    }


}