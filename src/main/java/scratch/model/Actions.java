package scratch.model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Actions {
    private static ObservableList<Actionable> actions = FXCollections.observableArrayList(
            new DeclareVariable("Déclaration variable", ""),
            new AssignVariable("Assignation", "", ""),
            new Increment("Inc/Dec variable", "", ""),
            new Move("Avancer de", "30"),
            new TurnLeft("Tourner à gauche de", "90"),
            new TurnRight("Tourner à droite de", "90"),
            new Repeat("Répéter", "4"),
            new EndRepeat("Arreter la répétition"),
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
