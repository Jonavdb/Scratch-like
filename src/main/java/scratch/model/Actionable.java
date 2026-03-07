package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.scene.paint.Color;

public interface Actionable {
    void execute(Player player);
    Actionable copy();
    String toSave();
    IntegerProperty getProperty();
    String getPrefix();
    String getSuffix();
    String getName();
    Color getColor();
}
