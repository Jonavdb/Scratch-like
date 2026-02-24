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
    public void execute() {

    }

    public int getDistance() {
        return distance.get();
    }

    public IntegerProperty getProperty() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance.set(distance);
    }

    @Override
    public Actionable copy() {
        return new Move(this.name, this.getDistance());
    }

    public String toString() {
        return name + " " + getDistance();
    }
}
