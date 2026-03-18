package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public interface Actionable {
    void execute(Player player);
    Actionable copy();
    String toSave();
    StringProperty getProperty();
    String getPrefix();
    String getSuffix();
    String getName();
    Color getColor();
}
