package scratch.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Actions {
    private Actions() {

    }

    private static ObservableList<Actionable> actions = FXCollections.observableArrayList(
            new Move("Avancer de"),
            new Turn("Tourner à gauche"),
            new Turn("Tourner à droite"),
            new Up("Lever stylo"),
            new Down("Abaisser stylo")
    );

    public static ObservableList<Actionable> getActions() {
        return FXCollections.unmodifiableObservableList(actions);
    }

    public static Actionable getAction(int idx) {
        return (idx >= 0 && idx < actions.size()) ? actions.get(idx) : null;
    }
}
