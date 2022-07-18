package lk.ijse.pos.util;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class ValidationUtil {
    public static Object validate(LinkedHashMap<TextField, Pattern> map, JFXButton... btn) {
        for (TextField key : map.keySet()) {
            Pattern pattern = map.get(key);
            if (!pattern.matcher(key.getText()).matches()) {
                addError(key, btn);
                return key;
            }
            removeError(key, btn);
        }
        return true;
    }

    private static void addError(TextField textField, JFXButton... btn) {
        if (textField.getText().length() > 0) {
            /*textField.setStyle("-fx-border-width: 4");
            textField.setStyle("-fx-border-color: red");*/
            textField.setStyle("-fx-text-fill: RED");
        }else{
            textField.setStyle("-fx-text-fill: BLACK");
        }
        for (JFXButton button : btn) {
            button.setDisable(true);
        }
    }

    private static void removeError(TextField textField, JFXButton... btn) {
        /*textField.setStyle("-fx-border-width: 4");
        textField.setStyle("-fx-border-color: green");*/

        textField.setStyle("-fx-text-fill: BLACK");
        for (JFXButton button : btn) {
            button.setDisable(false);
        }
    }
}
