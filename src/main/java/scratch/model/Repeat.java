package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.util.List;

public class Repeat implements Actionable {
    private final String name;
    private final ActionBlock actions = new ActionBlock("repeat");
    private final StringProperty tick;

    public Repeat(String name, String tick) {
        this.name = name;
        this.tick = new SimpleStringProperty(tick);
    }
    @Override
    public void execute(ProgramState state) {
        String numberOfRepeatText = this.getProperty().get();
        int numberOfRepeat = 0;
        var map = state.getVariableMap();
        boolean isLiteralValue = map.getParamValue(numberOfRepeatText).isPresent();
        numberOfRepeat = isLiteralValue ? Integer.parseInt(numberOfRepeatText) : map.getValue(getTick());
        if(numberOfRepeat <= 0) {
            throw new RuntimeException("Erreur : Impossible de répéter " + numberOfRepeat + " fois (minimum 1 fois).");
        }
    }

    @Override
    public Actionable copy() {
        return new Repeat(this.name, this.getProperty().get());
    }

    /*public void addChild(Actionable action){
        actions.addAction(action);
    }*/

    @Override
    public String toSave() {
        return "REPEAT;" + getTick();
    }

    @Override
    public StringProperty getProperty() {
        return tick;
    }

    @Override
    public String toString() {
        return "Répeter" + tick.getName() ;
    }

    @Override
    public Color getColor() {
        return Color.AQUA.darker();
    }

    public List<Actionable> getChildren(){
        return actions.getChildren();
    }

    public String getTick() {
        return tick.get();
    }

    /*public StringProperty tickProperty() {
        return tick;
    }*/
}
