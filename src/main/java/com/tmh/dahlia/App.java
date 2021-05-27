package com.tmh.dahlia;

import com.tmh.dahlia.ui.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App extends Application {
    private final static String DEFAULT_INFO_ROW_VALUE = "NA";

    private static final Logger LOGGER = LogManager.getLogger(App.class);

    private final InfoRow
        timeRow = new InfoRow("Update time", DEFAULT_INFO_ROW_VALUE),
        priceRow = new InfoRow("Price", DEFAULT_INFO_ROW_VALUE),
        incDecRow = new InfoRow("+/-", DEFAULT_INFO_ROW_VALUE),
        largestQtyRow = new InfoRow("Largest qty.", DEFAULT_INFO_ROW_VALUE),
        largestQtyPriceRow = new InfoRow("Largest qty. price", DEFAULT_INFO_ROW_VALUE),
        largestQtyBuySell = new InfoRow("Largest qty. buy/sell", DEFAULT_INFO_ROW_VALUE);
    private final BlockPushButton startStopButton = new BlockPushButton(
            new BlockPushButton.Profile("Stop", Color.WHITE, Color.web("#d90368")),
            new BlockPushButton.Profile("Start", Color.WHITE, Color.web("#04a777")));
    private final StatusBar statusBar = new StatusBar();
    private final TaskSyncButton exportButton = new TaskSyncButton("Export", Color.WHITE, Color.web("#0353a4"));

    private final SessionTimer sessionTimer = new SessionTimer();
    private WebDriver webDriver;

    private void showError(Stage stage, Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionString = sw.toString();

        TextArea error = new TextArea();
        error.setText(exceptionString);
        VBox.setVgrow(error, Priority.ALWAYS);

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(error);

        var scene = new Scene(mainLayout, 500, 300);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Dahlia - Error");
    }

    private void buildUi(Stage stage) throws Exception {
        ButtonRow buttonRow = new ButtonRow();
        HBox.setMargin(exportButton, new Insets(0, 0, 0, 5));
        buttonRow.getChildren().addAll(startStopButton, exportButton);

        InfoGroup stockprice = new InfoGroup("Stockprice");
        stockprice.getChildren().addAll(timeRow, priceRow, incDecRow, largestQtyRow, largestQtyPriceRow, largestQtyBuySell);

        Pane spacer = new Pane();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(buttonRow, stockprice, spacer, statusBar);
        mainLayout.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        mainLayout.setPadding(new Insets(10, 0, 10, 0));

        var scene = new Scene(mainLayout, 300, 350);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Dahlia");
    }

    private void addActions(Stage stage) throws Exception {


    }

    private void test() throws Exception {
            System.setProperty("webdriver.chrome.driver", "drivers/chromedriver");
            webDriver  = new ChromeDriver();
            webDriver.navigate().to("http://stockprice.vn/a/mix.html");
            Thread.sleep(5000);
            webDriver.navigate().refresh();
            Thread.sleep(5000);
//            webDriver.close();
//            webDriver.quit();

//
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() throws Exception {
//                throw new Exception("ABCXYZ");
//            }
//        })
    }

    @Override
    public void start(Stage stage) {
        stage.setOnCloseRequest(windowEvent -> {
            if (webDriver != null) {
                webDriver.close();
                webDriver.quit();
            }
            System.exit(0);
        });
        try {
            buildUi(stage);
            addActions(stage);
            test();
        } catch (Exception e) {
            showError(stage, e);
            e.getStackTrace();
        } finally {
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}