package scratch.model;

import javafx.beans.property.IntegerProperty;

public interface Actionable {
    void execute();
    Actionable copy();
    String toSave();
    IntegerProperty getProperty();
    String getPrefix();
    String getSuffix();
}
