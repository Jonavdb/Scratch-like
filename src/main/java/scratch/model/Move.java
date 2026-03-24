package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class Move implements Actionable{
    private final String name;
    private final StringProperty distance;

    public Move(String name, String distance){
        this.name = name;
        this.distance = new SimpleStringProperty(distance);
    }

    @Override
    public void execute(Player player) {
        String distanceText = this.getFirstProperty().get();
        int dist = 0;

        try {
            dist = Integer.parseInt(distanceText);
        } catch (NumberFormatException e) {
            // c'est PAS un chiffre, donc c'est le nom d'une variale (ex: 'val', 'nb', ...)
            dist = player.getVariableValue(distanceText);
        }

        if(dist <= 0 || dist > 100) return;

        double radians = Math.toRadians(player.getAngle()) + Math.PI / 2;

        // Trigonométrie pour avancer
        double diffX = dist * Math.cos(radians);  // NouveauX = cosinus(angle) * vitesse
        double diffY = dist * Math.sin(radians);  // NouveauY = sinus(angle) * vitesse

        player.setPosition(new CustomPoint2D(player.getX() + diffX, player.getY() - diffY));
    }

    public String getDistance() {
        return distance.get();
    }

    public void setDistance(String distance) {
        this.distance.set(distance);
    }

    public StringProperty getFirstProperty() {
        return distance;
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
        return name;
    }
}
