package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class Move implements Actionable {
    private String regexValidation = "^[a-zA-Z_][a-zA-Z0-9_]*$";

    private final String name;
    private final StringProperty distance;

    public Move(String name, String distance) {
        this.name = name;
        this.distance = new SimpleStringProperty(distance);
    }

    @Override
    public void execute(ProgramState state) {
        String distanceText = getDistance();
        VariableMap map = state.getVariableMap();
        int dist = map.getParamValue(distanceText).orElseGet(() -> map.getValue(distanceText));

        if(dist <= 0 || dist > 100) {
            throw new RuntimeException("Erreur : Impossible d'avancer de " + dist + " (limite : 1 à 100).");
        }

        state.getPlayer().moveForward(dist);
    }

    public String getDistance() {
        return distance.get();
    }

    public void setDistance(String distance) {
        this.distance.set(distance);
    }

    public StringProperty getProperty() {
        return distance;
    }

    @Override
    public Actionable copy() {
        return new Move(this.name, this.distance.get());
    }

    @Override
    public String toSave() {
        return "MOVE_FORWARD;" + getDistance();
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }

    @Override
    public boolean isValid(String... params) {
        String distText = params.length > 0 ? params[0] : getDistance();

        if(distText == null || distText.trim().isEmpty()) {
            return false;
        }

        try {
            int dist = Integer.parseInt(distText);
            return dist >= 1 && dist <= 100;
        } catch(NumberFormatException e) {
            return isValidVariableName(distText);
        }
    }

    private boolean isValidVariableName(String name) {
        return name != null && name.matches(regexValidation);
    }

    public String toString() {
        return name;
    }
}
