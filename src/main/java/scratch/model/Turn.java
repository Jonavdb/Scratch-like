package scratch.model;

public class Turn implements Actionable{

    private final String name;

    public Turn(String name) {
        this.name = name;
    }

    @Override
    public void execute() {

    }

    @Override
    public Actionable copy() {
        return new Turn(this.name);
    }

    public String toString() {
        return name;
    }
}
