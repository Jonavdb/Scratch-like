package scratch.view.components;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import scratch.model.*;
import javafx.scene.paint.Color;
import scratch.model.*;

public class DoubleParamView extends VBox {

    private final Actionable action;
    private final Label firstErrorLabel;
    private final Label secondErrorLabel;

    /**
     * Constructeur
     *
     * @param action              L'action
     * @param firstParamProperty  Propriété du 1er paramètre de l'action
     * @param secondParamProperty Propriété du 2eme paramètre de l'action
     * @param prefix1             Texte à afficher avant le premier paramètre
     * @param prefix2             Texte à afficher avant le deuxième paramètre
     * @param enableFirstButtons  True/False: affiche/cache les boutons inc/Dec après le premier paramètre
     * @param enableSecondButtons True/False: affiche/cache les boutons inc/Dec après le deuxième paramètre
     */
    public DoubleParamView(Actionable action,
                           StringProperty firstParamProperty,
                           StringProperty secondParamProperty,
                           String prefix1,
                           String prefix2,
                           boolean enableFirstButtons,
                           boolean enableSecondButtons) {
        super(5);
        this.action = action;

        // on initialise les textes d'erreurs associés aux deux paramètres
        this.firstErrorLabel = createErrorLabel();
        this.secondErrorLabel = createErrorLabel();

        // on initialise les champs de textes associés aux deux paramètres
        TextField firstTextField = new TextField();
        TextField secondTextField = new TextField();

        // on construit le bloc de configuration de chaque paramètre
        Node firstParamBlock = buildParameterBlock(prefix1, firstTextField, firstParamProperty, firstErrorLabel, enableFirstButtons);
        Node secondParamBlock = buildParameterBlock(prefix2, secondTextField, secondParamProperty, secondErrorLabel, enableSecondButtons);

        // on assemble les deux blocs
        HBox mainHBox = new HBox(10);
        mainHBox.setAlignment(Pos.CENTER_LEFT);
        mainHBox.getChildren().addAll(firstParamBlock, secondParamBlock);

        // check validation des paramètres
        setupValidationUI(firstTextField, secondTextField);

        // on met tout dans une Vbox
        this.getChildren().add(mainHBox);
    }

    /*
         Construction layout d'un bloc de paramètres
         [prefix]     [(textField, errorLabel)]    [optionel: boutons +/-)
     */
    private Node buildParameterBlock(String prefixText,
                                     TextField textField,
                                     StringProperty boundProperty,
                                     Label errorLabel,
                                     boolean enableButtons) {
        HBox blockBox = new HBox(5);
        blockBox.setAlignment(Pos.CENTER_LEFT);

        // texte affiché avant le champ de config
        Label prefixLabel = new Label(prefixText);

        // assemblage du textfield du paramètre et de son message d'erreur associé dans une vbox
        textField.setPrefWidth(40);
        textField.setAlignment(Pos.BASELINE_CENTER);
        textField.textProperty().bindBidirectional(boundProperty); // on bind le UI au model
        VBox fieldAndErrorBox = new VBox(textField, errorLabel);
        fieldAndErrorBox.setAlignment(Pos.BOTTOM_CENTER);

        // assemblage du bloc de config (prefix + textfield)
        blockBox.getChildren().addAll(prefixLabel, fieldAndErrorBox);

        // on ajoute +/- boutons si activé
        if (enableButtons) {
            Button btnIncrement = new Button("+");
            Button btnDecrement = new Button("-");

            configureIncrementButtons(boundProperty, btnIncrement, btnDecrement);
            blockBox.getChildren().addAll(btnIncrement, btnDecrement);
        }

        return blockBox;
    }


    private Label createErrorLabel() {
        Label errorLabel = new Label("Erreur valeur");
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false); // pour que quand c'est invisible l'espace se libère
        return errorLabel;
    }

    private void setupValidationUI(TextField firstField, TextField secondField) {
        javafx.beans.value.ChangeListener<String> validationListener = (obs, oldVal, newVal) -> {
            String firstVal = firstField.getText();
            String secondVal = secondField.getText();


            toggleError(firstErrorLabel, !action.isFirstValid(firstVal));
            toggleError(secondErrorLabel, !action.isSecondValid(secondVal));
        };

        // on attache le listener aux deux champs de texte
        firstField.textProperty().addListener(validationListener);
        secondField.textProperty().addListener(validationListener);

        // on active le listener
        validationListener.changed(null, null, null);
    }

    private void toggleError(Label errorLabel, boolean isError) {
        errorLabel.setVisible(isError);
        errorLabel.setManaged(isError);
    }

    /**
     * Configures la logique d'increment/decrement pour un paramètre et cache automatiquement les boutons si variable non litterale
     */
    private void configureIncrementButtons(StringProperty property, Button btnIncrement, Button btnDecrement) {
        // check format texte propriété valide
        BooleanBinding isVariableName = Bindings.createBooleanBinding(() -> {
            String val = property.get();
            return val != null && val.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
        }, property);

        // on bind la visibilité au résultat précédent
        btnIncrement.visibleProperty().bind(isVariableName.not());
        btnIncrement.managedProperty().bind(isVariableName.not());

        btnDecrement.visibleProperty().bind(isVariableName.not());
        btnDecrement.managedProperty().bind(isVariableName.not());

        // Setup button actions with try-catch fallbacks
        btnIncrement.setOnAction(e -> {
            try {
                int val = Integer.parseInt(property.get());
                property.set(String.valueOf(val + 1));
            } catch (NumberFormatException ex) {
                property.set("1");
            }
        });

        btnDecrement.setOnAction(e -> {
            try {
                int val = Integer.parseInt(property.get());
                property.set(String.valueOf(val - 1));
            } catch (NumberFormatException ex) {
                property.set("0");
            }
        });
    }
}