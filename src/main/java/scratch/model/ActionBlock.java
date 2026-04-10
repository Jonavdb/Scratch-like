package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class ActionBlock implements Actionable {
  private final StringProperty blockName; // "REPEAT", "IF", "WHILE", etc.
  private final List<Actionable> childrenActions;

  public ActionBlock(String name) {
    this.blockName = new SimpleStringProperty(name);
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
  public void execute(ProgramState state) {
    for(Actionable action : childrenActions) {
      action.execute(state);
    }
  }

  @Override
  public Actionable copy() {
    // copie du block + de chaque enfant à l'intérieur
    ActionBlock copyBlock = new ActionBlock(getBlockName());
    for(Actionable action : childrenActions) {
      copyBlock.addAction(action.copy());
    }
    return copyBlock;
  }

  @Override
  public StringProperty getProperty() {
    return null; // les blocs ne sont pas liés à un seul champ texte
  }

  @Override
  public String toString() {
    return getBlockName();
  }

  @Override
  public Color getColor() {
    return Color.TURQUOISE;
  }

  @Override
  // toSave() modulable au cas où d'autres ActionBlocs arriveraient plus tard
  public String toSave() {
    StringBuilder sb = new StringBuilder();

    sb.append(blockName.get()).append(";");
    if(getProperty() != null && !getProperty().get().isEmpty()) {
      sb.append(getProperty().get());
    }
    sb.append("\n");

    for(Actionable action : childrenActions) {
      sb.append(action.toSave()).append("\n");
    }

    // à voir, peut-être à changer avec le format demandé
    sb.append("END_").append(blockName.get()).append(";");
    return sb.toString();
  }

  @Override
  public boolean isValid(String... params) {
    // params peuvent servir plus tard pour REPEAT, IF, etc.
    // Exemples possibles :
    // - REPEAT : params[0] = "10"
    // - IF : params[0] = condition, etc.

    // Pour un bloc générique : il doit au minimum avoir des enfants valides
    if(childrenActions == null) {
      return false;
    }

    for(Actionable action : childrenActions) {
      if(action == null) return false;
      if(!action.isValid()) return false;

      // TODO: un bloc vide est valide ou pas?
      /*
        if (childrenActions.isEmpty()) {
            return false;
        }
        */

    }
    return true;
  }

  // Getters et setters
  public String getBlockName() {
    return blockName.get();
  }

  public StringProperty blockNameProperty() {
    return blockName;
  }

  public void setBlockName(String name) {
    blockName.set(name);
  }
}