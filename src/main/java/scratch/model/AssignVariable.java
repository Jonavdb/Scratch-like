package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class AssignVariable implements Actionable{
    private final String nameAction;
    private final StringProperty nameVariable;
    private final StringProperty assignedParam;

    public AssignVariable(String nameAction, String nameVariable, String assignedParam) {
        this.nameAction = nameAction;
        this.nameVariable = new SimpleStringProperty(nameVariable);
        this.assignedParam = new SimpleStringProperty(assignedParam);
    }

    @Override
    public void execute(Player player) {
        // TODO -> ?
    }

    public String getNameVariable() {return nameVariable.get();}

    public void setSetNameVariable(String name) {this.nameVariable.set(name);}

    public String getAssignedParam() {return assignedParam.get();}

    public void setAssignedParam(String param) {
        assignedParam.set(param);}

    @Override
    public StringProperty getFirstProperty() {
        return nameVariable;
    }

    public StringProperty getParamProperty() {
        return assignedParam;
    }

    @Override
    public Actionable copy() {return new AssignVariable(this.nameAction, this.getNameVariable(), this.getAssignedParam());}

    @Override
    public String toSave() {return "VAR_ASSIGNMENT;" + getNameVariable() + ";" + getAssignedParam();}

    @Override
    public Color getColor() {return Color.AQUA.darker();}

    @Override
    public String toString() {return nameAction;}

}
