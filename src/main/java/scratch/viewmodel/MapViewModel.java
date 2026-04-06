package scratch.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scratch.model.MapActionType;
import scratch.model.MapChangeObject;
import scratch.model.VariableMap;
import scratch.model.VariableMapListener;

public class MapViewModel implements VariableMapListener<String, Integer> {

    public static class VariableRow {
        private final StringProperty nameProperty;
        private final StringProperty valueProperty;

        public VariableRow(String name, String value) {
            this.nameProperty = new SimpleStringProperty(name);
            this.valueProperty = new SimpleStringProperty(value);
        }

        public String getNameVariable() {return nameProperty.get();}
        public String getValueStr() {return valueProperty.getValue();}

        public StringProperty getNameProperty() {return nameProperty;}
        public StringProperty getValueProperty() {return valueProperty;}

        public void updateName(String name) {nameProperty.set(name);}
        public void updateValue(String value) {valueProperty.set(value);}
    }

    private final ObservableList<VariableRow> rows = FXCollections.observableArrayList();

    public MapViewModel(VariableMap map) {
        map.addListener(this);
    }

    // fonction met à jour les données d'affichage de la liste des entrées de la TableView
    // change contient les nouvelles données de l'entrée qui a été créée / modifié
    @Override
    public void onUpdate(MapChangeObject change) {
        // on initialise les données du row (c'est les données de cet objet qui seront utilisées par tableView)
        VariableRow row;

        // on récupère le type d'action (pour savoir si on ajoute une ligne, ou on met à jour une ligne existante)
        var actionType = change.getActionType();

        // on récupère le nom de la variable (qui va être affchée à gauche)
        String rowVariable = change.getVariable();

        // on récupère sa valeur (qui va être affichée à droite)
        String rowStrValue;

        if(actionType == MapActionType.DECLARE) {
            // creation d'une nouvelle ligne
            row = new VariableRow(rowVariable, "0");
            System.out.println("on update: " + rows.hashCode());
            rows.add(row);
        } else {

            // ici, mise à jour d'une ligne existante
            // on récupère l'entrée dont le nom de la valeur correspond aux datas de modificitation (MapChangeObject)
            row = rows.stream()
                    .filter(v -> v.getNameVariable().equals(rowVariable))
                    .findFirst()
                    .orElse(null); // Retourne null si la variable n'est pas trouvée

            // si aucun résulat, rien n'est fait
            if(row == null) return;

            // si l'entrée est trouvée, mise à jour de la valeur de la variable
            rowStrValue = Integer.toString(change.getValue());
            row.updateValue(rowStrValue);
        }
    }

    public ObservableList<VariableRow> getEntries() {return rows;}
}
