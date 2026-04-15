package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.util.List;

public class Repeat extends ActionBlock implements Actionable {
    private String regexValidation = "^[a-zA-Z_][a-zA-Z0-9_]*$";
    private final StringProperty tick;

    public Repeat(String name, String tick) {
        super(name);
        this.tick = new SimpleStringProperty(tick);
    }

    @Override
    public void execute(ProgramState state) {
        String numberOfRepeatText = this.getProperty().get();
        VariableMap map = state.getVariableMap();
        int numberOfRepeat = map.getParamValue(numberOfRepeatText).orElseGet(() -> map.getValue(getTick()));

        if(numberOfRepeat <= 0) {
            throw new RuntimeException("Erreur : Impossible de répéter " + numberOfRepeat + " fois (minimum 1 fois).");
        }
    }

    @Override
    protected ActionBlock createEmptyCopy() {
        return new Repeat(this.getBlockName(), this.getTick());
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
        return "Répeter" + tick.getName();
    }

    @Override
    public Color getColor() {
        return Color.AQUA.darker();
    }

    @Override
    public ActionType getType() {
        return ActionType.REPEAT;
    }

    public String getTick() {
        return tick.get();
    }

    public boolean isValid(String... params) {
        String tickText = params.length > 0 ? params[0] : getTick();
        boolean isTickValid = false;

        if(tickText != null && !tickText.isEmpty()) {
            try {
                int n = Integer.parseInt(tickText);
                isTickValid = (n >= 1);
            } catch(NumberFormatException e) {
                isTickValid = isValidVariableName(tickText);
            }
        }

        return isTickValid && super.isValid(params); // super.isValid va vérifier chaque enfant du block repeat
    }

    private boolean isValidVariableName(String name) {
        return name != null && name.matches(regexValidation);
    }
}