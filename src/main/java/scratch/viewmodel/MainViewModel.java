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
import javafx.scene.control.ListView;
import scratch.model.*;


import java.io.*;
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

    // debug
    private IntegerProperty currActionIndex = new SimpleIntegerProperty(0);
    private PlayerViewModel playerVm = new PlayerViewModel(new Player());

    public MainViewModel(ChosenActions chosenActions) {
        this.chosenActions = chosenActions;
        this.chosenActions.getChosenActions().addListener((ListChangeListener<Actionable>) change -> {
            this.setIsPlaying(false);
            currActionIndex.set(0);
            isLoad.set(false);
        });
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
                String textValue = (parts.length > 1) ? parts[1] : "";

                Actionable newAction = null;

                switch (action) {
                    case "MOVE_FORWARD":
                        newAction = new Move("Avancer de ", textValue);
                        break;
                    case "PEN_UP":
                        newAction = new Up("Lever stylo");
                        break;
                    case "PEN_DOWN":
                        newAction = new Down("Abaisser stylo");
                        break;
                    case "TURN_RIGHT":
                        newAction = new TurnRight("Tourner à droite de ", textValue);
                        break;
                    case "TURN_LEFT":
                        newAction = new TurnLeft("Tourner à gauche de ", textValue);
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
        if (currActionIndex.get() < chosenActions.getChosenActions().size()) {
            Actionable action = chosenActions.get(currActionIndex.get());
            playerVm.processAction(action);
            currActionIndex.set(currActionIndex.get() + 1);
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
            if (list.isEmpty() || isPlaying.get()) {
                return false;
            }
            boolean isPenDown = true;
            for (Actionable action : list) {
                if (action instanceof Down) {
                    if (isPenDown) {
                        return false;
                    }
                    isPenDown = true;
                } else if (action instanceof Up) {
                    if (!isPenDown) {
                        return false;
                    }
                    isPenDown = false;
                }
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
        if(selectedAction == null || selectedAction.getFirstProperty() == null) {
            errorProperty.set(false);
            return;
        }

        boolean isValid = Actions.isValueValid(selectedAction, newText);

        if (isValid) {
            errorProperty.set(false);
            selectedAction.getFirstProperty().set(newText); // On sauvegarde le texte ("90" ou "nb")
        } else {
            errorProperty.set(true);
            selectedAction.getFirstProperty().set(""); // Reset case vide
        }
    }

    public PlayerViewModel getPlayerViewModel() {
        return playerVm;
    }

    public PlayerViewModel resetPlayerViewModel() {
        playerVm = new PlayerViewModel(new Player());
        return playerVm;
    }

    public StringBinding getLoadTextButton(){
        return Bindings.when(currActionIndex.isEqualTo(0)).then("Charger").otherwise("Reintialiser");
    }

    public StringBinding getExecuteTextButton(){
        return Bindings.when(currActionIndex.greaterThan(0)).then("Suivant").otherwise("Executer");
    }

    public void resetCommands(){
        currActionIndex.set(0);
        isLoad.set(true);
        resetPlayerViewModel();
    }

    public BooleanBinding canExecuteProgram(){
        return Bindings.createBooleanBinding(()->
            !chosenActions.getChosenActions().isEmpty() && currActionIndex.get() < chosenActions.getChosenActions().size() && isLoad.get() && !isPlaying.get(),
            chosenActions.getChosenActions(),
            currActionIndex, isLoad, isPlaying
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
            if (currActionIndex.get() < chosenActions.getChosenActions().size()) {
                executeAction();
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
