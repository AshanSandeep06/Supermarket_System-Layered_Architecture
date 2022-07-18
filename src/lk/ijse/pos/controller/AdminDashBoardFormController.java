package lk.ijse.pos.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.AdminDashBoardBO;
import lk.ijse.pos.bo.custom.impl.AdminDashBoardBOImpl;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dao.custom.impl.CustomerDAOImpl;
import lk.ijse.pos.dao.custom.impl.ItemDAOImpl;
import lk.ijse.pos.dao.custom.impl.OrderDAOImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class AdminDashBoardFormController {
    public Label lblMovableItem;
    public Label lblCustomer;
    public Label lblFoodItem;
    public Button btnFoodItem;
    public Button btnMovableItem;
    public Button btnCustomer;
    public Label lblReports;
    public Button btnReports;
    public Label lblDate;
    public Label lblTime;
    public Label lblTotalOrderLabel;
    public Label lblCustomerLabel;
    public Label lblFoodItemsLabel;
    public AnchorPane optionContext;
    public AnchorPane context;
    ObservableList obList = FXCollections.observableArrayList();

    //Property Injection (DI)
     private final AdminDashBoardBO dashBoardBO = (AdminDashBoardBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.ADMINDASHBOARDBO);

    public void initialize() {
        try {
            loadDateAndTime();
            obList.addAll(lblFoodItem, btnFoodItem, lblCustomer, btnCustomer, lblMovableItem, btnMovableItem, lblReports, btnReports);
            lblTotalOrderLabel.setText(String.valueOf(dashBoardBO.getAllOrdersCount()));
            lblCustomerLabel.setText(String.valueOf(dashBoardBO.getAllCustomersCount()));
            lblFoodItemsLabel.setText(String.valueOf(dashBoardBO.getAllItemsCount()));

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadDateAndTime() {
        lblDate.setText(new SimpleDateFormat("EEEE, MMMM dd, yyyy").format(new Date()));

        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, (ev) -> {
            LocalTime currentTime = LocalTime.now();
            int hour = currentTime.getHour();
            if (hour >= 12) {
                lblTime.setText(currentTime.getHour() + ":" + currentTime.getMinute() + ":" + currentTime.getSecond() + " PM");
            } else {
                lblTime.setText(currentTime.getHour() + ":" + currentTime.getMinute() + ":" + currentTime.getSecond() + " AM");
            }
        }),
                new KeyFrame(Duration.seconds(1)) );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void logOutOnAction(ActionEvent event) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/LoginForm.fxml"))));
        stage.setTitle("Super Market Login");
        stage.show();
    }

    public void foodItemOnAction(ActionEvent event) throws IOException {
        setColors(lblFoodItem, btnFoodItem);
        setUI("ManageItemsForm");
    }

    public void movableItemOnAction(ActionEvent event) throws IOException {
        setColors(lblMovableItem, btnMovableItem);
        setUI("MovableItemForm");
    }

    public void customerOnAction(ActionEvent event) throws IOException {
        setColors(lblCustomer, btnCustomer);
        setUI("ManageCustomerForm");
    }

    public void reportsOnAction(ActionEvent event) throws IOException {
        setColors(lblReports, btnReports);
        setUI("IncomeReportsForm");
    }

    private void setUI(String fxmlFile) throws IOException {
        optionContext.getChildren().clear();
        optionContext.getChildren().add(FXMLLoader.load(getClass().getResource("../view/" + fxmlFile + ".fxml")));
    }

    private void setColors(Label label, Button btn) {
        for (Object ob : obList) {
            if (ob instanceof Label) {
                ((Label) ob).setStyle("-fx-background-color : null");
                if (((Label) ob).getId().equals(label.getId())) {
                    ((Label) ob).setStyle("-fx-background-color : #0097e6");
                }
            } else if (ob instanceof Button) {
                ((Button) ob).setStyle("-fx-background-color : null");
                if (((Button) ob).getId().equals(btn.getId())) {
                    ((Button) ob).setStyle("-fx-background-color : #0097e6");
                }
            }
        }
    }
}
