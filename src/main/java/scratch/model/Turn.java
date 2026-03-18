package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

public abstract class Turn implements Actionable {
  private final String name;
  private final IntegerProperty angle;

  public Turn(String name, int angle) {
    this.name = name;
    this.angle = new SimpleIntegerProperty(angle);
  }

  public abstract void execute(Player player);
  public abstract String getPrefix();
  public abstract Actionable copy();

  public int getAngle(){
    return angle.get();
  }

  public String getName() {
    return name;
  }

  public void setAngle(int newAngle) {
    angle.set(newAngle);
  }

  public String getSuffix() {
    return " degrés";
  }

  public IntegerProperty getProperty() {
    return angle;
  }

  public Color getColor() {
    return Color.RED;
  }

  public String toString() {
    return name + " " + getAngle();
  }
}
