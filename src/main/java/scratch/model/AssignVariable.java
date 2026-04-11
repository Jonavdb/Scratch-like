package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class AssignVariable implements Actionable{
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

    public String getVariableName() {return variableName.get();}

    public void setVariableName(String name) {this.variableName.set(name);}

    public String getAssignedParam() {return assignedParam.get();}

    public void setAssignedParam(String param) {
        assignedParam.set(param);}

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
    public String toSave() {return "VAR_ASSIGNMENT;" + getVariableName() + ";" + getAssignedParam();}

    @Override
    public Color getColor() {return Color.AQUA.darker();}

    @Override
    public ActionType getType() {
        return ActionType.ASSIGN;
    }

    @Override
    public String toString() {return actionName;}

}
