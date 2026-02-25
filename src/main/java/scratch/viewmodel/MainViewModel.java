package scratch.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import scratch.model.*;

import javax.imageio.IIOException;
import java.io.*;
import java.util.Scanner;

public class MainViewModel {
    private ChosenActions chosenActions;
    private ObjectProperty<Actionable> selectedAction = new SimpleObjectProperty<>();
    private IntegerProperty idChosenAction = new SimpleIntegerProperty(-1);

    public MainViewModel(ChosenActions chosenActions) {
        this.chosenActions = chosenActions;
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
                writer.println(action.ToSave());
            }

            System.out.println("Sauvegarde réussie !");
        } catch (FileNotFoundException e) {
            System.err.println("Erreur lors de la sauvegarde" + e.getMessage());
        }
    }

    public void loadFile(File file){
        if (file == null)
            return;
        try (Scanner scan = new Scanner(file)) {
            chosenActions.removeAll();
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] parts = line.split(";");
                String action = parts[0];
                int value = Integer.parseInt(parts[1]);
                Actionable newAction = null;
                switch (action) {
                    case "MOVE_FORWARD":
                        newAction = new Move("Avancer de ", value);
                        break;
                    case "PEN_UP":
                        newAction = new Up("Lever stylo");
                        break;
                    case "PEN_DOWN":
                        newAction = new Down("Abaisser stylo");
                        break;
                    case "TURN_RIGHT":
                        newAction = new TurnRight("Tourner à droite de ", value);
                        break;
                    case "TURN_LEFT":
                        newAction = new TurnLeft("Tourner à gauche de ", value);
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

    public BooleanBinding canRemoveAll() { return Bindings.isNotEmpty(chosenActions.getChosenActions()); }

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

    public void setIdChosenAction(int idChosenAction) {
        this.idChosenAction.set(idChosenAction);
    }
}
