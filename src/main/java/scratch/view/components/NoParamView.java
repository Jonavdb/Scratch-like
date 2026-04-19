package scratch.view.components;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class NoParamView extends VBox {

  public NoParamView(String labelText) {
    this.getChildren().add(new Label(labelText));
  }
}
