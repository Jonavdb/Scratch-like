package scratch.view.components;

import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scratch.model.*;

public class SingleParamView extends VBox {
  public SingleParamView(String labelText, Actionable action, StringProperty property, Label errorLabel, String suffix) {
    super(5);

    Label label = new Label(labelText);
    Label suffixLabel = new Label(suffix);
    TextField textField = new TextField();
    textField.setPrefWidth(50);
    textField.setText(property.get());


    setupValidationUI(textField, action, errorLabel, property);

    HBox hbox = new HBox(10, label, textField);
    hbox.setAlignment(Pos.CENTER_LEFT);

    if(action instanceof Repeat) {
        Button btnIncrement = new Button("+");
        Button btnDecrement = new Button("-");

        configureIncrementButtons(action, property, btnIncrement, btnDecrement);
        hbox.getChildren().addAll(btnIncrement, btnDecrement);
    }

    if (suffix != null && !suffix.isEmpty()){
        hbox.getChildren().add(suffixLabel);
    }

    this.getChildren().addAll(hbox, errorLabel);
  }

  private void setupValidationUI(TextField textField, Actionable action, Label errorLabel, StringProperty property) {
    boolean isInitialValid = Actions.isValueValid(action, textField.getText());

    errorLabel.setVisible(!isInitialValid);
    errorLabel.setManaged(!isInitialValid);

    textField.textProperty().addListener((obs, oldVal, newVal) -> {
      boolean isValid = Actions.isValueValid(action, newVal);
      errorLabel.setVisible(!isValid);
      errorLabel.setManaged(!isValid);
      if (isValid){
          property.set(newVal);
      }
    });
      property.addListener((obs, oldVal, newVal) -> {
          if (newVal != null && !newVal.equals(textField.getText())) {
              textField.setText(newVal);
          }
      });
  }

    private void configureIncrementButtons(Actionable action, StringProperty propValue, Button btnIncrement, Button btnDecrement) {
        btnIncrement.setOnAction(e -> {
            try {
                int val = Integer.parseInt(propValue.get());
                propValue.set(String.valueOf(val + 1));
            } catch (NumberFormatException ex) {
                propValue.set("1");
            }
        });
        btnDecrement.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            try {
                int val = Integer.parseInt(propValue.get());
                return val <= 1;
            } catch (NumberFormatException e) {
                return true;
            }
        }, propValue));

        btnDecrement.setOnAction(e -> {
            try {
                int val = Integer.parseInt(propValue.get());
                propValue.set(String.valueOf(val - 1));
            } catch (NumberFormatException ex) {
                propValue.set("0");
            }
        });
    }
}
