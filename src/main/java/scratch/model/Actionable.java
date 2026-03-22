package scratch.model;

import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public interface Actionable {
    void execute(Player player);
    Actionable copy();
    String toSave();
    StringProperty getProperty();
    String getName();
    Color getColor();
}
