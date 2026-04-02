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
    private String regexValidation = "^[a-zA-Z_][a-zA-Z0-9_]*$"; // Valide noms variables (lettre/_ + lettres/chiffres/_*) : variable1, _var OK | 1var, ma-var KO
    private ObjectProperty<Actionable> selectedAction = new SimpleObjectProperty<>();
    private IntegerProperty idChosenAction = new SimpleIntegerProperty(-1); // -1 pour quand il y ait premier el, il soit à 0
    private BooleanProperty isLoad = new SimpleBooleanProperty(false);
    private BooleanProperty isPlaying = new SimpleBooleanProperty(false);
    private StringProperty errorRuntimeMessage = new SimpleStringProperty();
    private IntegerProperty errorActionIndex = new SimpleIntegerProperty(-1);
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

    public StringProperty getErrorRuntimeMessageProperty() {
        return errorRuntimeMessage;
    }

    public IntegerProperty errorActionIndexProperty() {
        return errorActionIndex;
    }

    public int getErrorActionIndex() {
        return errorActionIndex.get();
    }

    public void setErrorRuntimeMessage(String errorRuntimeMessage) {
        this.errorRuntimeMessage.set(errorRuntimeMessage);
    }

    public void setErrorActionIndex(int errorActionIndex) {
        this.errorActionIndex.set(errorActionIndex);
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

        // on fabrique le clone
        Actionable addItem = actionToAdd.copy();

        int targetIndex = idChosenAction.get();

        if(targetIndex >= 0) {
            chosenActions.addAtPosition(targetIndex + 1, addItem);  // on le met après l'élément sélectionné
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

    public void removeAll() {
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

    public void copyAction() {
        int index = getIdChosenAction();
        Actionable toCopy = chosenActions.get(index);
        Actionable copyItem = toCopy.copy();
        chosenActions.add(copyItem);
    }

    public void saveInFile(File file) {
        chosenActions.saveInFile(file);
    }

    public void loadFile(File file) {
        if(file == null) return;
        setIsPlaying(false);
        setIsStop(false);
        setErrorActionIndex(-1);
        setErrorRuntimeMessage("");
        currActionIndex.set(0);
        isLoad.set(false);
        chosenActions.removeAll();
        chosenActions.loadFile(file);
    }

    public void executeAction() {
        if(isStop.get() || getErrorActionIndex() != -1) return;

        Actionable next = executor.getNextAction();
        if(next != null) {
            setCurrentActionIndex(executor.getCurrentExecutingIndex());

            try {
                next.execute(state);
            } catch(RuntimeException e){
                setIsPlaying(false);
                setIsStop(true);
                setCurrentActionIndex(currActionIndex.get());
                setErrorActionIndex(executor.getCurrentExecutingIndex());
                setErrorRuntimeMessage(e.getMessage());
            }
        } else {
            setIsPlaying(false);

            // on nettoie les vieilles erreurs avant de relancer
            setErrorActionIndex(-1);
            setErrorRuntimeMessage("");
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

    public BooleanBinding canDuplicate() {
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
                        if(action.getProperty().get().matches(regexValidation)) {
                            variables.add(action.getProperty().get());  // on ajoute la variable créée dans un array
                        } else {
                            return false;
                        }
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
                        if(param1.matches(regexValidation) && !variables.contains(param1)) {
                            return false;
                        }
                    }

                    // Vérification de la DEUXIÈME propriété (spécifique à Assign et Increment)
                    if(action instanceof AssignVariable|| action instanceof Increment) {
                        if (action.getProperty().get().isEmpty() || action.getProperty() == null){
                            return false;
                        }
                        String param2 = action instanceof AssignVariable ? ((AssignVariable) action).getParamProperty().get() : ((Increment) action).getIncrementParam();

                        if(param2 != null && param2.matches(regexValidation) && !variables.contains(param2)) {
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

                if(action instanceof Repeat) {
                    numberRepeat++;
                }
                if(action instanceof EndRepeat) {
                    numberRepeat--;
                }

            }
            if(numberRepeat != 0) {
                return false;
            }
            return true;
        }, chosenActions.getChosenActions(), isPlaying);
    }

    public BooleanBinding canRemoveAll() {
        return Bindings.isNotEmpty(chosenActions.getChosenActions());
    }

    public BooleanBinding canStop() {
        return Bindings.createBooleanBinding(() -> {
            return isPlaying.get();
        }, isPlaying);
    }

    public boolean getIsStop() {
        return isStop.get();
    }

    public void stopAutoExecute() {
        setIsStop(true);
        setIsStop(false);
        if(timeline != null) timeline.stop();
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
        errorRuntimeMessage.set("");    // on efface les erreurs des actions précédentes
        this.idChosenAction.set(idChosenAction);
    }

    public int getCurrentActionIndex() {
        return currActionIndex.get();
    }

    public void setCurrentActionIndex(int idx) {
        currActionIndex.set(idx);
    }

    public boolean getIsPlaying() {
        return isPlaying.get();
    }

    public void setIsPlaying(boolean value) {
        isPlaying.set(value);
    }

    public void setIsStop(boolean value) {
        isStop.set(value);
    }

    public void validateAndUpdateValue(Actionable selectedAction, String newText) {
        if(selectedAction == null || selectedAction.getProperty() == null) {
            errorProperty.set(false);
            return;
        }

        boolean isValid = Actions.isValueValid(selectedAction, newText);

        if(isValid) {
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

    public StringBinding getLoadTextButton() {
        return Bindings.when(currActionIndex.isEqualTo(0)).then("Charger").otherwise("Reintialiser");
    }

    public StringBinding getExecuteTextButton() {
        return Bindings.when(currActionIndex.greaterThan(0)).then("Suivant").otherwise("Executer");
    }

    public void resetCommands() {
        resetPlayerViewModel();
        executor.init(chosenActions.getChosenActions(), state.getVariableMap());
        currActionIndex.set(0);
        isLoad.set(true);
        setIsStop(false);
        setErrorActionIndex(-1);
        setErrorRuntimeMessage("");
    }

    public BooleanBinding canExecuteProgram() {
        return Bindings.createBooleanBinding(() ->
                        executor != null && !chosenActions.getChosenActions().isEmpty() && !executor.isDoneProperty().get() && isLoad.get() && !isPlaying.get(),
                chosenActions.getChosenActions(), currActionIndex, isLoad, isPlaying, executor.isDoneProperty()
        );
    }

    public void startAutoExecute() {
        // on nettoie les vieilles erreurs avant de relancer
        setErrorActionIndex(-1);
        setErrorRuntimeMessage("");
        setIsPlaying(true);
        setIsStop(false);

        timeline = new Timeline();

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event_1 -> {
            if(!isPlaying.get() || isStop.get()) {
                timeline.stop();
                return;
            }

            Actionable next = executor.getNextAction();
            if(next != null) {
                setCurrentActionIndex(executor.getCurrentExecutingIndex());

                try {
                    next.execute(state);
                } catch(RuntimeException e) {
                    timeline.stop();
                    setIsPlaying(false);
                    setIsStop(true);
                    setCurrentActionIndex(currActionIndex.get());
                    setErrorActionIndex(executor.getCurrentExecutingIndex());
                    setErrorRuntimeMessage(e.getMessage());
                }
            } else {
                timeline.stop();
                setIsPlaying(false);
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