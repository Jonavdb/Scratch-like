package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class DeclareVariable implements Actionable {
    private final String name;
    private final StringProperty variableName;

    public DeclareVariable(String nameAction, String initialVarName) {
        this.name = nameAction;
        this.variableName = new SimpleStringProperty(initialVarName); // stocke le nom de la variable tapée par l'user
    }

    @Override
    public void execute(Player player) {
        player.declareVariable(this.variableName.get());
    }

    public String getVariableName() {return variableName.get();}

    public void setSetNameVariable(String name) {this.variableName.set(name);}

    @Override
    public StringProperty getProperty() {
        return variableName;
    }

    @Override
    public Actionable copy() {return new DeclareVariable(this.name, this.getVariableName());}

    @Override
    public String toSave() {return "VAR_DECLARATION;" + getVariableName();}

    @Override
    public Color getColor() {return Color.AQUA.darker();}

    @Override
    public String toString() {return name;}

}
