package scratch.model;

public class TurnRight extends Turn {
    public TurnRight(String name, String angle) {
        super(name, angle);
    }

    public void execute(ProgramState state) {
        String angleText = this.getProperty().get();
        int turnValue = 0;

        var player = state.getPlayer();
        var map = state.getVariableMap();
        boolean isLiteral = map.getParamValue(angleText).isPresent();

        turnValue = isLiteral ? Integer.parseInt(angleText) : map.getValue(angleText);

        if(turnValue < 1 || turnValue > 180) {
            throw new RuntimeException("Erreur : Impossible de tourner de " + turnValue + " (limite : 1 à 180).");
        }

        player.setAngle(player.getAngle() - turnValue);
    }

    @Override
    public Actionable copy() {
        return new TurnRight(this.toString(), this.getProperty().get());
    }

    @Override
    public String toSave() {
        return "TURN_RIGHT;" + getAngle();
    }

    @Override
    public ActionType getType() {
        return ActionType.TURN_RIGHT;
    }
}
