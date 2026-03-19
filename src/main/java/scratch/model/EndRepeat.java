package scratch.model;

import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class EndRepeat implements Actionable {
    private final String name;
    public EndRepeat(String name) {
        this.name = name;
    }

    @Override
    public void execute(Player player) {

    }

    @Override
    public Actionable copy() {
        return new EndRepeat(this.getName());
    }

    @Override
    public String toSave() {
        return "";
    }

    @Override
    public StringProperty getProperty() {
        return null;
    }

    @Override
    public String getPrefix() {
        return "Arreter la répétition";
    }

    @Override
    public String getSuffix() {
        return "";
    }

    @Override
    public String getName() {
        return "Arreter la répétition";
    }

    @Override
    public Color getColor() {
        return Color.TURQUOISE;
    }
}
