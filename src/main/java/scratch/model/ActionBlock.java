package scratch.model;

import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class ActionBlock implements Actionable {
  private final String name;
  private final List<Actionable> childrenActions;

  public ActionBlock(String name) {
    this.name = name;
    this.childrenActions = new ArrayList<>();
  }

  // Méthodes du Composite
  public void addAction(Actionable action) {
    childrenActions.add(action);
  }

  public void removeAction(Actionable action) {
    childrenActions.remove(action);
  }

  public List<Actionable> getChildren() {
    return childrenActions;
  }

  // Méthodes de l'interface
  @Override
  public void execute(Player player) {
    for(Actionable action : childrenActions) {
      action.execute(player);
    }
  }

  @Override
  public Actionable copy() {
    // copie du block + de chaque enfant à l'intérieur
    ActionBlock copyBlock = new ActionBlock(this.name);
    for(Actionable action : childrenActions) {
      copyBlock.addAction(action.copy());
    }
    return copyBlock;
  }

  @Override
  public StringProperty getProperty() {
    return null;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public Color getColor() {
    return Color.TURQUOISE;
  }

  @Override
  public String toSave() {
    StringBuilder sb = new StringBuilder();
    sb.append("REPEAT;").append(this.getProperty().get()).append("\n"); // ex: REPEAT;4

    for (Actionable action : this.getChildren()) {
      sb.append(action.toSave()).append("\n");
    }

    sb.append("END_REPEAT;");
    return sb.toString();
  }
}
