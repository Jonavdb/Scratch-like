package scratch.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Actions {
    private Actions() {

    }

    private static ObservableList<Actionable> actions = FXCollections.observableArrayList(
            new Move(),
            new Turn(),
            new Turn(),
            new Up(),
            new Down()

    );

    public static ObservableList<Actionable> getActions() {
        return FXCollections.unmodifiableObservableList(actions);
    }

    public static Actionable getAction(int idx) {
        return (idx >= 0 && idx < actions.size()) ? actions.get(idx) : null;
    }
}
