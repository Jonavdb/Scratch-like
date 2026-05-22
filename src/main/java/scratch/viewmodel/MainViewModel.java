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
import java.util.List;
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
    private boolean isDrawEnabled = false;
    private Timeline timeline;
    private Execution executor = new Execution();
    private IntegerProperty currActionIndex = new SimpleIntegerProperty(0);
    private PlayerViewModel playerVm;
    private TrackerViewModel trackerVm;
    private ProgramState state = new ProgramState(new Player(), new VariableMap());

    public MainViewModel(ChosenActions chosenActions) {
        this.chosenActions = chosenActions;
        this.chosenActions.getChosenActions().addListener((ListChangeListener<Actionable>) change -> {
            this.setIsPlaying(false);
            currActionIndex.set(0);
            isLoad.set(false);

            // nettoie les erreurs à chaque nouveau départ
            this.setErrorActionIndex(-1);
            this.setErrorRuntimeMessage("");
        });
        playerVm = new PlayerViewModel(state.getPlayer());
        trackerVm = new TrackerViewModel(state.getVariableMap(), state.getPlayer());
        resetCommands();
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
        if(isDrawEnabled) {
            return Actions.getAdvancedActions();
        }
        else {
            return Actions.getBasicActions();
        }
    }

    public void enableDrawPolygon(boolean bool) {
        isDrawEnabled = bool;
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
            return !chosenActions.getChosenActions().isEmpty()
                    && !isPlaying.get()
                    && chosenActions.validateForLoad();
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
        setIsPlaying(false);
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
    public TrackerViewModel getTrackerViewModel() {return trackerVm;}

    public void resetProgramState() {
        state.reset(); // reset the models data
        trackerVm.getEntries().clear(); // clear map display
    }

    public StringBinding getLoadTextButton() {
        return Bindings.when(currActionIndex.isEqualTo(0)).then("Charger").otherwise("Reintialiser");
    }

    public StringBinding getExecuteTextButton() {
        return Bindings.when(currActionIndex.greaterThan(0)).then("Suivant").otherwise("Executer");
    }

    public void resetCommands(){
        resetProgramState();
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

    public int getIndentationLevel(int index){
        int gap = 0;
        List<Actionable> actions = chosenActions.getChosenActions();
        for (int i = 0; i < index; i++) {
            Actionable pastItem = actions.get(i);
            if (pastItem instanceof Repeat) {
                gap++;
            } else if (pastItem instanceof EndRepeat) {
                gap--;
            }
        }
        Actionable currentItem = chosenActions.get(index);
        if (currentItem instanceof EndRepeat){
            gap--;
        }
        if (gap < 0){
            gap = 0;
        }
        return gap;
    }
}