package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class AssignVariable implements Actionable{
    private final String nameAction;
    private final StringProperty nameVariable;
    private final StringProperty valueVariable;

    public AssignVariable(String nameAction, String nameVariable, String valueVariable) {
        this.nameAction = nameAction;
        this.nameVariable= new SimpleStringProperty(nameVariable);
        this.valueVariable = new SimpleStringProperty(valueVariable);
    }

    @Override
    public void execute(Player player) {
        // TODO -> ?
    }

    public String getNameVariable() {return nameVariable.get();}

    public void setSetNameVariable(String name) {this.nameVariable.set(name);}

    public String getValueVariable() {return valueVariable.get();}

    public void setValueVariable(String value) {valueVariable.set(value);}

    @Override
    public StringProperty getFirstProperty() {
        return nameVariable;
    }

    public StringProperty getValueProperty() {
        return valueVariable;
    }

    @Override
    public Actionable copy() {return new DeclareVariable(this.nameAction, this.getNameVariable());}

    @Override
    public String toSave() {return "VAR_ASSIGNMENT;" + getNameVariable() + ";" + getValueVariable();}

    @Override
    public Color getColor() {return Color.AQUA.darker();}

    public String toString() {return nameAction;}

}
