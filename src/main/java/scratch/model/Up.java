package scratch.model;

public class Up implements Actionable{

    private final String name;

    public Up(String name) {
        this.name = name;
    }

    @Override
    public void execute() {

    }

    @Override
    public Actionable copy() {
        return new Up(this.name);
    }

    public String toString() {
        return name;
    }
}
