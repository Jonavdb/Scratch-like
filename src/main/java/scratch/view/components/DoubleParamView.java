package scratch.view.components;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scratch.model.*;

public class DoubleParamView extends VBox {
  public DoubleParamView(String paramName, String paramValue, Actionable action,
                         StringProperty propName, StringProperty propValue,
                         Label errorLabel) {
    super(5);

    Label label = new Label(paramName);
    Label labelValue = new Label(paramValue);
    TextField textField = new TextField();
    TextField textValue = new TextField();
    textField.setPrefWidth(40);
    textValue.setPrefWidth(40);
    textField.setAlignment(Pos.BASELINE_CENTER);
    textValue.setAlignment(Pos.BASELINE_CENTER);

    textField.textProperty().bindBidirectional(propName);
    textValue.textProperty().bindBidirectional(propValue);

    setupDoubleValidationUI(textField, textValue, action, errorLabel);

    HBox hbox = new HBox(10, label, textField, labelValue, textValue);
    hbox.setAlignment(Pos.CENTER_LEFT);

    // affichage dynamique des boutons "+" et "-" uniquement si on est en Increment ou Assign
    if(action.getType() == ActionType.INCREMENT || action.getType() == ActionType.ASSIGN) {
      Button btnIncrement = new Button("+");
      Button btnDecrement = new Button("-");

      configureIncrementButtons(action, propValue, btnIncrement, btnDecrement);
      hbox.getChildren().addAll(btnIncrement, btnDecrement);
    }

    this.getChildren().addAll(hbox, errorLabel);
  }

  private void setupDoubleValidationUI(TextField firstField, TextField secondField, Actionable action, Label errorLabel) {
    javafx.beans.value.ChangeListener<String> validationListener = (obs, oldVal, newVal) -> {
      String firstFieldText = firstField.getText();
      String secondFieldText = secondField.getText();

      boolean isValid = action.isValid(firstFieldText, secondFieldText);

      errorLabel.setVisible(!isValid);
      errorLabel.setManaged(!isValid);
    };

    firstField.textProperty().addListener(validationListener);
    secondField.textProperty().addListener(validationListener);

    validationListener.changed(null, null, null);
  }

  private void configureIncrementButtons(Actionable action, StringProperty propValue, Button btnIncrement, Button btnDecrement) {
    BooleanBinding isNumber = Bindings.createBooleanBinding(() ->
            propValue.get().matches("^[a-zA-Z_][a-zA-Z0-9_]*$"), propValue
    );

    // Btn increment
    btnIncrement.setOnAction(e -> {
      try {
        int val = Integer.parseInt(propValue.get());
        propValue.set(String.valueOf(val + 1));
      } catch(NumberFormatException ex) {
        propValue.set("1");
      }
    });

    btnIncrement.visibleProperty().bind(isNumber.not());
    btnIncrement.managedProperty().bind(isNumber.not());

    // Btn decrement
    btnDecrement.setOnAction(e -> {
      try {
        int val = Integer.parseInt(propValue.get());
        propValue.set(String.valueOf(val - 1));
      } catch(NumberFormatException ex) {
        propValue.set("0");
      }
    });

    btnDecrement.visibleProperty().bind(isNumber.not());
    btnIncrement.managedProperty().bind(isNumber.not());
  }
}