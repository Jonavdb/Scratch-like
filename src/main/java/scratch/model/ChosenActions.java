package scratch.model;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class ChosenActions {
    private final String regexValidation = "^[a-zA-Z_][a-zA-Z0-9_]*$";

    private ObservableList<Actionable> chosenActions = FXCollections.observableArrayList(
            action -> {
                if(action == null)
                    return new Observable[0];
                if(action instanceof AssignVariable av) {
                    return new Observable[]{av.getProperty(), av.getParamProperty()}; // si assign, on observe les 2 propriétés
                } else if(action instanceof Increment inc) {
                    return new Observable[]{inc.getProperty(), inc.getIncrementProperty()}; // si increm, on observe les 2 propriétés
                } else if(action instanceof Polygon polyg) {
                    return new Observable[]{polyg.getProperty(), polyg.getSecondParamProperty()}; // si draw polygon, on observe les 2 propriétés
                }else if(action.getProperty() != null) {
                    return new Observable[]{action.getProperty()}; // sinon on observe juste la propriété principale si action avec une seule propriété
                }
                return new Observable[0]; // si pas de propriété, on observe rien
            }
    );

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
            case "DRAW_POLYGON" -> new Polygon("Polygone", textValueOrName, valueVariable);
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
                && areVariablesValid()
               // && areLiteralValuesValid() - au cas ou, sinon laisser commenté
                && isRepeatVariableProtected()
                && isDrawPolygonBalanced();


    }

    /* Le principe de "la porte fermée" : on lit les actions de haut en bas, tant qu'on lit des "DeclareVariable", c'est ok
     * dès qu'on lit une Action qui n'est pas "DeclareVariable", on ferme la porte.
     * Ca permet de n'avoir que des déclarations en haut du programme.
     */
    private boolean isDeclarationZoneValid() {
        boolean isDoorClosed = false;
        for(Actionable action : chosenActions) {
            if(action instanceof DeclareVariable) {
                if(isDoorClosed) return false;
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

    // dessin polygon
    private boolean isDrawPolygonBalanced() {
        int nbDraw = 0;
        for (Actionable action : chosenActions) {
            if(action.getType() == ActionType.DRAW_POLYGON) {
                ++nbDraw;
            }
            if(nbDraw > 3) {
                return false;
            }
        }
        return true;
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
        ArrayList<String> declaredVariables = new ArrayList<>();

        for (Actionable action : chosenActions) {
            if (action.getType() == ActionType.DECLARE) {
                if (action.getProperty() == null) return false;

                String varName = action.getProperty().get();
                if (varName == null || !varName.matches(regexValidation)) {
                    return false;
                }
                declaredVariables.add(varName);
            }
            // gère l'usage variables déclarées dans les autres actions
            else {
                // verif validité paramètre 1
                if (action.getProperty() != null && action.getProperty().get() != null) {
                    String param1 = action.getProperty().get();

                    // erreur : l'utilisateur a ecrit le nom  d'une variable non déclaré pour le premier paramètre
                    if (param1.matches(regexValidation) && !declaredVariables.contains(param1)) {
                        return false;
                    }
                }

                // verif validité paramètre 2 (pour les actions à deux paramètres : ASSIGN, INCREMENT, DRAW_POLYGON)
                if (action.getType() == ActionType.ASSIGN || action.getType() == ActionType.INCREMENT || action.getType() == ActionType.DRAW_POLYGON) {
                    String param2 = null;

                    // on extrait le deuxieme paramètre
                    if (action.getType() == ActionType.ASSIGN) {
                        param2 = ((AssignVariable) action).getParamProperty().get();
                    } else if (action.getType() == ActionType.INCREMENT) {
                        param2 = ((Increment) action).getIncrementParam();
                    } else if (action.getType() == ActionType.DRAW_POLYGON) {
                        if (((Polygon) action).getSecondParamProperty() != null) {
                            param2 = ((Polygon) action).getSecondParamProperty().get();
                        }
                    }

                    // erreur : l'utilisateur a ecrit le nom  d'une variable non déclaré pour le deuxième paramètre
                    if (param2 != null && param2.matches(regexValidation) && !declaredVariables.contains(param2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // gestion des valeurs littérales (optionnel)
    private boolean areLiteralValuesValid() {
        for (Actionable action : chosenActions) {

            // --- 1. Vérification du Paramètre 1 pour les valeurs littérales ---
            if (action.getProperty() != null && action.getProperty().get() != null) {
                String param1 = action.getProperty().get();

                // S'il ne correspond pas à l'expression régulière, il est traité comme une valeur littérale
                if (!param1.matches(regexValidation)) {

                    if (action.getType() == ActionType.DRAW_POLYGON) {
                        // if ( /* [INSÉRER LA CONDITION ICI] */ ) return false;
                    }
                    // Ajouter un else-if ici pour les autres actions où param1 peut être une valeur littérale
                    if (action.getType() == ActionType.MOVE) {
                        // if ( /* [INSÉRER LA CONDITION ICI] */ ) return false;
                    }
                    if (action.getType() == ActionType.TURN_RIGHT) {
                        // if ( /* [INSÉRER LA CONDITION ICI] */ ) return false;
                    }
                    if (action.getType() == ActionType.TURN_LEFT) {
                        // if ( /* [INSÉRER LA CONDITION ICI] */ ) return false;
                    }
                }
            }

            // --- 2. Vérification du Paramètre 2 pour les valeurs littérales ---
            if (action.getType() == ActionType.ASSIGN || action.getType() == ActionType.INCREMENT || action.getType() == ActionType.DRAW_POLYGON) {
                String param2 = null;

                if (action.getType() == ActionType.ASSIGN) {
                    param2 = ((AssignVariable) action).getParamProperty().get();
                } else if (action.getType() == ActionType.INCREMENT) {
                    param2 = ((Increment) action).getIncrementParam();
                } else if (action.getType() == ActionType.DRAW_POLYGON) {
                    param2 = ((Polygon) action).getSecondParamProperty().get();
                }

                // Si param2 n'est pas nul et ne correspond PAS à l'expression régulière, c'est une valeur littérale
                if (param2 != null && !param2.matches(regexValidation)) {
                    // Ajouter la logique de vérification ici
                }
                // Ajouter un else-if ici pour d'autres actions où param2 peut être une valeur littérale nécessitant une vérification
            }
        }
        return true;
    }
    private boolean isRepeatVariableProtected() {
        Stack<String> protectedVariables = new Stack<>(); // on déclare un stack de nom de variable

        for(Actionable action : chosenActions) {
            if(action.getType() == ActionType.REPEAT) {
                String repeatVar = action.getProperty().get();
                if(repeatVar != null && repeatVar.matches(regexValidation)) {
                    protectedVariables.push(repeatVar); // on ajoute le nom de la variable assignée à Repeat à la stack de String
                } else {
                    protectedVariables.push(""); // on ajoute un nom vide pour quand même former la pile correcte car la valeur du Tick d'un repeat peut être un nombre et non une variable
                }
            } else if(action.getType() == ActionType.END_REPEAT) {
                if(!protectedVariables.isEmpty()) {
                    protectedVariables.pop(); // si on rencontre un EndRepeat, on enlève le dernier étage de la pile (soit le nom de la dernière variable assignée au dernier repeat rencontré)
                }
            } else if(action.getType() == ActionType.INCREMENT || action.getType() == ActionType.ASSIGN) {
                String variableToCheck = action.getProperty().get();
                if(variableToCheck != null && protectedVariables.contains(variableToCheck)) {
                    return false; // Si le nom de la variable qu'on veut modifier est le même que celui d'un repeat encore en cours, on retourne faux
                }
            }
        }
        return true;
    }
}
