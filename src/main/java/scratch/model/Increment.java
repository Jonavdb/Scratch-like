package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class Increment implements Actionable{
    private final String actionName;
    private final StringProperty nameVariable;
    private final StringProperty incrementProperty;

    public Increment(String actionName, String initialVarName, String incrementParam) {
        this.actionName = actionName;
        this.nameVariable = new SimpleStringProperty(initialVarName);
        this.incrementProperty = new SimpleStringProperty(incrementParam);
    }

    @Override
    public void execute(Player player) {
        // TODO -> ?
    }

    public String getNameVariable() {return nameVariable.get();}

    public void setSetNameVariable(String name) {this.nameVariable.set(name);}

    public String getIncrementParam() {return incrementProperty.get();}

    public void setIncrementParam(String param) {
        incrementProperty.set(param);}

    @Override
    public StringProperty getFirstProperty() {
        return nameVariable;
    }

    public StringProperty getIncrementProperty() {
        return incrementProperty;
    }

    @Override
    public Actionable copy() {return new Increment(this.actionName, this.getNameVariable(), this.getIncrementParam());}

    @Override
    public String toSave() {return "INCREMENT_VARIABLE;" + getNameVariable() + ";" + getIncrementParam();}

    @Override
    public Color getColor() {return Color.AQUA.darker();}

    @Override
    public String toString() {return actionName;}
}
