package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.util.List;

public class Repeat implements Actionable {
    private final String name;
    private final ActionBlock actions = new ActionBlock("repeat");
    private final StringProperty tick = new SimpleStringProperty("10");
    public Repeat(String name) {
        this.name = name;
    }
    @Override
    public void execute(Player player) {
        int n = Integer.parseInt(tick.get());
        for (int i = 0; i < n; ++i){
            actions.execute(player);
        }
    }

    @Override
    public Actionable copy() {
        return new Repeat(this.getName());
    }

    public void addChild(Actionable action){
        actions.addAction(action);
    }

    @Override
    public String toSave() {
        return "";
    }

    @Override
    public StringProperty getProperty() {
        return tick;
    }

    @Override
    public String getPrefix() {
        return "Répeter";
    }

    @Override
    public String getSuffix() {
        return "fois";
    }

    @Override
    public String getName() {
        return "répeter " + tick.getName() ;
    }

    @Override
    public Color getColor() {
        return Color.TURQUOISE;
    }

    public List<Actionable> getChildren(){
        return actions.getChildren();
    }
}
