package scratch.model;

import javafx.beans.property.IntegerProperty;

public class Down implements Actionable {
    private final String name;

    public Down(String name) {
        this.name = name;
    }

    @Override
    public void execute() {
    }

    @Override
    public Actionable copy() {
        return new Down(this.name);
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
        return "Abaisser le stylo";
    }

    @Override
    public String getSuffix() {
        return "";
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }
}
