package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class Polygon implements Actionable{
    private String regexValidation = "^[a-zA-Z_][a-zA-Z0-9_]*$";

    private final String actionName;
    private final StringProperty firstParamProperty; // property longueur côtés
    private final StringProperty secondParamProperty; // property nb côtés

    public Polygon(String actionName, String firstParamName, String secondParamName) {
        this.actionName = actionName;
        this.firstParamProperty = new SimpleStringProperty(firstParamName);
        this.secondParamProperty = new SimpleStringProperty(secondParamName);
    }
    @Override
    public void execute(ProgramState state) {
        String firstParamText = getFirstParamName(); // longueur côtés
        String secondParamText = getSecondParamName(); // nb côtés
        VariableMap map = state.getVariableMap();

        int length = map.getParamValue(firstParamText).orElseGet(() -> map.getValue(firstParamText));
        int nbSides = map.getParamValue(secondParamText).orElseGet(() -> map.getValue(secondParamText));
        String errtext = "";
        if(length < 3 || nbSides < 3)  {
            if(length < 3 && length < 3) {errtext = "Nombre de côtés et longueur invalide.";}
            else if(length < 3) {errtext = "Longueur côtés invalide.";}
            else if(nbSides < 3) {errtext = "Nombre de côtés invalide";}
            throw new RuntimeException(errtext);
        }
        state.getPlayer().drawPolygon(length, nbSides);

    }

    @Override
    public Actionable copy() {
        return new Polygon(this.actionName, this.getFirstParamName(), this.getSecondParamName());
    }

    @Override
    public String toSave() {
        return "DRAW_POLYGON;" + getFirstParamName() + ";" + getSecondParamName();
    }

    // prop longueur côtés
    @Override
    public StringProperty getProperty() {
        return this.firstParamProperty;
    }

    public StringProperty getSecondParamProperty() {
        return this.secondParamProperty;
    }

    public String getFirstParamName() {
        return this.firstParamProperty.get();
    }

    public String getSecondParamName() {
        return this.secondParamProperty.get();
    }



    @Override
    public Color getColor() {
        return Color.ORANGE;
    }

    @Override
    public boolean isValid(String... params) {
        // pour le moment on suppose qu'on aura toujours que 2 paramètres
        if(params.length != 2) return false;

        String firstParamText = params[0];
        String secondParamText = params[1];

        return isFirstValid(firstParamText) && isSecondValid(secondParamText);
    }

    public boolean isFirstValid(String text) {
        if(text == null || text.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isSecondValid(String text) {
        if(text == null || text.trim().isEmpty()) {
            return false;
        }

        return true;
    }

    private boolean isValidVariableName(String name) {
        return name != null && name.matches(regexValidation);
    }

    @Override
    public ActionType getType() {
        return ActionType.DRAW_POLYGON;
    }

    @Override
    public String toString() {
        return actionName;
    }
}
