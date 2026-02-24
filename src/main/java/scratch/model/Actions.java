package scratch.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Actions {
    private Actions() {

    }

    private static ObservableList<Actionable> actions = FXCollections.observableArrayList(
            new Move("Avancer de", 90),
            new TurnLeft("Tourner à gauche", 90),
            new TurnRight("Tourner à droite", 90),
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
