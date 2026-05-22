package scratch.model;

import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.lang.classfile.Label;

//
public interface Actionable {
    void execute(ProgramState state);
    Actionable copy();
    String toSave();
    StringProperty getProperty();
    String toString();
    Color getColor();
    boolean isValid(String... params);
    boolean isFirstValid(String text);
    boolean isSecondValid(String text);
    ActionType getType();
}
