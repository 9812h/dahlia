package com.tmh.dahlia;

import com.tmh.dahlia.ui.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;

public class App extends Application {

    private final static String DEFAULT_INFO_ROW_VALUE = "NA";

    private final ArrayList<ArrayList<Object>> data = new ArrayList<>();

    private final InfoRow
        timeRow = new InfoRow("Time", DEFAULT_INFO_ROW_VALUE),
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

    @Override
    public void start(Stage stage) {
        System.setProperty("webdriver.chrome.driver", "C:/Users/tmh/Downloads/chromedriver_win32/chromedriver.exe");
        buildUi(stage);
        addActions();
    }

    public static void main(String[] args) {
        launch();
    }

    private void buildUi(Stage stage) {
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
        stage.show();
    }

    private void addActions() {

        exportButton.disable();

        startStopButton.addEventListener(BlockPushButton.EventType.ON, () -> {
            statusBar.setContent("ON");
            sessionTimer.start();
            exportButton.enable();
        });

        startStopButton.addEventListener(BlockPushButton.EventType.OFF, () -> {
            statusBar.setContent("OFF");
            sessionTimer.stop();
            exportButton.disable();
        });

        sessionTimer.addEventListener(SessionTimer.EventType.TIMER_STARTED, () -> {
            webDriver = new ChromeDriver();
            webDriver.get("http://stockprice.vn/a/mix.html");
        });

        sessionTimer.addEventListener(SessionTimer.EventType.TIMER_STOPPED, new SessionTimer.EventListener() {
            @Override
            public void work() {
                webDriver.close();
            }
        });
    }

}