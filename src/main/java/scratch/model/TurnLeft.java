package scratch.model;

public class TurnLeft extends Turn {
    public TurnLeft(String name, String angle) {
      super(name, angle);
    }

    public void execute(ProgramState state) {
        String angleText = this.getProperty().get();
        int turnValue = 0;

        var player = state.getPlayer();
        var map = state.getVariableMap();
        boolean isLiteral = map.tryGetLiteral(angleText).isPresent();

        turnValue = isLiteral ? Integer.parseInt(angleText) : map.getValue(angleText);

        if(turnValue < 1 || turnValue > 180) return;

        player.setAngle(player.getAngle() + turnValue);
    }


    @Override
    public Actionable copy() {
        return new TurnLeft(this.toString(), this.getAngle());
    }

    @Override
    public String toSave() {
        return "TURN_LEFT;" + getAngle();
    }
}
