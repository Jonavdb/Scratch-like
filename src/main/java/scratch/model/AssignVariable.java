package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class AssignVariable implements Actionable {
    private String regexValidation = "^[a-zA-Z_][a-zA-Z0-9_]*$";

    private final String actionName;
    private final StringProperty variableName;
    private final StringProperty assignedParam;

    public AssignVariable(String nameAction, String initialVarName, String assignedParam) {
        this.actionName = nameAction;
        this.variableName = new SimpleStringProperty(initialVarName);
        this.assignedParam = new SimpleStringProperty(assignedParam);
    }

    @Override
    public void execute(ProgramState state) {
        VariableMap map = state.getVariableMap();
        String variableText = getVariableName();
        String variableValue = getAssignedParam();
        boolean isDeclaredSuccessfully = map.assign(variableText, variableValue);
    }

    public String getVariableName() {
        return variableName.get();
    }

    public void setVariableName(String name) {
        this.variableName.set(name);
    }

    public String getAssignedParam() {
        return assignedParam.get();
    }

    public void setAssignedParam(String param) {
        assignedParam.set(param);
    }

    @Override
    public StringProperty getProperty() {
        return variableName;
    }

    public StringProperty getParamProperty() {
        return assignedParam;
    }

    @Override
    public Actionable copy() {
        return new AssignVariable(this.actionName, this.getVariableName(), this.getAssignedParam());
    }

    @Override
    public String toSave() {
        return "VAR_ASSIGNMENT;" + getVariableName() + ";" + getAssignedParam();
    }

    @Override
    public Color getColor() {
        return Color.AQUA.darker();
    }

    @Override
    public boolean isValid(String... params) {
        // pour le moment on suppose qu'on aura toujours que 2 paramètres
        if(params.length != 2) return false;

        String variableName = params[0];
        String variableValue = params[1];

        if(variableName == null || variableName.isEmpty()) {
            return false;
        }

        if(!variableName.matches(regexValidation)) {
            return false;
        }

        if(variableValue == null) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return actionName;
    }
}
