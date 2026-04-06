package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class Increment implements Actionable{
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
        var map = state.getVariableMap();
        var nameVariable = getVariableName();
        var incrementParam = getIncrementParam();
        boolean checkIncrement = map.increment(nameVariable, incrementParam);
    }

    public String getVariableName() {return variableName.get();}

    public void setVariableName(String name) {this.variableName.set(name);}

    public String getIncrementParam() {return incrementProperty.get();}

    public void setIncrementParam(String param) {
        incrementProperty.set(param);}

    @Override
    public StringProperty getProperty() {
        return variableName;
    }

    public StringProperty getIncrementProperty() {
        return incrementProperty;
    }

    @Override
    public Actionable copy() {return new Increment(this.actionName, this.getVariableName(), this.getIncrementParam());}

    @Override
    public String toSave() {return "INCREMENT_VARIABLE;" + getVariableName() + ";" + getIncrementParam();}

    @Override
    public Color getColor() {return Color.AQUA.darker();}

    @Override
    public String toString() {return actionName;}
}
