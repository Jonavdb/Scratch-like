package scratch.model;

import javafx.beans.property.IntegerProperty;

public interface Actionable {

    void execute();
    Actionable copy();
    String ToSave();
    IntegerProperty getProperty();
}
