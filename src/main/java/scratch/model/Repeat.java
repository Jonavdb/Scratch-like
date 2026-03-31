package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.util.List;

public class Repeat implements Actionable {
    private final String name;
    private final ActionBlock actions = new ActionBlock("repeat");
    private StringProperty tick;

    public Repeat(String name, String tick) {
        this.name = name;
        this.tick = new SimpleStringProperty(tick);
    }
    @Override
    public void execute(ProgramState state) {
//        int n = Integer.parseInt(tick.get());
//        for (int i = 0; i < n; ++i){
//            actions.execute(player);
//        }
    }

    @Override
    public Actionable copy() {
        return new Repeat(this.name, this.getProperty().get());
    }

    public void addChild(Actionable action){
        actions.addAction(action);
    }

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

    public StringProperty tickProperty() {
        return tick;
    }
}
