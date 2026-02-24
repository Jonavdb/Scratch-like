package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Turn implements Actionable{

    private final String name;
    private IntegerProperty angle;

    public Turn(String name, int angle) {
        this.name = name;
        this.angle = new SimpleIntegerProperty(angle);
    }

    @Override
    public void execute() {

    }

    public int getAngle(){
        return angle.get();
    }

    public void setAngle(int angle) {
        this.angle.set(angle);
    }

    public IntegerProperty getProperty() {
        return angle;
    }

    @Override
    public Actionable copy() {
        return new Turn(this.name, this.getAngle());
    }

    public String toString() {
        return name + " " + getAngle();
    }
}
