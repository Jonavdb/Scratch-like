package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.scene.paint.Color;

public class Up implements Actionable{
    private final String name;

    public Up(String name) {
        this.name = name;
    }

    @Override
    public void execute() {
    }

    @Override
    public Actionable copy() {
        return new Up(this.name);
    }

    @Override
    public String toSave() {
        return "PEN_DOWN;0";
    }

    @Override
    public IntegerProperty getProperty() {
        return null;
    }

    @Override
    public String getPrefix() {
        return "Lever le stylo";
    }

    @Override
    public String getSuffix() {
        return "";
    }

    @Override
    public Color getColor() {
        return Color.GREEN;
    }

    public String toString() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }
}
