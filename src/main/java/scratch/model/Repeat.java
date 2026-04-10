package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.util.List;

public class Repeat implements Actionable {
    private String regexValidation = "^[a-zA-Z_][a-zA-Z0-9_]*$";

    private final String name;
    private final ActionBlock actions = new ActionBlock("repeat");
    private final StringProperty tick;

    public Repeat(String name, String tick) {
        this.name = name;
        this.tick = new SimpleStringProperty(tick);
    }

    @Override
    public void execute(ProgramState state) {
        String numberOfRepeatText = this.getProperty().get();
        VariableMap map = state.getVariableMap();
        int numberOfRepeat = map.getParamValue(numberOfRepeatText).orElseGet(() -> map.getValue(getTick()));

        if (numberOfRepeat <= 0) {
            throw new RuntimeException("Erreur : Impossible de répéter " + numberOfRepeat + " fois (minimum 1 fois).");
        }
    }

    @Override
    public Actionable copy() {
        return new Repeat(this.name, this.getProperty().get());
    }

    @Override
    public String toSave() {
        return "REPEAT;" + getTick();
    }

    @Override
    public StringProperty getProperty() {
        return tick;
    }

    @Override
    public String toString() {
        return "Répeter" + tick.getName() ;
    }

    @Override
    public Color getColor() {
        return Color.AQUA.darker();
    }

    public List<Actionable> getChildren(){
        return actions.getChildren();
    }

    public String getTick() {
        return tick.get();
    }

    public boolean isValid(String... params) {
        String tickText = params.length > 0 ? params[0] : getTick();
        if (tickText == null || tickText.isEmpty()) return false;

        try {
            int n = Integer.parseInt(tickText);
            return n >= 1;
        } catch (NumberFormatException e) {
            return isValidVariableName(tickText);
        }
    }

    private boolean isValidVariableName(String name) {
        return name != null && name.matches(regexValidation);
    }
}
