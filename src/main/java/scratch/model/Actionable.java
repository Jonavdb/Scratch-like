package scratch.model;

import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public interface Actionable {
    void execute(ProgramState state);
    Actionable copy();
    String toSave();
    StringProperty getFirstProperty();
    String toString();
    Color getColor();
}
