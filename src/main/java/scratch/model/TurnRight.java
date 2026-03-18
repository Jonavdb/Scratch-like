package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

public class TurnRight extends Turn {
    public TurnRight(String name, String angle) {
        super(name, angle);
    }

    @Override
    public String getPrefix() {
        return "Tourner à droite de ";
    }

    @Override
    public Actionable copy() {
        return new TurnRight(this.getName(), this.getAngle());
    }

    @Override
    public String toSave() {
        return "TURN_RIGHT;" + getAngle();
    }
}
