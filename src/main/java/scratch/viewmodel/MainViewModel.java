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

            // validation des règles métier (si une seule est fausse, on retourne false)
            return isDeclarationZoneValid(list)
                    && isRepeatBalanced(list)
                    && isPenStateValid(list)
                    && areVariablesValid(list);

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


    // =========================================================================
    // METHODES POUR CAN LOAD
    // =========================================================================
    /* Le principe de "la porte fermée" : on lit les actions de haut en bas, tant qu'on lit des "DeclareVariable", c'est ok
     * dès qu'on lit une Action qui n'est pas "DeclareVariable", on ferme la porte.
     * Ca permet de n'avoir que des déclarations en haut du programme.
     */
    private boolean isDeclarationZoneValid(ObservableList<Actionable> list) {
        boolean isDoorClosed = false;

        for(Actionable action : list) {
            if(action instanceof DeclareVariable) {
                if(isDoorClosed) return false;
            } else {
                isDoorClosed = true;
            }
        }
        return true;
    }

    // boucles
    private boolean isRepeatBalanced(ObservableList<Actionable> list) {
        int numberRepeat = 0;

        for(Actionable action : list) {
            if(action instanceof Repeat) numberRepeat++;
            if(action instanceof EndRepeat) numberRepeat--;

            if(numberRepeat < 0) return false; // un EndRepeat avant un Repeat
        }
        return numberRepeat == 0;
    }

    // cohérence du stylo
    private boolean isPenStateValid(ObservableList<Actionable> list) {
        boolean isPenDown = true;

        for(Actionable action : list) {
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
    private boolean areVariablesValid(ObservableList<Actionable> list) {
        ArrayList<String> variables = new ArrayList<>();

        for(Actionable action : list) {
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