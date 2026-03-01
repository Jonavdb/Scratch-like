package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Move implements Actionable{
    private final String name;
    private IntegerProperty distance;

    public Move(String name, int distance){
        this.name = name;
        this.distance = new SimpleIntegerProperty(distance);
    }

    @Override
    public void execute() {}

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

    public String toString() {
        return name + " " + getDistance();
    }

    public String getName() {
        return name;
    }
}
