package scratch.model;

public class Move implements Actionable{

    private final String name;

    public Move(String name){
        this.name = name;
    }
    @Override
    public void execute() {

    }

    @Override
    public Actionable copy() {
        return new Move(this.name);
    }

    public String toString() {
        return name;
    }
}
