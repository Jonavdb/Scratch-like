package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

public class Move implements Actionable{
    private final String name;
    private IntegerProperty distance;

    public Move(String name, int distance){
        this.name = name;
        this.distance = new SimpleIntegerProperty(distance);
    }

    @Override
    public void execute(Player player) {
        int dist = this.getProperty().get();
        if(dist <= 0 || dist > 100)
            return;

        double radians = Math.toRadians(player.getAngle()) + Math.PI / 2;

        // Trigonométrie pour avancer
        double diffX = dist * Math.cos(radians);  // NouveauX = cosinus(angle) * vitesse
        double diffY = dist * Math.sin(radians);  // NouveauY = sinus(angle) * vitesse

        player.setPosition(new CustomPoint2D(player.getX() + diffX, player.getY() - diffY));
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
