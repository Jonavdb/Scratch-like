package scratch.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import scratch.model.Actionable;

public class ActionPaletteView extends VBox {
  private final ListView<Actionable> availableActions = new ListView<>();
  private final Button btnAdd = new Button("Ajouter au programme");

  public ActionPaletteView() {
    this.setSpacing(10);
    this.setPadding(new Insets(0, 10, 0, 0));
    this.getChildren().addAll(new Text("Palette d'actions"), availableActions, btnAdd);
    HBox.setHgrow(this, Priority.ALWAYS);
  }

  public ListView<Actionable> getAvailableActions() {
    return availableActions;
  }

  public Button getBtnAdd() {
    return btnAdd;
  }
}
