package scratch.model;

import javafx.beans.property.IntegerProperty;

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
    public IntegerProperty getProperty() {
        return null;
    }

    public String toString() {
        return name;
    }
}
