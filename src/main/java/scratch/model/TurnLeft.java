package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

public class TurnLeft implements Actionable{
    private final String name;
    private IntegerProperty angle;

    public TurnLeft(String name, int angle) {
        this.name = name;
        this.angle = new SimpleIntegerProperty(angle);
    }

    @Override
    public void execute(PlayerState state) {
        int turnValue = this.getProperty().get();
        state.setAngle(state.getAngle() + turnValue);
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
    public String getPrefix() {
        return "Tourner à gauche de ";
    }

    @Override
    public String getSuffix() {
        return " degrés";
    }

    @Override
    public Actionable copy() {
        return new TurnLeft(this.name, this.getAngle());
    }

    @Override
    public String toSave() {
        return "TURN_LEFT;" + getAngle();
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }

    public String toString() {
        return name + " " + getAngle();
    }

    @Override
    public String getName() {
        return name;
    }
}
