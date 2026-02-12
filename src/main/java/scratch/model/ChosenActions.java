package scratch.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.action.Action;

public class ChosenActions {

    private ObservableList<Actionable> chosenActions = FXCollections.observableArrayList();

    public ObservableList<Actionable> getChosenActions() {
        return FXCollections.unmodifiableObservableList(chosenActions);
    }

    public void add(Actionable action) {
        chosenActions.add(action);
    }

    public Actionable remove(int idx){
        if (idx >= 0 && idx < chosenActions.size()){
            return chosenActions.remove(idx);
        }
        return null;
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
}
