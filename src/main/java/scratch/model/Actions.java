package scratch.model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Actions {

    private static ObservableList<Actionable> actions = FXCollections.observableArrayList(
            new Move("Avancer de", "30"),
            new TurnLeft("Tourner à gauche", "90"),
            new TurnRight("Tourner à droite", "90"),
            new Up("Lever stylo"),
            new Down("Abaisser stylo"),
            new Repeat("Répéter"),
            new EndRepeat("Arreter la répétition")
    );

    public static ObservableList<Actionable> getActions() {
        return FXCollections.unmodifiableObservableList(actions);
    }

    public static Actionable getAction(int idx) {
        return (idx >= 0 && idx < actions.size()) ? actions.get(idx) : null;
    }

    public static boolean isValueValid(Actionable action, String textValue) {
        if (action == null) return false;

        // Regex : Commence par une lettre ou un _, suivi de lettres, chiffres ou _
        if (textValue.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            return true; // nom de variable valide
        }

        // Vérification des bornes
        try {
            int value = Integer.parseInt(textValue);

            if (action instanceof Move) {
                return value > 0 && value <= 100;
            } else if (action instanceof TurnLeft || action instanceof TurnRight) {
                return value >= 1 && value <= 180;
            }
            return true; // Lever ou Abaisser stylo

        } catch (NumberFormatException e) {
            // Ce n'est ni une variable valide, ni un nombre
            return false;
        }
    }
}
