package com.tmh.dahlia;

import com.tmh.dahlia.ui.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application {

    private final static String DEFAULT_INFO_ROW_VALUE = "no_value";

    private final InfoRow
        timeRow = new InfoRow("Time", DEFAULT_INFO_ROW_VALUE),
        priceRow = new InfoRow("Price", DEFAULT_INFO_ROW_VALUE),
        incDecRow = new InfoRow("+/-", DEFAULT_INFO_ROW_VALUE),
        largestQtyRow = new InfoRow("Largest qty.", DEFAULT_INFO_ROW_VALUE),
        largestQtyPriceRow = new InfoRow("Largest qty. price", DEFAULT_INFO_ROW_VALUE),
        largestQtyBuySell = new InfoRow("Largest qty. buy/sell", DEFAULT_INFO_ROW_VALUE);

        @Override
    public void start(Stage stage) {

        ButtonRow buttonRow = new ButtonRow();
        BlockPushButton startStopButton = new BlockPushButton(
                new BlockPushButton.State("Start", Color.WHITE, Color.web("#04a777")),
                new BlockPushButton.State("Stop", Color.WHITE, Color.web("#d90368")));
        TaskSyncButton exportButton = new TaskSyncButton("Export", Color.WHITE, Color.web("#0353a4"));
        HBox.setMargin(exportButton, new Insets(0, 0, 0, 5));
        buttonRow.getChildren().addAll(startStopButton, exportButton);

        InfoGroup stockprice = new InfoGroup("Stockprice");
        stockprice.getChildren().addAll(timeRow, priceRow, incDecRow, largestQtyRow, largestQtyPriceRow, largestQtyBuySell);

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(buttonRow, stockprice);
        mainLayout.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        mainLayout.setPadding(new Insets(10, 0, 10, 0));

        var scene = new Scene(mainLayout, 300, 350);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Dahlia");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}