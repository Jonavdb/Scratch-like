package scratch.model;

import javafx.beans.property.IntegerProperty;

public interface Actionable {

    void execute();
    Actionable copy();
    IntegerProperty getProperty();
}
