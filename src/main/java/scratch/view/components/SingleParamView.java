package scratch.view.components;

import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scratch.model.Actionable;
import scratch.model.Actions;

public class SingleParamView extends VBox {
  public SingleParamView(String labelText, Actionable action, StringProperty property, Label errorLabel) {
    super(5);

    Label label = new Label(labelText);
    TextField textField = new TextField();
    textField.setPrefWidth(50);
    textField.textProperty().bindBidirectional(property);

    setupValidationUI(textField, action, errorLabel);

    HBox hbox = new HBox(10, label, textField);
    hbox.setAlignment(Pos.CENTER_LEFT);
  }

  private void setupValidationUI(TextField textField, Actionable action, Label errorLabel) {
    boolean isInitialValid = Actions.isValueValid(action, textField.getText());

    errorLabel.setVisible(!isInitialValid);
    errorLabel.setManaged(!isInitialValid);

    textField.textProperty().addListener((obs, oldVal, newVal) -> {
      boolean isValid = Actions.isValueValid(action, newVal);
      errorLabel.setVisible(!isValid);
      errorLabel.setManaged(!isValid);
    });
  }
}
