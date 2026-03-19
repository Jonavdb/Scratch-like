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


import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class MainViewModel {
    private ChosenActions chosenActions;
    private ObjectProperty<Actionable> selectedAction = new SimpleObjectProperty<>();
    private IntegerProperty idChosenAction = new SimpleIntegerProperty(-1);
    private BooleanProperty isLoad = new SimpleBooleanProperty(false);
    private BooleanProperty isPlaying = new SimpleBooleanProperty(false);
    private final BooleanProperty errorProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty isStop = new SimpleBooleanProperty(false);
    private final DoubleProperty speed = new SimpleDoubleProperty(1.0);
    private Timeline timeline;
    private List<Actionable> program;

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
        Actionable toAdd = selectedAction.get();
        Actionable addItem = toAdd.copy();
        chosenActions.add(addItem);
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
        if (currActionIndex.get() < program.size()) {
            System.out.println(program.size());
            System.out.println(currActionIndex.get());
            playerVm.processAction(program.get(currActionIndex.get()));
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
        resetPlayerViewModel();
        List<Actionable> tree = listIntoTree(chosenActions.getChosenActions());
        program = new ArrayList<>();
        TreeIntoList(tree, program);
        isLoad.set(true);
    }

    public BooleanBinding canExecuteProgram(){
        return Bindings.createBooleanBinding(()->
            program != null && !chosenActions.getChosenActions().isEmpty() && currActionIndex.get() < program.size() && isLoad.get() && !isPlaying.get(),
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
            if (currActionIndex.get() < program.size()) {
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

    private List<Actionable> listIntoTree(List<Actionable> basicList){
        List<Actionable> tree = new ArrayList<>();
        Stack<List<Actionable>> stack = new Stack<>();
        stack.push(tree);
        for(Actionable action : basicList) {
            if (action instanceof Repeat){
                Repeat repeat = (Repeat) action.copy();
                stack.peek().add(repeat);
                stack.push(repeat.getChildren());
            } else if (action instanceof EndRepeat) {
                if (stack.size() > 1){
                    stack.pop();
                }
            } else {
                stack.peek().add(action.copy());
            }
        }
        return tree;
    }

    private void TreeIntoList(List<Actionable> tree, List<Actionable> result){
        for (Actionable action : tree) {
            if (action instanceof Repeat) {
                Repeat repeat = (Repeat) action;
                int n = Integer.parseInt(repeat.getTick());
                for (int i = 0; i < n; ++i){
                    TreeIntoList(repeat.getChildren(), result);
                }
            } else {
                result.add(action);
            }
        } // applatissement du répeat à l'execution
    }


}
