package scratch.model;

import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class Down implements Actionable {
    private final String name;

    public Down(String name) {
        this.name = name;
    }

    @Override
    public void execute(ProgramState state) {
        var player = state.getPlayer();
        player.setpenDown(true);
    }

    @Override
    public Actionable copy() {
        return new Down(this.name);
    }

    @Override
    public String toSave() {
        return "PEN_DOWN;";
    }

    @Override
    public StringProperty getProperty() {
        return null;
    }

    @Override
    public Color getColor() {
        return Color.GREEN;
    }

    @Override
    public ActionType getType() {
        return ActionType.DOWN;
    }

    @Override
    public boolean isValid(String... params) {
        // action sans paramètre donc pas de validation métier
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
