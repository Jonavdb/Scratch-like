package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Move implements Actionable{
    private final String name;
    private IntegerProperty distance;

    public Move(String name, int distance){
        this.name = name;
        this.distance = new SimpleIntegerProperty(distance);
    }

    @Override
    public void execute(PlayerState state) {
        int dist = this.getProperty().get();
        if(dist <= 0 || dist > 100)
            return;

        // Pour une potentielle ligne
//        double currentAngle = state.getAngle();
//        double currX = state.getX();
//        double currY = state.getY();

//        final double angleOffsetRecalibror = 90;

        // =========================================================================
        // CALCUL DU DÉPLACEMENT (Vecteur de mouvement)
        // =========================================================================
        // On calcule l'angle de déplacement (en radians). On ajoute PI/2 (90 degrés)
        // car l'avant de notre triangle est décalé par rapport au zéro mathématique.
        double radians = Math.toRadians(state.getAngle()) + Math.PI / 2;

        // Trigonométrie pour avancer
        double diffX = dist * Math.cos(radians);  // NouveauX = cosinus(angle) * vitesse
        double diffY = dist * Math.sin(radians);  // NouveauY = sinus(angle) * vitesse

        // L'angle réel sur le cercle (0 à 360)
//        double realAngle = angleOffsetRecalibror + currentAngle;

        // On vérifie si l'angle fait pointer le personnage vers le "haut" de l'écran
//        var normalized = (realAngle % 360 + 360) % 360;
//        boolean facingUp = normalized < 180 && normalized > 1;

//        int directionY = facingUp ? -1 : 1; // L'angle réel sur le cercle (0 à 360)
        state.setPosition(new Point2D(state.getX() + diffX, state.getY() - diffY));
    }

    public int getDistance() {
        return distance.get();
    }

    public IntegerProperty getProperty() {
        return distance;
    }

    @Override
    public String getPrefix() {
        return "Avancer de ";
    }

    @Override
    public String getSuffix() {
        return " pixels";
    }

    public void setDistance(int distance) {
        this.distance.set(distance);
    }

    @Override
    public Actionable copy() {
        return new Move(this.name, this.getDistance());
    }

    @Override
    public String toSave() {
        return "MOVE_FORWARD;" + getDistance();
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }

    public String toString() {
        return name + " " + getDistance();
    }

    public String getName() {
        return name;
    }
}
