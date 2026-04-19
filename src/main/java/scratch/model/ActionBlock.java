package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class ActionBlock implements Actionable {
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

  // on oblige les enfants à savoir créer une version vide d'eux-mêmes
  protected abstract ActionBlock createEmptyCopy();

  // le parent s'occupe de faire la copie des enfants pour tout le monde
  @Override
  public Actionable copy() {
    ActionBlock copyBlock = this.createEmptyCopy();  // demande à l'enfant (Repeat, If, etc.) de nous donner une coquille vide

    // le parent fait le travail répétitif
    for(Actionable action : this.childrenActions) {
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

  @Override
  public ActionType getType() {
    return ActionType.BLOCK;
  }

  public StringProperty blockNameProperty() {
    return blockName;
  }

  public void setBlockName(String name) {
    blockName.set(name);
  }
}