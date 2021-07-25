module com.tmh.dahlia {
    requires javafx.controls;

    requires javafx.fxml;

    requires selenium.api;
    requires selenium.support;
    requires selenium.chrome.driver;
    requires org.apache.logging.log4j;

    requires TrayNotification.a8bfa597eb;
	
	
    exports com.tmh.dahlia;
}