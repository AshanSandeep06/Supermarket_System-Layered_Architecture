package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXProgressBar;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomePageFormController {
    public JFXProgressBar progressBar;
    public ProgressIndicator progressPercentage;
    public ImageView imgCart;
    public AnchorPane context;

    ScaleTransition scaleTransition = new ScaleTransition();

    public void initialize() {
        cartAnimation(imgCart);
        new screenNavigate().start();
    }

    private void cartAnimation(ImageView imgCart) {
        scaleTransition.setNode(imgCart);
        scaleTransition.setDuration(Duration.millis(600));
        scaleTransition.setCycleCount(TranslateTransition.INDEFINITE);
        scaleTransition.setInterpolator(Interpolator.LINEAR);
        scaleTransition.setByX(1.0);
        scaleTransition.setByY(1.0);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }

    class screenNavigate extends Thread {
        @Override
        public void run() {
            try {
                for (int i = 0; i < 11; i++) {
                    double d = i * (0.1);
                    progressBar.setProgress(d);
                    progressPercentage.setProgress(d);
                    //progressPercentage.setStyle("-fx-background-color: WHITE");

                    /*if (i * 10 == 100) {
                        progressPercentage.setVisible(true);
                        progressPercentage.setProgress(1);
                    }*/

                    Thread.sleep(250);
                }

               Platform.runLater(() -> {
                    Stage stage = (Stage) context.getScene().getWindow();
                    stage.setTitle("Super Market Login");
                    try {
                        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/LoginForm.fxml"))));
                        stage.show();
                    } catch (IOException ex) {
                        //Logger.getLogger(HomePageFormController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            } catch (Exception e) {

            }
        }
    }
}
