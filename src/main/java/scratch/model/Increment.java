package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class Increment implements Actionable{

    private final String nameAction;
    private final StringProperty nameVariable;
    private final StringProperty incrementProperty;

    public Increment(String nameAction, String nameVariable, String incrementParam) {
        this.nameAction = nameAction;
        this.nameVariable = new SimpleStringProperty(nameVariable);
        this.incrementProperty = new SimpleStringProperty(incrementParam);
    }

    @Override
    public void execute(ProgramState state) {
        // TODO -> ?
        var map = state.getVariableMap();
        var nameVariable = getNameVariable();
        var incrementParam = getIncrementParam();
        boolean ok = map.increment(nameVariable, incrementParam);
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
    public Actionable copy() {return new Increment(this.nameAction, this.getNameVariable(), this.getIncrementParam());}

    @Override
    public String toSave() {return "INCREMENT_VARIABLE;" + getNameVariable() + ";" + getIncrementParam();}

    @Override
    public Color getColor() {return Color.AQUA.darker();}

    @Override
    public String toString() {return nameAction;}
}
