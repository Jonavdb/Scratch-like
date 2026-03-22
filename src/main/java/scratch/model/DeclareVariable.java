package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class DeclareVariable implements Actionable {
    private final String name;
    private final StringProperty nameVariable;

    public DeclareVariable(String nameAction, String nameVariable) {
        this.name = nameAction;
        this.nameVariable = new SimpleStringProperty(nameVariable);
    }


    @Override
    public void execute(Player player) {
        // TODO -> ?
    }

    public String getNameVariable() {return nameVariable.get();}

    public void setSetNameVariable(String name) {this.nameVariable.set(name);}

    @Override
    public StringProperty getProperty() {
        return nameVariable;
    }

    @Override
    public Actionable copy() {return new DeclareVariable(this.name, this.getNameVariable());}

    @Override
    public String toSave() {return "VAR_DECLARATION;" + getNameVariable();}

    @Override
    public Color getColor() {return Color.AQUA.darker();}

    public String toString() {return name;}

    @Override
    public String getName() {return name;}
}
