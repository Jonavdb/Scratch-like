package scratch.model;

public class ProgramState {
    private final Player player;
    private final VariableMap map;

    public ProgramState(Player player, VariableMap map) {
        this.player = player;
        this.map = map;
    }

    public Player getPlayer() {
        return player;
    }

    public VariableMap getVariableMap() {
        return map;
    }
}
