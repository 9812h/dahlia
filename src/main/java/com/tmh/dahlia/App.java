package com.tmh.dahlia;

import com.github.plushaze.traynotification.animations.Animations;
import com.github.plushaze.traynotification.notification.Notifications;
import com.github.plushaze.traynotification.notification.TrayNotification;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class App extends Application {
    private static long SCENE_HEIGHT = 300;
    private static long SCENE_WIDTH  = 450;

    private static int DEFAULT_START_NUM = 0;

    private static final Logger LOGGER = LogManager.getLogger(App.class);

    private SessionTimer sessionTimer;
    private WebDriver webDriver;

    private enum SettingKeys {
        DRIVER_PATH,
        UPDATE_TIME
    }

    private HashMap<SettingKeys, Object> settings;

    private Scene mainScene;

    private final ArrayList<RowData> sessionData = new ArrayList<>();
    private RowData largestQttyData = null;

    private boolean periodCheckFlag = true;
    private int latestNum = DEFAULT_START_NUM;

    @Override
    public void start(Stage stage) {
        try {
            stage.setOnCloseRequest(windowEvent -> System.exit(0));

            readSettings();

            System.setProperty("webdriver.chrome.driver", (String) settings.get(SettingKeys.DRIVER_PATH));

            SessionTimer.EventListener periodicListener = () -> {
                if (periodCheckFlag) periodCheckFlag = false; else return;
                try {
                    LocalDateTime start = LocalDateTime.now();
                    LOGGER.debug("periodic check");
                    webDriver.findElement(By.xpath("//div[@data-qtip='Câp nhật']")).click();

                    new WebDriverWait(webDriver, 100)
                            .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[contains(@class,'SPTable') and contains(@id,'tblDealHist')]")));

                    String historyTbXpath = "//table[contains(@class,'SPTable') and contains(@id,'tblDealHist')]";
                    int currentNum = Integer.parseInt(webDriver.findElement(By.xpath(historyTbXpath + "//tr[1]//td[1]")).getText());

                    LOGGER.debug("New row(s): " + (currentNum - latestNum));
                    boolean foundLargestQtty = false;
                    for (int i = 1; i <= currentNum - latestNum; ++i) {
                        String baseXpath = historyTbXpath + "//tr[" + i + "]";
                        Integer num = Integer.parseInt(webDriver.findElement(By.xpath(baseXpath + "//td[1]")).getText());
                        String time = webDriver.findElement(By.xpath(baseXpath + "//td[2]")).getText();
                        Float price = Float.parseFloat(webDriver.findElement(By.xpath(baseXpath + "//td[3]")).getText()
                                .replace(".", "").replace(",", "."));
                        Float change = Float.parseFloat(webDriver.findElement(By.xpath(baseXpath + "//td[4]")).getText()
                                .replace(".", "").replace(",", "."));
                        Float qtty = Float.parseFloat(webDriver.findElement(By.xpath(baseXpath + "//td[5]")).getText()
                                .replace(".", "").replace(",", "."));
                        String BorS = webDriver.findElement(By.xpath(baseXpath + "//td[6]")).getText();
                        RowData currentRowData = new RowData(num, time, price, change, qtty, BorS);
                        sessionData.add(currentRowData);

                        if (num > 3) {
                            if (largestQttyData == null) {
                                largestQttyData = currentRowData;
                                foundLargestQtty = true;
                            } else {
                                if (qtty >= largestQttyData.getQtty()) {
                                    largestQttyData = currentRowData;
                                    foundLargestQtty = true;
                                }
                            }
                        }

                        LOGGER.debug("currentRowData:");
                        LOGGER.debug(currentRowData);
                        LOGGER.debug("largestQttyData:");
                        LOGGER.debug(largestQttyData);
                        LOGGER.debug("foundLargestQtty:");
                        LOGGER.debug(foundLargestQtty);
                    }
                    latestNum = currentNum;

                    if (foundLargestQtty) {
                        Platform.runLater(() -> {
                            TrayNotification notification = new TrayNotification(
                                    "Largest qtty: " + largestQttyData.getQtty(),
                                    largestQttyData.getNotifString(),
                                    Notifications.NOTICE);
                            notification.setAnimation(Animations.POPUP);
                            notification.showAndWait();
                            //                        TODO: update UI
                        });

                    }
                    LocalDateTime finish = LocalDateTime.now();
                    LOGGER.debug("-> " + start.until(finish, ChronoUnit.SECONDS) + "s");
                } catch (Exception e) {
                    LOGGER.debug(ExceptionUtils.getStackTrace(e));
                } finally {
                    periodCheckFlag = true;
                }
            };

            sessionTimer = new SessionTimer((Long) settings.get(SettingKeys.UPDATE_TIME));

            sessionTimer.addEventListener(SessionTimer.EventType.SESSION_START, () -> {
                sessionData.clear();
                largestQttyData = null;
            });

            sessionTimer.addEventListener(SessionTimer.EventType.HALF_START, () -> {
                try {
                    ChromeOptions options = new ChromeOptions();
                    options.setHeadless(true);
                    webDriver = new ChromeDriver(options);

                    //                webDriver.manage().window().maximize();

                    //                TODO Show info in status bar
                    webDriver.navigate().to("https://quotes.vcbs.com.vn/a/mix.html");

                    (new WebDriverWait(webDriver, 100)
                            .until(ExpectedConditions.elementToBeClickable(By.id("combobox-1009-inputEl"))))
                            .sendKeys("VN30F1M");

                    new WebDriverWait(webDriver, 100)
                            .until((ExpectedCondition<Boolean>) driver -> {
                                WebElement button1015 = driver.findElement(By.id("button-1011"));
                                String elementClasses = button1015.getAttribute("class");
                                return !elementClasses.contains("x-item-disabled") && !elementClasses.contains("x-btn-disabled");
                            });


                    webDriver.findElement(By.id("btnDetail")).click();

                    new WebDriverWait(webDriver, 100)
                            .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@data-ref='textEl']")));

                    WebElement tb = webDriver.findElement(By.xpath("//div[contains(@class, 'x-window x-layer')]"));

                    //                WebElement tr = tb.findElement(By.tagName("tr"));
                    //                LOGGER.debug(tr.getText());

                    sessionTimer.addEventListener(SessionTimer.EventType.HALF_PERIODIC_CHECK, periodicListener);
                } catch (Exception e) {
                    LOGGER.debug(ExceptionUtils.getStackTrace(e));
                }
            });

            sessionTimer.addEventListener(SessionTimer.EventType.HALF_FINISH, () -> {
                sessionTimer.removeEventListener(SessionTimer.EventType.HALF_PERIODIC_CHECK, periodicListener);
                webDriver.close();
                webDriver.quit();
            });

            sessionTimer.addEventListener(SessionTimer.EventType.TIMER_STOPPED, () -> {
                sessionTimer.removeEventListener(SessionTimer.EventType.HALF_PERIODIC_CHECK, periodicListener);
                try {
                    webDriver.close();
                    webDriver.quit();
                } catch (Exception ignored) {}
            });

            sessionTimer.addEventListener(SessionTimer.EventType.SESSION_START, () -> {
                latestNum = DEFAULT_START_NUM;
            });

            sessionTimer.addEventListener(SessionTimer.EventType.SESSION_FINISH, () -> {
                latestNum = DEFAULT_START_NUM;
            });

            sessionTimer.addEventListener(SessionTimer.EventType.HALF_PERIODIC_CHECK, () -> {

            });

            buildUi(stage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }

    private void buildUi(Stage stage) {
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
            settings.put(SettingKeys.UPDATE_TIME, Long.valueOf(updateTime));
            scanner.close();
        } catch (Exception e) {
            settings.put(SettingKeys.DRIVER_PATH, "");
            settings.put(SettingKeys.UPDATE_TIME, 5000);
        }
    }

    private void updateSettings(String path, Long time) throws Exception {
        if (!path.equals((String) settings.get(SettingKeys.DRIVER_PATH))) {
            settings.put(SettingKeys.DRIVER_PATH, path);
            System.setProperty("webdriver.chrome.driver", (String) settings.get(SettingKeys.DRIVER_PATH));
        }
        if (!time.equals((Long) settings.put(SettingKeys.UPDATE_TIME, time))) {
            settings.put(SettingKeys.UPDATE_TIME, time);
            sessionTimer.updatePeriodicTimerDelay(time);
        }

        try {
            FileWriter writer = new FileWriter("settings");
            writer.write(settings.get(SettingKeys.DRIVER_PATH) + "\n");
            writer.write(((Long) settings.get(SettingKeys.UPDATE_TIME)).toString());
            writer.close();
        } catch (Exception e) {
            LOGGER.debug("Error on writing settings file!");
            throw e;
        }
    }

    private Scene generateMainScene(Stage stage) {
//        TrayNotification notification = new TrayNotification(
//                "Largest qtty",
//                "",
//                Notifications.INFORMATION);
//        notification.setAnimation(Animations.POPUP);
//        notification.showAndWait();

        HBox topControls = new HBox();

        TwoStateButton buttonStartStop = new TwoStateButton("Start", TwoStateButton.State.STATE_1);
        Button buttonExport = new Button("Export");
        buttonExport.setDisable(true);
        Button buttonSettings = new Button("Settings");

        buttonStartStop.setMaxWidth(Long.MAX_VALUE);
        HBox.setHgrow(buttonStartStop, Priority.ALWAYS);
        buttonStartStop.setOnChangeToState1(() -> {
////        TODO check settings

            buttonExport.setDisable(true);
            buttonSettings.setDisable(false);

            Platform.runLater(() -> {buttonStartStop.setText("Start");});

            sessionTimer.stop();

        });

        buttonStartStop.setOnChangeToState2(() -> {
            sessionTimer.start();

            buttonExport.setDisable(false);
            buttonSettings.setDisable(true);

            Platform.runLater(() -> {buttonStartStop.setText("Stop");});
        });


        buttonSettings.setOnMouseClicked((Event) -> {
            stage.setScene(generateSettingsScene(stage));
        });

        topControls.getChildren().addAll(buttonStartStop, buttonExport, buttonSettings);

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(topControls);

        return new Scene(mainLayout, SCENE_WIDTH, SCENE_HEIGHT);
    }

    private Scene generateSettingsScene(Stage stage) {

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
        textFieldUpdateTime.setText(((Long) settings.get(SettingKeys.UPDATE_TIME)).toString());

//        TwoStateButton showHideBrowser =

        Node driverPathRow = generateSettingRow("Chrome driver", new Node[]{labelDriverPath});
        Node browseDriverRow = generateSettingRow(null, new Node[]{buttonBrowseDriver});
        Node updateTimeRow = generateSettingRow("Update time (millisec)", new Node[]{textFieldUpdateTime});
//        Node Show  = generateSettingRow("Hide browser window", new Node[]{textFieldUpdateTime});

        Pane spacer = new Pane();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox actionRow = new HBox();
        Button buttonCancel = new Button("Cancel");
        buttonCancel.setOnMouseClicked((Event) -> {stage.setScene(mainScene);});
        Button buttonOK = new Button("OK");
        buttonOK.setOnMouseClicked((Event) -> {
            try {
                updateSettings(labelDriverPath.getText(), Long.valueOf(textFieldUpdateTime.getText()));
                stage.setScene(mainScene);
            } catch (Exception e) {
                LOGGER.debug("Error on saving settings");
                e.printStackTrace();
            }
        });
        actionRow.getChildren().addAll(buttonCancel, buttonOK);

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(driverPathRow, browseDriverRow, updateTimeRow, spacer, actionRow);
        return new Scene(mainLayout, SCENE_WIDTH, SCENE_HEIGHT);
    }

    private Node generateSettingRow(String text, Node[] controls) {
        HBox row = new HBox();
        if (text != null) {
            Label label = new Label(text);
            row.getChildren().add(label);
        }
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().add(spacer);
        for (Node control : controls) { row.getChildren().add(control); }
        return row;
    }

}