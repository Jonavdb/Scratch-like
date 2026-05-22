package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class Increment implements Actionable {
    private String regexValidation = "^[a-zA-Z_][a-zA-Z0-9_]*$";

    private final String actionName;
    private final StringProperty variableName;
    private final StringProperty incrementProperty;

    public Increment(String actionName, String initialVarName, String incrementParam) {
        this.actionName = actionName;
        this.variableName = new SimpleStringProperty(initialVarName);
        this.incrementProperty = new SimpleStringProperty(incrementParam);
    }

    @Override
    public void execute(ProgramState state) {
        VariableMap map = state.getVariableMap();
        String variableText = getVariableName();
        String variableValue = getIncrementParam();
        boolean isDeclaredSuccessfully = map.increment(variableText, variableValue);
    }

    public String getVariableName() {
        return variableName.get();
    }

    public void setVariableName(String name) {
        this.variableName.set(name);
    }

    public String getIncrementParam() {
        return incrementProperty.get();
    }

    public void setIncrementParam(String param) {
        incrementProperty.set(param);
    }

    @Override
    public StringProperty getProperty() {
        return variableName;
    }

    public StringProperty getIncrementProperty() {
        return incrementProperty;
    }

    @Override
    public Actionable copy() {
        return new Increment(this.actionName, this.getVariableName(), this.getIncrementParam());
    }

    @Override
    public String toSave() {
        return "INCREMENT_VARIABLE;" + getVariableName() + ";" + getIncrementParam();
    }

    @Override
    public Color getColor() {
        return Color.AQUA.darker();
    }

    @Override
    public ActionType getType() {
        return ActionType.INCREMENT;
    }

    public boolean isFirstValid(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        if (!text.matches(regexValidation)) {
            return false;
        }

        return true;
    }


    public boolean isSecondValid(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isValid(String... params) {
        // We expect exactly 2 parameters (variableName and incrementationValue)
        if (params.length != 2) {
            return false;
        }

        // Delegate validation to the dedicated methods
        boolean firstValid = isFirstValid(params[0]);
        boolean secondValid = isSecondValid(params[1]);

        // Both parameters must pass validation for the whole action to be valid
        return firstValid && secondValid;
    }
    @Override
    public String toString() {
        return actionName;
    }
}
