package lk.ijse.pos.controller;

import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.pos.db.DBConnection;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperDesignViewer;
import net.sf.jasperreports.view.JasperViewer;
import sun.util.resources.cldr.st.CalendarData_st_LS;

import java.sql.SQLException;

public class MovableItemFormController {
    public AnchorPane context;

    public void mostMovableItemOnAction(ActionEvent event) {
        try {
            loadTheReport("MostMovableItemReport");

        } catch (SQLException | ClassNotFoundException | JRException e) {
            e.printStackTrace();
        }
    }

    public void leastMovableOnAction(ActionEvent event) {
        try {
            loadTheReport("LeastMovableItemReport");

        } catch (SQLException | ClassNotFoundException | JRException e) {
            e.printStackTrace();
        }
    }

    private void loadTheReport(String jasperFile) throws JRException, SQLException, ClassNotFoundException {
        JasperReport compiledReport = (JasperReport) JRLoader.loadObject(getClass().getResource("../view/reports/" + jasperFile + ".jasper"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, null, DBConnection.getInstance().getConnection());
        JasperViewer.viewReport(jasperPrint,false);
    }
}
