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
    public IntegerProperty getProperty() {
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
