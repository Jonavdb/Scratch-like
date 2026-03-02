package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

public abstract class Turn implements Actionable {
  private final IntegerProperty angle;

  public Turn(int angle) {
    this.angle = new SimpleIntegerProperty(angle);
  }

  public int getAngle(){
    return angle.get();
  }

  public void setAngle(int angle) {
    this.angle.set(angle);
  }

  @Override
  public IntegerProperty getProperty() {
    return angle;
  }

  @Override
  public void execute() {
  }

  @Override
  public String getPrefix() {
    return "";
  }

  @Override
  public Color getColor() {
    return Color.RED;
  }

  @Override
  public abstract Actionable copy();
}
