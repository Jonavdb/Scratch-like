package scratch.model;

import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class Up implements Actionable{
    private final String name;

    public Up(String name) {
        this.name = name;
    }

    @Override
    public void execute(ProgramState state) {
        var player = state.getPlayer();
        if (!player.isPenDown()){
            throw new RuntimeException("Le stylo est déjà levé !");
        }
        player.setPenDown(false);
    }

    @Override
    public Actionable copy() {
        return new Up(this.name);
    }

    @Override
    public String toSave() {
        return "PEN_UP;0";
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
    public boolean isValid(String... params) {
        // action sans paramètre donc pas de validation métier
        return true;
    }

    // inutile ici
    @Override
    public boolean isFirstValid(String text) {
        return false;
    }

    // inutile ici
    @Override
    public boolean isSecondValid(String text) {
        return false;
    }

    @Override
    public ActionType getType() {
        return ActionType.UP;
    }

    public String toString() {
        return name;
    }
}
