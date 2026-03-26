package scratch.model;

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

  public abstract void execute(ProgramState state);
  public abstract Actionable copy();

  public String getAngle(){
    return angle.get();
  }

  public String toString() {
    return name;
  }

  public void setAngle(String newAngle) {
    angle.set(newAngle);
  }

  public StringProperty getFirstProperty() {
    return angle;
  }

  public Color getColor() {
    return Color.RED;
  }

}
