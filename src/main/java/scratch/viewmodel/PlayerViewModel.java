package scratch.viewmodel;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scratch.model.Player;
import scratch.model.CustomPoint2D;

public class PlayerViewModel {
    private final Player player;

    private final ObservableList<Double> bodyPoints = FXCollections.observableArrayList();
    private final DoubleProperty headX = new SimpleDoubleProperty(0);
    private final DoubleProperty headY = new SimpleDoubleProperty(0);


    public PlayerViewModel(Player player) {
        this.player = player;

        // Initialisation de la liste des points du joueur ie.triangle (x1, y1, x2, y2, x3, y3)
        for (int i = 0; i < 6; ++i) bodyPoints.add(0.0);

        // Mise à jour des points quand le joueur bouge ou tourne
        InvalidationListener listener = (obs) ->  updatePoints();
        player.positionProperty().addListener(listener);
        player.angleProperty().addListener(listener);

        updatePoints();
    }

    public void updatePoints() {
        double currentX = player.getX();
        double currentY = player.getY();
        double currentAngle = player.getAngle();
        double size = player.getSize();
        double angleRecalibrer = 90;

        double xP1 = currentX + size * Math.sin(Math.toRadians(60)) * Math.cos(Math.toRadians(currentAngle + angleRecalibrer));
        double yP1 = currentY - size * Math.sin(Math.toRadians(60)) * Math.sin(Math.toRadians(currentAngle + angleRecalibrer));
        double xP2 = currentX - size * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(currentAngle));
        double yP2 = currentY + size * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(currentAngle));
        double xP3 = currentX + size * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(currentAngle));
        double yP3 = currentY - size * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(currentAngle));

        bodyPoints.setAll(xP1, yP1, xP2, yP2, xP3, yP3);
        headX.set(xP1);
        headY.set(yP1);
    }

    public ObservableList<Double> getBodyPoints() {
        return bodyPoints;
    }

    public ReadOnlyDoubleProperty headXProperty() {return headX;}
    public ReadOnlyDoubleProperty headYProperty() {return headY;}

    public ReadOnlyObjectProperty<CustomPoint2D> positionProperty() {return player.positionProperty();}
    public ReadOnlyBooleanProperty penDowProperty() { return player.penDownProperty();}
}
