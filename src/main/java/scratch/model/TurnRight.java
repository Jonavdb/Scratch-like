package scratch.model;

public class TurnRight extends Turn {
    public TurnRight(String name, String angle) {
        super(name, angle);
    }

    public void execute(ProgramState state) {
        String angleText = this.getAngle();
        VariableMap map = state.getVariableMap();
        int turnValue = map.getParamValue(angleText).orElseGet(() -> map.getValue(angleText));

        if(turnValue < 1 || turnValue > 180) {
            throw new RuntimeException("Erreur : Impossible de tourner de " + turnValue + " (limite : 1 à 180).");
        }

        state.getPlayer().turnRight(turnValue);
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
    public boolean isValid(String... params) {
        String angleText = params.length > 0 ? params[0] : getAngle();
        if (angleText == null || angleText.isEmpty()) return false;

        // Essayer de parser comme entier
        try {
            int angle = Integer.parseInt(angleText);
            return angle >= 1 && angle <= 180;
        } catch (NumberFormatException e) {
            // Sinon, on suppose que c’est une variable, donc on vérifie seulement la syntaxe
            return isValidVariableName(angleText);
        }
    }
}
