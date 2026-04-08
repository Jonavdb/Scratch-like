package scratch.model;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class ChosenActions {
    private final String regexValidation = "^[a-zA-Z_][a-zA-Z0-9_]*$";

    private ObservableList<Actionable> chosenActions = FXCollections.observableArrayList(
            action -> {
                if(action != null && action.getProperty() != null) {
                    return new Observable[]{
                            action.getProperty()
                    };
                }
                return new Observable[0];
            }
    );

    // trouvez un moyen pour que ça ne pointe pas sur ma liste mais sur mes actions en elles-mêmes et ensuite passer à l'action suivante.
    // stack d'index donc quand on arrive à un bloc repeat, push pour rentrer dedans, pop pour sortir.
    // état d'éxécution qui retient des trucs.
    // multi index et multi list Quand j'arrive à un bloc repeat, je stack la liste et son index et je repars sur la liste du bloc repeat et je set son index personnel à 0.

    private final ObservableList<Actionable> unmodifiableChosenActions = FXCollections.unmodifiableObservableList(chosenActions);

    public ObservableList<Actionable> getChosenActions() {
        return unmodifiableChosenActions;
    }

    public void add(Actionable action) {
        chosenActions.add(action);
    }

    public void addAtPosition(int item, Actionable action) {
        chosenActions.add(item, action);
    }

    public Actionable remove(int idx) {
        if(idx >= 0 && idx < chosenActions.size()) {
            return chosenActions.remove(idx);
        }
        return null;
    }

    public void removeAll() {
        chosenActions.clear();
    }

    public void up(int idx) {
        if(idx > 0 && idx < chosenActions.size()) {
            Actionable action = chosenActions.set(idx - 1, chosenActions.get(idx));
            chosenActions.set(idx, action);
        }
    }

    public void down(int idx) {
        if(idx >= 0 && idx < chosenActions.size() - 1) {
            Actionable action = chosenActions.set(idx + 1, chosenActions.get(idx));
            chosenActions.set(idx, action);
        }
    }

    public int nbChosenAction() {
        return chosenActions.size();
    }

    public Actionable get(int index) {
        return chosenActions.get(index);
    }

    public void saveInFile(File file) {
        if(file == null)
            return;
        try(PrintWriter writer = new PrintWriter(file)) {
            for(Actionable action : unmodifiableChosenActions) {
                writer.println(action.toSave());
            }

            System.out.println("Sauvegarde réussie !");
        } catch(FileNotFoundException e) {
            System.err.println("Erreur lors de la sauvegarde" + e.getMessage());
        }
    }

    public void loadFile(File file) {
        if(file == null) return;

        try(Scanner scan = new Scanner(file)) {
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                if(line.trim().isEmpty()) return;

                String[] parts = line.split(";");
                String action = parts[0];

                // On récupère la valeur en TEXTE (s'il y en a une)
                String textValueOrName = (parts.length > 1) ? parts[1] : "";
                Actionable newAction = getActionable(parts, action, textValueOrName);

                if(newAction != null) {
                    chosenActions.add(newAction);
                }
            }
            System.out.println("Chargement réussi !");

        } catch(FileNotFoundException e) {
            System.err.println("Fichier introuvable : " + e.getMessage());
        }
    }

    private static Actionable getActionable(String[] parts, String action, String textValueOrName) {
        String valueVariable = (parts.length > 2) ? parts[2] : "";
        return switch(action) {
            case "MOVE_FORWARD" -> new Move("Avancer de", textValueOrName);
            case "PEN_UP" -> new Up("Lever stylo");
            case "PEN_DOWN" -> new Down("Abaisser stylo");
            case "TURN_RIGHT" -> new TurnRight("Tourner à droite de", textValueOrName);
            case "TURN_LEFT" -> new TurnLeft("Tourner à gauche de", textValueOrName);
            case "VAR_DECLARATION" -> new DeclareVariable("Déclaration variable", textValueOrName);
            case "VAR_ASSIGNMENT" -> new AssignVariable("Assignation", textValueOrName, valueVariable);
            case "INCREMENT_VARIABLE" -> new Increment("Inc/Dec variable", textValueOrName, valueVariable);
            case "REPEAT" -> new Repeat("Répeter", textValueOrName);
            case "END_REPEAT" -> new EndRepeat("Arrêter la répétition");
            default -> null;
        };
    }

    public boolean validateForLoad() {
        // validation des règles métier (si une seule est fausse, on retourne false)
        return isDeclarationZoneValid()
                && isRepeatBalanced()
                && isPenStateValid()
                && areVariablesValid();
    }

    /* Le principe de "la porte fermée" : on lit les actions de haut en bas, tant qu'on lit des "DeclareVariable", c'est ok
     * dès qu'on lit une Action qui n'est pas "DeclareVariable", on ferme la porte.
     * Ca permet de n'avoir que des déclarations en haut du programme.
     */
    private boolean isDeclarationZoneValid() {
        boolean isDoorClosed = false;
        for (Actionable action : chosenActions) {
            if (action instanceof DeclareVariable) {
                if (isDoorClosed) return false;
            } else {
                isDoorClosed = true;
            }
        }
        return true;
    }

    // boucles
    private boolean isRepeatBalanced() {
        int numberRepeat = 0;

        for(Actionable action : chosenActions) {
            if(action instanceof Repeat) numberRepeat++;
            if(action instanceof EndRepeat) numberRepeat--;

            if(numberRepeat < 0) return false; // un EndRepeat avant un Repeat
        }
        return numberRepeat == 0;
    }

    // cohérence du stylo
    private boolean isPenStateValid() {
        boolean isPenDown = true;

        for(Actionable action : chosenActions) {
            if(action instanceof Down) {
                if(isPenDown) return false; // Déjà baissé
                isPenDown = true;
            } else if(action instanceof Up) {
                if(!isPenDown) return false; // Déjà levé
                isPenDown = false;
            }
        }
        return true;
    }

    // gestion des variables (création et utilisation)
    private boolean areVariablesValid() {
        ArrayList<String> variables = new ArrayList<>();

        for(Actionable action : chosenActions) {
            // on enregistre les variables créées
            if(action instanceof DeclareVariable) {
                if(action.getProperty() == null) return false;

                String varName = action.getProperty().get();
                if(varName == null || !varName.matches(regexValidation)) return false;

                variables.add(varName);
            }
            // on vérifie l'utilisation dans les autres actions
            else {
                // Vérification Paramètre 1
                if(action.getProperty() != null && action.getProperty().get() != null) {
                    String param1 = action.getProperty().get();
                    if(param1.matches(regexValidation) && !variables.contains(param1)) {
                        return false; // Utilise une variable non déclarée
                    }
                }

                // Vérification Paramètre 2 (Spécifique Assign et Increment)
                if(action instanceof AssignVariable || action instanceof Increment) {
                    if(action.getProperty() == null || action.getProperty().get().isEmpty()) {
                        return false;
                    }
                    String param2 = action instanceof AssignVariable
                            ? ((AssignVariable) action).getParamProperty().get()
                            : ((Increment) action).getIncrementParam();

                    if(param2 != null && param2.matches(regexValidation) && !variables.contains(param2)) {
                        return false; // Utilise une variable non déclarée en paramètre 2
                    }
                }
            }
        }
        return true;
    }
}
