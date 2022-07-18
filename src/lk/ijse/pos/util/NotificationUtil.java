package lk.ijse.pos.util;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationUtil {
    public static void setNotifications(String title, String text, ImageView view, int notificationTime) {
        Notifications notificationBuilder = Notifications.create()
                .title(title)
                .text(text)
                .graphic(view)
                .hideAfter(Duration.seconds(notificationTime))
                .position(Pos.CENTER);
        notificationBuilder.darkStyle();
        notificationBuilder.show();
    }

    public static void setNotifications(String title, String text, int notificationTime) {
        Notifications notificationBuilder = Notifications.create()
                .title(title)
                .text(text)
                .hideAfter(Duration.seconds(notificationTime))
                .position(Pos.CENTER);
        notificationBuilder.darkStyle();
        notificationBuilder.show();
    }
}
