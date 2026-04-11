package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class DeclareVariable implements Actionable {
    private final String actionName;
    private final StringProperty variableName;

    public DeclareVariable(String nameAction, String variableName) {
        this.actionName = nameAction;
        this.variableName = new SimpleStringProperty(variableName);
    }

    @Override
    public void execute(ProgramState state) {
        VariableMap variableStorage = state.getVariableMap();
        String identifier = getVariableName();
        boolean isDeclaredSuccessfully = variableStorage.declare(identifier);
    }

    public String getVariableName() {
        return variableName.get();
    }

    public void setVariableName(String name) {
        this.variableName.set(name);
    }

    @Override
    public StringProperty getProperty() {
        return variableName;
    }

    @Override
    public Actionable copy() {
        return new DeclareVariable(this.actionName, this.getVariableName());
    }

    @Override
    public String toSave() {
        return "VAR_DECLARATION;" + getVariableName();
    }

    @Override
    public Color getColor() {
        return Color.AQUA.darker();
    }

    @Override
    public ActionType getType() {
        return ActionType.DECLARE;
    }

    @Override
    public String toString() {
        return actionName;
    }
}
