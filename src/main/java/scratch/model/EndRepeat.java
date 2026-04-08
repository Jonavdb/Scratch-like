package scratch.model;

import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class EndRepeat implements Actionable {
    private final String name;
    public EndRepeat(String name) {
        this.name = name;
    }

    @Override
    public void execute(ProgramState state) {

    }

    @Override
    public Actionable copy() {
        return new EndRepeat(this.name);
    }

    @Override
    public String toSave() {
        return "END_REPEAT";
    }

    @Override
    public StringProperty getProperty() {
        return null;
    }

    @Override
    public String toString() {
        return "Fin répéter";
    }

    @Override
    public Color getColor() {
        return Color.AQUA.darker();
    }
}
