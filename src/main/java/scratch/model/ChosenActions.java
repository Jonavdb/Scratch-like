package scratch.model;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ChosenActions {
    private ObservableList<Actionable> chosenActions = FXCollections.observableArrayList(
            action -> {
                if (action != null && action.getProperty() != null) {
                    return new Observable[]{
                            action.getProperty()
                    };
                }
                return new Observable[0];
            }
    );
    //
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

    public Actionable remove(int idx){
        if (idx >= 0 && idx < chosenActions.size()){
            return chosenActions.remove(idx);
        }
        return null;
    }

    public void removeAll(){
        chosenActions.clear();
    }

    public void up(int idx) {
        if (idx > 0 && idx < chosenActions.size()){
            Actionable action = chosenActions.set(idx - 1, chosenActions.get(idx));
            chosenActions.set(idx, action);
        }
    }

    public void down (int idx) {
        if (idx >= 0 && idx < chosenActions.size() - 1) {
            Actionable action = chosenActions.set(idx + 1, chosenActions.get(idx));
            chosenActions.set(idx, action);
        }
    }

    public int nbChosenAction(){
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
            chosenActions.removeAll();
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                if(line.trim().isEmpty()) return;

                String[] parts = line.split(";");
                String action = parts[0];

                // On récupère la valeur en TEXTE (s'il y en a une)
                String textValueOrName = (parts.length > 1) ? parts[1] : "";
                String valueVariable = (parts.length > 2) ? parts[2] : "";
                Actionable newAction = null;

                switch(action) {
                    case "MOVE_FORWARD":
                        newAction = new Move("Avancer de", textValueOrName);
                        break;
                    case "PEN_UP":
                        newAction = new Up("Lever stylo");
                        break;
                    case "PEN_DOWN":
                        newAction = new Down("Abaisser stylo");
                        break;
                    case "TURN_RIGHT":
                        newAction = new TurnRight("Tourner à droite de", textValueOrName);
                        break;
                    case "TURN_LEFT":
                        newAction = new TurnLeft("Tourner à gauche de", textValueOrName);
                        break;
                    case "VAR_DECLARATION":
                        newAction = new DeclareVariable("Déclaration variable", textValueOrName);
                        break;
                    case "VAR_ASSIGNMENT":
                        newAction = new AssignVariable("Assignation", textValueOrName, valueVariable);
                        break;
                    case "INCREMENT_VARIABLE":
                        newAction = new Increment("Inc/Dec variable", textValueOrName, valueVariable);
                        break;
                    case "REPEAT":
                        newAction = new Repeat("Répeter", textValueOrName);
                        break;
                    case "END_REPEAT":
                        newAction = new EndRepeat("Arrêter la répétition");
                        break;
                }
                if(newAction != null) {
                    chosenActions.add(newAction);
                }
            }
            System.out.println("Chargement réussi !");

        } catch(FileNotFoundException e) {
            System.err.println("Fichier introuvable : " + e.getMessage());
        }
    }

}
