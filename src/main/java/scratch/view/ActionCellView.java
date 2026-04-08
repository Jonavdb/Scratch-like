package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import scratch.model.*;
import scratch.viewmodel.MainViewModel;

public class ActionCellView extends ListCell<Actionable> {
  private final MainViewModel vm;

  public ActionCellView(MainViewModel viewModel) {
    this.vm = viewModel;
  }

  @Override
  protected void updateItem(Actionable item, boolean empty) {
    super.updateItem(item, empty);

    // nettoyage systématique (Recyclage)
    resetCell();

    if (empty || item == null) {
      return;
    }

    // logique visuelle métier
    applyIndentation(item);
    applyErrorStyle();
    applyContent(item);
  }

  private void resetCell() {
    styleProperty().unbind();
    setStyle(null);
    textProperty().unbind();
    setText(null);
    setGraphic(null);
    setPadding(new Insets(3, 5, 3, 5));
  }

  // Indentation pour les boucles Repeat
  private void applyIndentation(Actionable item) {
    int gap = 0;
    int currentIndex = getIndex();

    if (currentIndex >= 0 && getListView() != null) {
      var allItems = getListView().getItems();
      for (int i = 0; i < currentIndex; i++) {
        Actionable pastItem = allItems.get(i);
        if (pastItem instanceof Repeat) {
          gap++;
        } else if (pastItem instanceof EndRepeat) {
          gap--;
        }
      }
    }

    if (item instanceof EndRepeat) {
      gap--;
    }

    double leftPadding = 5 + (gap * 20);
    setPadding(new Insets(3, 5, 3, leftPadding));
  }

  // Cadre rouge en cas d'erreur Runtime
  private void applyErrorStyle() {
    int currentIndex = getIndex();
    styleProperty().bind(Bindings.createStringBinding(() -> {
      if (currentIndex == vm.errorActionIndexProperty().get()) {
        return "-fx-border-color: red;";
      }
      return "";
    }, vm.errorActionIndexProperty()));
  }

  // Cercle de couleur
  private void applyContent(Actionable item) {
    Color color = item.getColor();
    setGraphic(new Circle(5, color));
    setTextFill(color);

    // le switch-case
    bindTextLogic(item);
  }

  // Switch-Case qui bind la vue à la logique
  private void bindTextLogic(Actionable item) {
    switch(item) {
      case DeclareVariable dv -> textProperty().bind(Bindings.createStringBinding(
              () -> dv.toString() + " " + dv.getProperty().get(), dv.getProperty()));

      case AssignVariable av -> textProperty().bind(Bindings.createStringBinding(
              () -> {
                String varName = av.getProperty().get();
                String varValue = av.getParamProperty().get();
                if(varName == null || varName.trim().isEmpty()) return av.toString();
                return av.toString() + " : " + varName + " = " + (varValue != null ? varValue : "");
              }, av.getProperty(), av.getParamProperty()));

      case Increment inc -> textProperty().bind(Bindings.createStringBinding(
              () -> inc.toString() + " " + inc.getProperty().get() + " de " + inc.getIncrementProperty().get(),
              inc.getProperty(), inc.getIncrementProperty()));

      case Move move -> textProperty().bind(Bindings.createStringBinding(
              () -> move.toString() + " " + move.getProperty().get(), move.getProperty()));

      case TurnLeft tl -> textProperty().bind(Bindings.createStringBinding(
              () -> tl.toString() + " " + tl.getProperty().get(), tl.getProperty()));

      case TurnRight tr -> textProperty().bind(Bindings.createStringBinding(
              () -> tr.toString() + " " + tr.getProperty().get(), tr.getProperty()));

      case Repeat r -> textProperty().bind(Bindings.createStringBinding(
              () -> r.toString() + " " + r.getProperty().get(), r.getProperty()));

      case EndRepeat endRe -> setText(endRe.toString());
      case Up up -> setText(up.toString());
      case Down down -> setText(down.toString());

      default -> {
        if(item.getProperty() != null) {
          textProperty().bind(Bindings.createStringBinding(() -> item.getProperty() + ""));
        } else {
          setText(item.toString());
        }
      }
    }
  }
}
