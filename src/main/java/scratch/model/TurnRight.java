package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class TurnRight implements Actionable{
    private final String name;
    private IntegerProperty angle;

    public TurnRight(String name, int angle) {
        this.name = name;
        this.angle = new SimpleIntegerProperty(angle);
    }

    @Override
    public void execute() {}

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
    public String getPrefix() {
        return "Angle vers la droite ";
    }

    @Override
    public String getSuffix() {
        return " degrés";
    }

    @Override
    public Actionable copy() {
        return new TurnRight(this.name, this.getAngle());
    }

    @Override
    public String toSave() {
        return "TURN_RIGHT;" + getAngle();
    }

    public String toString() {
        return name + " " + getAngle();
    }

    @Override
    public String getName() {
        return name;
    }
}
