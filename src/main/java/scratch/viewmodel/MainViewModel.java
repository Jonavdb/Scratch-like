package scratch.viewmodel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.ListChangeListener;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import scratch.model.*;


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MainViewModel {
    private ChosenActions chosenActions;
    private ObjectProperty<Actionable> selectedAction = new SimpleObjectProperty<>();
    private IntegerProperty idChosenAction = new SimpleIntegerProperty(-1); // -1 pour quand il y ait premier el, il soit à 0
    private BooleanProperty isLoad = new SimpleBooleanProperty(false);
    private BooleanProperty isPlaying = new SimpleBooleanProperty(false);
    private final BooleanProperty errorProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty isStop = new SimpleBooleanProperty(false);
    private final DoubleProperty speed = new SimpleDoubleProperty(1.0);
    private Timeline timeline;
    private Execution executor = new Execution();
    // debug
    private IntegerProperty currActionIndex = new SimpleIntegerProperty(0);
    private PlayerViewModel playerVm;
    private ProgramState state = new ProgramState(new Player(), new VariableMap());

    public MainViewModel(ChosenActions chosenActions) {
        this.chosenActions = chosenActions;
        this.chosenActions.getChosenActions().addListener((ListChangeListener<Actionable>) change -> {
            this.setIsPlaying(false);
            currActionIndex.set(0);
            isLoad.set(false);
        });
        playerVm = new PlayerViewModel(state.getPlayer());
    }

    public ReadOnlyBooleanProperty errorProperty() {
        return errorProperty;
    }

    public ObservableList<Actionable> getAllAction() {
        return Actions.getActions();
    }

    public ObservableList<Actionable> getChosenAction() {
        return chosenActions.getChosenActions();
    }

    public void addAction() {
        Actionable actionToAdd = selectedAction.get();
        if(actionToAdd == null) return;
        Actionable addItem = actionToAdd.copy();

        int targetIndex = idChosenAction.get();

        if(targetIndex >= 0) {
            chosenActions.addAtPosition(targetIndex + 1, actionToAdd);  // on le met après l'élément sélectionné
        } else {
            chosenActions.add(addItem); // si rien de sélectionné, on le met à la fin
        }
    }

    public void removeAction() {
        int index = getIdChosenAction();
        if(index >= 0) {
            chosenActions.remove(index);
            setIdChosenAction(index < chosenActions.nbChosenAction() ? index : index - 1);
        }
    }

    public void removeAll(){
        chosenActions.removeAll();
    }

    public void upAction() {
        if(getIdChosenAction() > 0 && getIdChosenAction() < chosenActions.nbChosenAction()) {
            chosenActions.up(getIdChosenAction());
            setIdChosenAction(getIdChosenAction() - 1);
        }
    }

    public void downAction() {
        if(getIdChosenAction() >= 0 && getIdChosenAction() < chosenActions.nbChosenAction() - 1) {
            chosenActions.down(getIdChosenAction());
            setIdChosenAction(getIdChosenAction() + 1);
        }
    }

    public void copyAction(){
        int index = getIdChosenAction();
        Actionable toCopy = chosenActions.get(index);
        Actionable copyItem = toCopy.copy();
        chosenActions.add(copyItem);
    }

    public void saveInFile(File file){
        if (file == null)
            return;
        try (PrintWriter writer = new PrintWriter(file)){
            for (Actionable action : chosenActions.getChosenActions()) {
                writer.println(action.toSave());
            }

            System.out.println("Sauvegarde réussie !");
        } catch (FileNotFoundException e) {
            System.err.println("Erreur lors de la sauvegarde" + e.getMessage());
        }
    }

    public void loadFile(File file){
        if (file == null) return;

        try (Scanner scan = new Scanner(file)) {
            chosenActions.removeAll();

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if(line.trim().isEmpty()) return;

                String[] parts = line.split(";");
                String action = parts[0];

                // On récupère la valeur en TEXTE (s'il y en a une)
                String textValueOrName = (parts.length > 1) ? parts[1] : "";
                String valueVariable = (parts.length > 2) ? parts[2] : "";
                Actionable newAction = null;

                switch (action) {
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
                    case "VAR_ASSIGNMENT" :
                        newAction = new AssignVariable("Assignation", textValueOrName, valueVariable);
                        break;
                    case "INCREMENT_VARIABLE" :
                        newAction = new Increment("Inc/Dec variable", textValueOrName, valueVariable);
                        break;
                    case "REPEAT" :
                        newAction = new Repeat("Répeter", textValueOrName);
                        break;
                    case "END_REPEAT" :
                        newAction = new EndRepeat("Arrêter la répétition");
                        break;
                }
                if (newAction != null) {
                    chosenActions.add(newAction);
                }
            }
            System.out.println("Chargement réussi !");

        } catch (FileNotFoundException e){
            System.err.println("Fichier introuvable : " + e.getMessage());
        }
    }

    public void executeAction() {
        Actionable next = executor.getNextAction();
        if (next != null){
            currActionIndex.set(executor.getGlobalIndex());
            next.execute(state);
        }
    }

    public BooleanBinding canUp() {
        return idChosenAction.greaterThan(0);
    }

    public BooleanBinding canDown() {
        return Bindings.createBooleanBinding(() -> getIdChosenAction() >= 0 && getIdChosenAction() < chosenActions.nbChosenAction() - 1, idChosenAction, chosenActions.getChosenActions());
    }

    public BooleanBinding canAdd() {
        return Bindings.isNotNull(selectedAction);
    }

    public BooleanBinding canRemove() {
        return idChosenAction.greaterThanOrEqualTo(0);
    }

    public BooleanBinding canDuplicate(){
        return idChosenAction.greaterThanOrEqualTo(0);
    }

    public BooleanBinding canLoadProgram() {
        return Bindings.createBooleanBinding(() -> {
            ObservableList<Actionable> list = chosenActions.getChosenActions();

            if(list.isEmpty() || isPlaying.get()) {
                return false;
            }

            boolean isPenDown = true;
            boolean hasSeenNonDeclaration = false;  // un mouvement, un stylo, une assignation
            int numberRepeat = 0;
            ArrayList<String> variables = new ArrayList<>();

            for(Actionable action : list) {
                /* --- Zone de déclaration de variables --- */
                if(action instanceof DeclareVariable) {
                    if(action.getProperty() != null) {
                        variables.add(action.getProperty().get());  // on ajoute la variable créée dans un array
                    }

                    if(hasSeenNonDeclaration) {
                        return false;   // déclaration après une action non-déclaration -> interdit
                    }
                } else {
                    hasSeenNonDeclaration = true;   // on est sur une non-déclaration -> on ferme la porte

                    // Vérification de la PREMIÈRE propriété (valable pour toutes les actions)
                    if(action.getProperty() != null && action.getProperty().get() != null) {
                        String param1 = action.getProperty().get();

                        // est-ce que le paramètre est un nom de variable (ex: "nb") et qu'elle n'existe dans pas notre tableau
                        if(param1.matches("^[a-zA-Z_][a-zA-Z0-9_]*$") && !variables.contains(param1)) {
                            return false;
                        }
                        // si ce n'est pas une variable (ex: c'est le chiffre "50"), on ne fait rien car c'est valide
                    }

                    // Vérification de la DEUXIÈME propriété (spécifique à Assign et Increment)
                    if(action instanceof AssignVariable assignVariable) {
                        String param2 = assignVariable.getParamProperty().get();
                        if(param2 != null && param2.matches("^[a-zA-Z_][a-zA-Z0-9_]*$") && !variables.contains(param2)) {
                            return false; // erreur : la variable 2 n'existe pas
                        }
                    } else if(action instanceof Increment increment) {
                        String param2 = increment.getIncrementProperty().get();
                        if(param2 != null && param2.matches("^[a-zA-Z_][a-zA-Z0-9_]*$") && !variables.contains(param2)) {
                            return false; // erreur : la variable 2 n'existe pas
                        }
                    }
                }

                /* --- Vérification du stylo --- */
                if(action instanceof Down) {
                    if(isPenDown) {
                        return false;
                    }
                    isPenDown = true;
                } else if(action instanceof Up) {
                    if(!isPenDown) {
                        return false;
                    }
                    isPenDown = false;
                }

                if (action instanceof Repeat) {
                    numberRepeat++;
                }
                if (action instanceof EndRepeat){
                    numberRepeat--;
                }

            }
            if (numberRepeat != 0){
                return false;
            }
            return true;
        }, chosenActions.getChosenActions(), isPlaying);
    }

    public BooleanBinding canRemoveAll() { return Bindings.isNotEmpty(chosenActions.getChosenActions()); }

    public BooleanBinding canStop() {
        return Bindings.createBooleanBinding(() -> {
            return isPlaying.get();
        }, isPlaying);
    }

    public boolean getIsStop(){
        return isStop.get();
    }

    public void stopAutoExecute(){
        isStop.set(true);
        isPlaying.set(false);
        if (timeline != null)
            timeline.stop();
    }

    public double getSpeed() {
        return speed.get();
    }

    public DoubleProperty speedProperty() {
        return speed;
    }

    public Actionable getSelectedAction() {
        return selectedAction.get();
    }

    public ObjectProperty<Actionable> selectedActionProperty() {
        return selectedAction;
    }

    public int getIdChosenAction() {
        return idChosenAction.get();
    }

    public IntegerProperty idChosenActionProperty() {
        return idChosenAction;
    }

    public IntegerProperty currActionIndexProperty() {
        return currActionIndex;
    }

    public void setIdChosenAction(int idChosenAction) {
        this.idChosenAction.set(idChosenAction);
    }

    public int getCurrentActionIndex() {
        return currActionIndex.get();
    }

    public void setCurrentActionIndex(int idx) {
        currActionIndex.set(idx);
    }

    public boolean getIsPlaying(){
        return isPlaying.get();
    }

    public void setIsPlaying(boolean value){
        isPlaying.set(value);
    }

    public void validateAndUpdateValue(Actionable selectedAction, String newText) {
        if(selectedAction == null || selectedAction.getProperty() == null) {
            errorProperty.set(false);
            return;
        }

        boolean isValid = Actions.isValueValid(selectedAction, newText);

        if (isValid) {
            errorProperty.set(false);
            selectedAction.getProperty().set(newText); // On sauvegarde le texte ("90" ou "nb")
        } else {
            errorProperty.set(true);
            selectedAction.getProperty().set(""); // Reset case vide
        }
    }

    public PlayerViewModel getPlayerViewModel() {
        return playerVm;
    }

    public PlayerViewModel resetPlayerViewModel() {
        state = new ProgramState(new Player(), new VariableMap());
        playerVm = new PlayerViewModel(state.getPlayer());
        return playerVm;
    }

    public StringBinding getLoadTextButton(){
        return Bindings.when(currActionIndex.isEqualTo(0)).then("Charger").otherwise("Reintialiser");
    }

    public StringBinding getExecuteTextButton(){
        return Bindings.when(currActionIndex.greaterThan(0)).then("Suivant").otherwise("Executer");
    }

    public void resetCommands(){
        resetPlayerViewModel();
        executor.init(chosenActions.getChosenActions(), state.getVariableMap());
        currActionIndex.set(0);
        isLoad.set(true);
    }

    public BooleanBinding canExecuteProgram(){
        return Bindings.createBooleanBinding(()->
            executor != null && !chosenActions.getChosenActions().isEmpty() && !executor.isDone() && isLoad.get() && !isPlaying.get(),
                chosenActions.getChosenActions(), currActionIndex, isLoad, isPlaying
        );
    }

    public void startAutoExecute(){
        isPlaying.set(true);
        isStop.set(false);
        timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event_1 -> {
            if (!isPlaying.get() || isStop.get()){
                timeline.stop();
                return;
            }
            Actionable next = executor.getNextAction();
            if (next != null) {
                next.execute(state);
                currActionIndex.set(currActionIndex.get() + 1);
            } else {
                timeline.stop();
                isPlaying.set(false);
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        speed.addListener((obs, oldVal, newVal) -> {
            timeline.setRate(1.0 / newVal.doubleValue());
        });
        timeline.setRate(1.0 / speed.get());
        timeline.play();
    }


}
