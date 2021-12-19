package Game;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animations {
    public static FadeTransition fadeTransition(Node node, double from, double to, double duration, int cycleCount, boolean autoReverse) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(node);
        fadeTransition.setDuration(Duration.millis(duration));
        fadeTransition.setFromValue(from);
        fadeTransition.setToValue(to);
        fadeTransition.setCycleCount(cycleCount);
        fadeTransition.setAutoReverse(autoReverse);
        return fadeTransition;
    }
    public static TranslateTransition translateTransition(Node node, double x, double y, double duration, int cycleCount, boolean autoReverse) {
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setNode(node);
        translateTransition.setByX(x);
        translateTransition.setByY(y);
        translateTransition.setDuration(Duration.millis(duration));
        translateTransition.setCycleCount(cycleCount);
        translateTransition.setAutoReverse(autoReverse);
        return translateTransition;
    }
    public static Timeline delayTransition(double duration)
    {
        return new Timeline(new KeyFrame(Duration.millis(duration), e -> { }));
    }
}
