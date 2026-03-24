package scratch.model;

public class TurnLeft extends Turn {
    public TurnLeft(String name, String angle) {
      super(name, angle);
    }

    public void execute(Player player) {
        String angleText = this.getProperty().get();
        int turnValue = 0;

        try {
            turnValue = Integer.parseInt(angleText);
        } catch (NumberFormatException e) {
            // c'est PAS un chiffre, donc c'est le nom d'une variale (ex: 'val', 'nb', ...)
            turnValue = player.getVariableValue(angleText);
        }

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
