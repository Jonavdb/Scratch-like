package scratch.model;

/*
    Cette classe contient les nouvelles données liées à l'assignation
*/
public class MapChangeObject {
    String variable;
    Integer value;
    ActionType type;

    public MapChangeObject(String variable, Integer value, ActionType type) {
        this.variable = variable;
        this.value = value;
        this.type = type;
    }

    public String getVariable() {return variable;}
    public Integer getValue() {return value;}
    public ActionType getActionType() {return type;}
}

