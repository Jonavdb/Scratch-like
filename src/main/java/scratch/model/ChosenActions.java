package scratch.model;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
}
