package com.tmh.dahlia;

import com.tmh.dahlia.ui.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
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

        InfoGroup stockprice = new InfoGroup("Stockprice");
        stockprice.getChildren().addAll(timeRow, priceRow, incDecRow, largestQtyRow, largestQtyPriceRow, largestQtyBuySell);

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(stockprice);
        mainLayout.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        var scene = new Scene(mainLayout, 300, 300);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}