package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public abstract class Turn implements Actionable {
  private final String name;
  private final StringProperty angle;

  public Turn(String name, String angle) {
    this.name = name;
    this.angle = new SimpleStringProperty(angle);
  }

  public abstract String getPrefix();
  public abstract Actionable copy();

  public void execute(Player player) {
    String angleText = this.getProperty().get();
    int turnValue = 0;

    try {
      turnValue = Integer.parseInt(angleText);
    } catch (NumberFormatException e) {
      return;
    }

    player.setAngle(player.getAngle() + turnValue);
  }

  public String getAngle(){
    return angle.get();
  }

  public String getName() {
    return name;
  }

  public void setAngle(String newAngle) {
    angle.set(newAngle);
  }

  public String getSuffix() {
    return " degrés";
  }

  public StringProperty getProperty() {
    return angle;
  }

  public Color getColor() {
    return Color.RED;
  }

  public String toString() {
    return name + " " + getAngle();
  }
}
