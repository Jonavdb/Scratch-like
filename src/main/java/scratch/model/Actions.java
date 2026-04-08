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

    public static boolean isValueValid(Actionable action, String textValue) {
        if (action == null || textValue == null || textValue.trim().isEmpty()) return false;

        boolean isValidVarName = textValue.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"); // commence par une lettre ou un _, suivi de lettres, chiffres ou _

        if(action instanceof DeclareVariable) {
            return isValidVarName; // les déclarations ne peuvent être QUE des noms de variables
        }

        if(isValidVarName) return true; // pour les mouvements, on accepte un nom de variable OU un chiffre

        // Vérification nombres et bornes
        try {
            int value = Integer.parseInt(textValue);

            if (action instanceof Move) {
                return value > 0 && value <= 100;
            } else if (action instanceof TurnLeft || action instanceof TurnRight) {
                return value >= 1 && value <= 180;
            } else if (action instanceof Repeat) {
                return value > 0;
            }
            return true; // Lever ou Abaisser stylo

        } catch (NumberFormatException e) {
            return false;   // ce n'est ni une variable valide, ni un nombre
        }
    }
}
