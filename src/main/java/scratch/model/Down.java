package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class Down implements Actionable {
    private final String name;

    public Down(String name) {
        this.name = name;
    }

    @Override
    public void execute(Player player) {
        player.setpenDown(true);
    }

    @Override
    public Actionable copy() {
        return new Down(this.name);
    }

    @Override
    public String toSave() {
        return "PEN_DOWN;0";
    }

    @Override
    public StringProperty getProperty() {
        return null;
    }

    @Override
    public String getPrefix() {
        return "Abaisser le stylo";
    }

    @Override
    public String getSuffix() {
        return "";
    }

    @Override
    public Color getColor() {
        return Color.GREEN;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }
}
