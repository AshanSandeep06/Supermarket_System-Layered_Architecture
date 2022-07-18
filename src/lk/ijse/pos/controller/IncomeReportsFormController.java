package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXDatePicker;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.IncomeReportsBO;
import lk.ijse.pos.bo.custom.impl.IncomeReportsBOImpl;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.view.tdm.CustomerTM;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class IncomeReportsFormController {
    public JFXDatePicker dailyReportsDatePicker;
    public ComboBox<String> cmbMonthlyYear;
    public ComboBox<String> cmbMonthlyMonth;
    public ComboBox<String> cmbAnnuallyYear;
    public AnchorPane context;
    public ComboBox<CustomerDTO> cmbCustomerWiseIncome;

    IncomeReportsBO incomeReportsBO = (IncomeReportsBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.INCOMEREPORTSBO);

    public void initialize() {
        loadAllCustomers();
        cmbMonthlyYear.getItems().addAll("2022","2023","2024","2025","2026","2027","2028","2029","2030","2031","2032","2033");
        cmbAnnuallyYear.getItems().addAll("2022","2023","2024","2025","2026","2027","2028","2029","2030","2031","2032","2033");
        cmbMonthlyMonth.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
    }

    private void loadAllCustomers() {
        cmbCustomerWiseIncome.getItems().clear();
        try {
            ArrayList<CustomerDTO> arrayList = incomeReportsBO.getAllCustomers();

            for (CustomerDTO dto : arrayList) {
                cmbCustomerWiseIncome.getItems().add(dto);
            }

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void cusWiseIncomeOnAction(ActionEvent event) {
        try {
            if (cmbCustomerWiseIncome.getValue() != null) {
                HashMap map = new HashMap();
                String customerId = cmbCustomerWiseIncome.getValue().getCustID();
                String customerName = cmbCustomerWiseIncome.getValue().getCustName();
                BigDecimal totalIncome = new BigDecimal(incomeReportsBO.getTotalOrdersCostByCustomerId(customerId)).setScale(2);
                map.put("customerId", customerId);
                map.put("customerName", customerName);
                map.put("totalIncome", totalIncome);
                loadTheReport("CustomerWiseIncomeReport", map, DBConnection.getInstance().getConnection());

            } else {
                new Alert(Alert.AlertType.WARNING, "Please select a customer ID").show();
            }
        } catch (SQLException | ClassNotFoundException | JRException e) {
            e.printStackTrace();
        }
        cmbCustomerWiseIncome.getSelectionModel().clearSelection();
    }

    public void dailyReportsOnAction(ActionEvent event) {
        try {
            if (dailyReportsDatePicker.getValue()!=null) {
                String day = dailyReportsDatePicker.getValue().toString();
                BigDecimal totalIncome = new BigDecimal(incomeReportsBO.getTotalOrdersCostByDay(day)).setScale(2);
                HashMap map = new HashMap();
                map.put("date", day);
                map.put("totalIncome",totalIncome);
                loadTheReport("DailyIncomeReport", map, DBConnection.getInstance().getConnection());
            }else{
                new Alert(Alert.AlertType.ERROR,"Select a Date to get the report..!").show();
            }

        } catch (SQLException | ClassNotFoundException | JRException e) {
            e.printStackTrace();
        }
        dailyReportsDatePicker.setValue(null);
    }

    public void monthlyReportsOnAction(ActionEvent event) {
        try{
            if(cmbMonthlyYear.getValue()!=null && cmbMonthlyMonth.getValue()!=null){
                String year = cmbMonthlyYear.getValue();
                String month = cmbMonthlyMonth.getValue();
                BigDecimal totalIncome = new BigDecimal(incomeReportsBO.getTotalOrdersCostByYearAndMonth(year,month)).setScale(2);
                HashMap map = new HashMap();
                map.put("year",year);
                map.put("month",month);
                map.put("totalIncome",totalIncome);
                loadTheReport("MonthlyIncomeReport", map, DBConnection.getInstance().getConnection());
            }else{
                new Alert(Alert.AlertType.ERROR,"Select the Year & Month and to get the report..!").show();
            }

        }catch (SQLException | ClassNotFoundException | JRException e){
            e.printStackTrace();
        }
        cmbMonthlyYear.getSelectionModel().clearSelection();
        cmbMonthlyMonth.getSelectionModel().clearSelection();
    }

    public void annuallyReportsOnAction(ActionEvent event) {
        try{
            if(cmbAnnuallyYear.getValue()!=null){
                String year = cmbAnnuallyYear.getValue();
                BigDecimal totalIncome = new BigDecimal(incomeReportsBO.getTotalOrdersCostByYear(year)).setScale(2);
                HashMap map = new HashMap();
                map.put("year",year);
                map.put("totalIncome",totalIncome);
                loadTheReport("AnnuallyIncomeReport", map, DBConnection.getInstance().getConnection());
            }else{
                new Alert(Alert.AlertType.ERROR,"Select the Year to get the report..!").show();
            }

        }catch (SQLException | ClassNotFoundException | JRException e){
            e.printStackTrace();
        }
        cmbAnnuallyYear.getSelectionModel().clearSelection();
    }

    private void loadTheReport(String jasperFile, HashMap map, Connection connection) throws JRException, SQLException, ClassNotFoundException {
        JasperReport compiledReport = (JasperReport) JRLoader.loadObject(getClass().getResource("../view/reports/" + jasperFile + ".jasper"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, map, connection);
        JasperViewer.viewReport(jasperPrint, false);
    }
}
