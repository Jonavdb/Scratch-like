package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import scratch.model.*;
import scratch.viewmodel.MainViewModel;
import javafx.scene.control.Slider;
import java.io.File;

public class MainView extends VBox {
  private final MainViewModel vm;
  private IntegerProperty currentBoundProperty = null;
  private PlayerView playerView;
  private Pane spritePane = new Pane();

  // Conteneur Principal
  private final HBox appContainer = new HBox();

  // MenuBar
  private final MenuBar menuBar = new MenuBar();
  private final Menu fileMenu = new Menu("File");
  private final MenuItem saveItem = new MenuItem("Save");
  private final MenuItem loadItem = new MenuItem("Load");

  // Palette d'actions (gauche)
  private final VBox paletteBox = new VBox();
  private final ListView<Actionable> availableActions = new ListView<>();
  private final Button btnAdd = new Button("Ajouter au programme");

  // Programme (centre)
  private final VBox programBox = new VBox();
  private final ListView<Actionable> chosenActions = new ListView<>();
  private final Button btnUp = new Button("Monter");
  private final Button btnDown = new Button("Descendre");
  private final Button btnDuplicate = new Button("Dupliquer");
  private final Button btnDelete = new Button("Supprimer");
  private final Button btnEmptyAll = new Button("Vider tout");

  // Action détails (sous le programme)
  private final VBox detailContentContainer = new VBox(5);
  private final TitledPane detailPane = new TitledPane("Détail de l'action", detailContentContainer);

  // Scene (droite)
  private final VBox sceneBox = new VBox();
  private final Button btnLoad = new Button("Charger");
  private final Button btnExecute = new Button("Exécuter");
  private final Button btnStop = new Button("Arreter");
  private final Slider slider = new Slider(0.01, 5, 0.01);
  private final ToggleGroup groupOfExecute = new ToggleGroup();
  private final RadioButton automatic_execute = new RadioButton("Exécution automatique");
  private final RadioButton step_execute = new RadioButton("Exécution manuelle");

  public MainView(MainViewModel vm) {
    this.vm = vm;

    // Mise en place de l'UI
    configureMenu();
    configurePalette();
    configureProgram();
    configureScene();
    configureLayoutGlobal();
    this.getChildren().addAll(menuBar, appContainer);

    // Branchement de la logique
    setupBindings();
  }

  // =========================================================================
  // LAYOUTS
  // =========================================================================
  private void configureMenu() {
    fileMenu.getItems().addAll(saveItem, loadItem);
    menuBar.getMenus().add(fileMenu);
  }

  private void configurePalette() {
    paletteBox.setSpacing(10);
    paletteBox.setPadding(new Insets(0, 10, 0, 0));
    paletteBox.getChildren().addAll(new Text("Palette d'actions"), availableActions, btnAdd);
    HBox.setHgrow(paletteBox, Priority.ALWAYS);
  }

  private void configureProgram() {
    HBox btnContainerProgram = new HBox(10, btnUp, btnDown, btnDuplicate, btnDelete, btnEmptyAll);
    btnContainerProgram.setPadding(new Insets(10, 0, 10, 0));

    // Programme
    programBox.setSpacing(10);
    programBox.setPadding(new Insets(0, 0, 0, 10));

    // Détail de l'action
    programBox.getChildren().addAll(new Text("Programme"), chosenActions, btnContainerProgram, detailPane);
    HBox.setHgrow(programBox, Priority.ALWAYS);
  }

  private void configureScene() {
    GridPane gridPane = createGrid(10, 10);
    spritePane = createSpritePane();
    Group board = new Group(gridPane, spritePane);

    HBox btnContainerScene = new HBox(10, btnLoad, btnExecute, btnStop);
    btnContainerScene.setAlignment(Pos.CENTER);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setMajorTickUnit(1);
    slider.setBlockIncrement(0.1f);
    slider.setMaxWidth(Double.MAX_VALUE);
    sceneBox.setSpacing(10);
    sceneBox.setPadding(new Insets(0, 0, 0, 20));
    VBox.setMargin(slider, new Insets(0, 5, 0, 5));
    automatic_execute.setToggleGroup(groupOfExecute);
    automatic_execute.setSelected(true);
    step_execute.setToggleGroup(groupOfExecute);
    HBox btnExecuteContainerScene = new HBox(10, automatic_execute, step_execute);
    btnExecuteContainerScene.setAlignment(Pos.CENTER);
    sceneBox.getChildren().addAll(new Text("Scene"), board, btnExecuteContainerScene, btnContainerScene, slider);
  }

  private void configureLayoutGlobal() {
    appContainer.setPadding(new Insets(5, 20, 10, 20));
    appContainer.getChildren().addAll(paletteBox, programBox, sceneBox);
    this.setSpacing(20);
    VBox.setVgrow(appContainer, Priority.ALWAYS);
  }

  private void configureIncrementButtons() {

  }

  private void createSingleParamUI(String labelText, Actionable action, StringProperty property, Label errorLabel) {
    Label label = new Label(labelText);
    TextField textField = new TextField();
    textField.setPrefWidth(50);
    textField.textProperty().bindBidirectional(property);

    setupValidationUI(textField, action, errorLabel);

    HBox hbox = new HBox(10, label, textField);
    hbox.setAlignment(Pos.CENTER_LEFT);
    detailContentContainer.getChildren().addAll(hbox, errorLabel);
  }

  // Affichage dynamique des boutons "+" et "-" uniquement si on est en Increment ou Assign
  public void configureIncrementButtons(Actionable action, StringProperty property, Button btnIncrement, Button btnDecrement) {
    var isNumericBinding = Bindings.createBooleanBinding(() -> {
      String val = property.get();
      if(val == null || val.trim().isEmpty()) return false;

      try {
        Integer.parseInt(val.trim()); // c'est un chiffre
        return true;
      } catch(NumberFormatException e) {
        return false;   // c'est pas un chiffre
      }
    }, property);

    btnIncrement.visibleProperty().bind(isNumericBinding);
    btnIncrement.managedProperty().bind(isNumericBinding);
    btnDecrement.visibleProperty().bind(isNumericBinding);
    btnDecrement.managedProperty().bind(isNumericBinding);

    btnIncrement.setOnAction(e -> {
      int current = Integer.parseInt(property.get().trim());
      property.set(String.valueOf(current + 1));
    });

    btnDecrement.setOnAction(e -> {
      int current = Integer.parseInt(property.get().trim());
      property.set(String.valueOf(current - 1));
    });
  }

  private void createDoubleParamUI(String paramName, String paramValue, Actionable action,
                                   StringProperty propName, StringProperty propValue,
                                   Label errorLabel) {
    Label label = new Label(paramName);
    Label labelValue = new Label(paramValue);
    TextField textField = new TextField();
    TextField textValue = new TextField();
    textField.setPrefWidth(40);
    textValue.setPrefWidth(40);

    textField.textProperty().bindBidirectional(propName);
    textValue.textProperty().bindBidirectional(propValue);

    setupValidationUI(textField, action, errorLabel);

    HBox hbox = new HBox(10, label, textField, labelValue, textValue);
    hbox.setAlignment(Pos.CENTER_LEFT);

    // Affichage dynamique des boutons "+" et "-" uniquement si on est en Increment ou Assign
    if(action instanceof Increment || action instanceof AssignVariable) {
      Button btnIncrement = new Button("+");
      Button btnDecrement = new Button("-");

      configureIncrementButtons(action, propValue, btnIncrement, btnDecrement);
      hbox.getChildren().addAll(btnIncrement, btnDecrement);
    }

    detailContentContainer.getChildren().addAll(hbox, errorLabel);
  }

  private void createNoParamUI(String labelText) {
    detailContentContainer.getChildren().add(new Label(labelText));
  }

  private void updateDetailContainer(Actionable chosenAction) {
    detailContentContainer.getChildren().clear();

    if(chosenAction == null) {
      detailContentContainer.getChildren().add(new Label("(aucune action sélectionnée)"));
      return;
    }

    Label errorLabel = new Label("Erreur valeur");
    errorLabel.setTextFill(Color.RED);

    switch(chosenAction) {
      // Actions à 1 paramètre
      case DeclareVariable dv -> createSingleParamUI("Déclaration de la variable ", dv, dv.getProperty(), errorLabel);
      case Move move -> createSingleParamUI("Avancer de ", move, move.getProperty(), errorLabel);
      case TurnLeft tl -> createSingleParamUI("Tourner à gauche de ", tl, tl.getProperty(), errorLabel);
      case TurnRight tr -> createSingleParamUI("Tourner à droite de ", tr, tr.getProperty(), errorLabel);
      case Repeat r -> createSingleParamUI("Répéter ", r, r.getProperty(), errorLabel);

      // Actions à 2 paramètres
      case AssignVariable av ->
              createDoubleParamUI("Assignation de la variable ", " valeur : ", av, av.getProperty(), av.getParamProperty(), errorLabel);
      case Increment inc ->
              createDoubleParamUI("Incrémentation de la variable ", " de ", inc, inc.getProperty(), inc.getIncrementProperty(), errorLabel);

      // Actions sans paramètre
      case Up up -> createNoParamUI("Lever stylo");
      case Down down -> createNoParamUI("Abaisser stylo");
      case EndRepeat endRe -> createNoParamUI("Arreter répétition");

      default -> detailContentContainer.getChildren().add(new Label("Détail de : " + chosenAction.getProperty()));
    }
  }

  // Méthode pour générer des cellules personnalisées (cercle de couleur + texte) dans la colonne de GAUCHE
  private Callback<ListView<Actionable>, ListCell<Actionable>> createPaletteCellFactory() {
    return listView -> new ListCell<>() {
      @Override
      protected void updateItem(Actionable item, boolean empty) {
        super.updateItem(item, empty);
        textProperty().unbind(); // Sécurité

        if(empty || item == null) {
          setText(null);
          setGraphic(null);
          return;
        }

        // pour la palette, on affiche JUSTE le nom pur (ex: "Inc/Dec variable")
        setText(item.toString());

        Color color = item.getColor();
        Circle circle = new Circle(5, color);
        setTextFill(color);
        setGraphic(circle);
      }
    };
  }

  // Méthode pour générer des cellules personnalisées (cercle de couleur + texte) dans la colonne du MILIEU
  private Callback<ListView<Actionable>, ListCell<Actionable>> createActionCellFactory() {
    return actionableListView -> new ListCell<Actionable>() {
      @Override
      protected void updateItem(Actionable item, boolean empty) {
        super.updateItem(item, empty);
        textProperty().unbind();  // Coupe l'ancien binding car les cellules sont recyclées

        // Pas d'action à afficher
        if(empty || item == null) {
          setText(null);
          setGraphic(null);
          return;
        }

        // Verifier le type d'action
        switch(item) {
          case DeclareVariable declareVariable -> {
            textProperty().bind(Bindings.createStringBinding(
                    () -> declareVariable.toString() + " " + declareVariable.getProperty().get(),
                    declareVariable.getProperty()
            ));
          }
          case AssignVariable assignVariable -> {
            textProperty().bind(Bindings.createStringBinding(
                    () -> {
                      String varName = assignVariable.getProperty().get();
                      String varValue = assignVariable.getParamProperty().get();

                      // Si le nom de la variable est vide (ou null), on n'affiche que "Assignation"
                      if(varName == null || varName.trim().isEmpty()) {
                        return assignVariable.toString();
                      }

                      // Sinon, on affiche la formule complète "Assignation : nb = 15"
                      return assignVariable.toString() + " : " + varName + " = " + (varValue != null ? varValue : "");
                    },
                    assignVariable.getProperty(),
                    assignVariable.getParamProperty()
            ));
          }
          case Increment increment -> {
            textProperty().bind(Bindings.createStringBinding(
                    () -> increment.toString() + " " + increment.getProperty().get()
                            + " de " + increment.getIncrementProperty().get(),
                    increment.getProperty(),
                    increment.getIncrementProperty()
            ));
          }
          case Move move -> {
            textProperty().bind(Bindings.createStringBinding(
                    () -> move.toString() + " " + move.getProperty().get(),
                    move.getProperty()
            ));
          }
          case TurnLeft turnLeft -> {
            textProperty().bind(Bindings.createStringBinding(
                    () -> turnLeft.toString() + " " + turnLeft.getProperty().get(),
                    turnLeft.getProperty()
            ));
          }
          case TurnRight turnRight -> {
            textProperty().bind(Bindings.createStringBinding(
                    () -> turnRight.toString() + " " + turnRight.getProperty().get(),
                    turnRight.getProperty()
            ));
          }
          case Repeat repeat -> {
            textProperty().bind(Bindings.createStringBinding(
                    () -> repeat.toString() + " " + repeat.getProperty().get(),
                    repeat.getProperty()
            ));
          }
          case EndRepeat endRepeat -> {
            setText(endRepeat.toString());
          }
          case Up up -> {
            setText(up.toString());
          }
          case Down down -> {
            setText(down.toString());
          }
          default -> {
            // Si l'action possède une propriété modifiable (ex: un nouveau type de bloc le jour de l'exam)
            if(item.getProperty() != null) {
              textProperty().bind(Bindings.createStringBinding(
                      () -> item.getProperty() + ""
              ));
            } else {  // Si c'est une action simple sans paramètre
              setText(item.toString());
            }
          }
        }

        Color color = item.getColor();
        Circle circle = new Circle(5, color);

        setTextFill(color);
        setGraphic(circle);
      }
    };
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

  // =========================================================================
  // BINDINGS (MVVM) && UTILS
  // =========================================================================
  private void setupBindings() {
    bindLists();
    bindButtons();
    bindMenuActions();
    vm.currActionIndexProperty().addListener((obs, oldVal, newVal) -> {
      chosenActions.getSelectionModel().select(newVal.intValue());
    });
  }

  private void bindLists() {
    // Remplissage
    availableActions.setItems(vm.getAllAction());
    chosenActions.setItems(vm.getChosenAction());

    availableActions.setCellFactory(createPaletteCellFactory());
    chosenActions.setCellFactory(createActionCellFactory());

    // Synchronisation des sélections avec le VM
    vm.selectedActionProperty().bind(availableActions.getSelectionModel().selectedItemProperty());

    chosenActions.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
      if(newVal != null && newVal.intValue() >= 0) {
        vm.setIdChosenAction(newVal.intValue());
        updateDetailContainer(vm.getChosenAction().get(newVal.intValue()));
      } else {
        // si la sélection a disparu (index à -1 suite à une suppression par exemple)
        vm.setIdChosenAction(-1);
        updateDetailContainer(null); // force le nettoyage de l'interface
      }
    });

    // Double clic sur la palette
    availableActions.setOnMouseClicked(event -> {
      if(event.getClickCount() == 2) vm.addAction();
    });

    updateDetailContainer(null);
  }

  private void bindButtons() {
    btnAdd.disableProperty().bind(vm.canAdd().not());
    btnAdd.setOnAction(event -> vm.addAction());

    btnDelete.disableProperty().bind(vm.canRemove().not());
    btnDelete.setOnAction(event -> vm.removeAction());

    btnDown.disableProperty().bind(vm.canDown().not());
    btnDown.setOnAction(event -> {
      vm.downAction();
      chosenActions.getSelectionModel().select(vm.getIdChosenAction());
    });

    btnUp.disableProperty().bind(vm.canUp().not());
    btnUp.setOnAction(event -> {
      vm.upAction();
      chosenActions.getSelectionModel().select(vm.getIdChosenAction());
    });

    btnEmptyAll.disableProperty().bind(vm.canRemoveAll().not());
    btnEmptyAll.setOnAction(event -> vm.removeAll());

    btnDuplicate.setOnAction(event -> vm.copyAction());
    btnDuplicate.disableProperty().bind(vm.canDuplicate().not());

    btnLoad.setOnAction(event -> {
      vm.resetCommands();
      spritePane.getChildren().clear();
      playerView = new PlayerView(vm.getPlayerViewModel());
      spritePane.getChildren().add(playerView);
      chosenActions.getSelectionModel().select(0);
    });
    btnLoad.textProperty().bind(vm.getLoadTextButton());
    btnLoad.disableProperty().bind(vm.canLoadProgram().not());

    btnExecute.setOnAction(event -> {
      if(groupOfExecute.getSelectedToggle() == step_execute) {
        vm.executeAction();
      } else if(groupOfExecute.getSelectedToggle() == automatic_execute) {
        vm.startAutoExecute();
      }
    });
    btnExecute.disableProperty().bind(vm.canExecuteProgram().not());
    btnExecute.textProperty().bind(vm.getExecuteTextButton());
    btnStop.setOnAction(event -> vm.stopAutoExecute());
    btnStop.disableProperty().bind(vm.canStop().not());
    btnStop.visibleProperty().bind(groupOfExecute.selectedToggleProperty().isEqualTo(automatic_execute));
    btnStop.managedProperty().bind(btnStop.visibleProperty());
    slider.valueProperty().bindBidirectional(vm.speedProperty());
    slider.visibleProperty().bind(btnStop.visibleProperty());
    slider.managedProperty().bind(btnStop.visibleProperty());
  }

  private void bindMenuActions() {
    saveItem.setOnAction(event -> handleSaveFile());
    loadItem.setOnAction(event -> handleLoadFile());
  }

  // Algo qui gère la zone de déclarations de variables au début du programme //
  /*
   * Le principe de "la porte fermée" : on lit les actions de haut en bas
   * tant qu'on lit des "DeclareVariable", c'est ok
   * dès qu'on lit une Action qui n'est pas "DeclareVariable", on ferme la porte
   * ça permet d'avoir que des déclarations en haut du programme.
   */
  private boolean isDeclarationZoneValid() {
    boolean isDoorClosed = false;

    for(Actionable action : chosenActions.getItems()) {
      if(action instanceof DeclareVariable) {
        if(isDoorClosed) {
          return false; // si c'est une déclaration mais que la porte est fermée : erreur
        }
      } else {
        isDoorClosed = true;  // ce n'est pas une déclaration du coup on ferme définitivement la porte
      }
    }
    return true;
  }

  // =========================================================================
  // FILE CHOOSER & DESSIN
  // =========================================================================
  private void handleSaveFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier texte", "*.txt"));
    File file = fileChooser.showSaveDialog(this.getScene().getWindow());
    if(file != null) {
      vm.saveInFile(file);
    }
  }

  private void handleLoadFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier texte", "*.txt"));
    File file = fileChooser.showOpenDialog(this.getScene().getWindow());
    if(file != null) {
      vm.loadFile(file);
    }
  }

  private GridPane createGrid(int rows, int cols) {
    GridPane grid = new GridPane();
    grid.setMinSize(500, 500);

    Border cellBorder = new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.3)));
    grid.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.0))));

    for(int x = 0; x < cols; x++) {
      for(int y = 0; y < rows; y++) {
        StackPane cell = new StackPane();
        cell.setMinWidth(50);
        cell.setMinHeight(50);
        cell.setBorder(cellBorder);
        grid.add(cell, x, y);
      }
    }
    return grid;
  }

  private Pane createSpritePane() {

    spritePane.setMinSize(500, 500);

    // Instance de la vue du joueur
    this.playerView = new PlayerView(vm.getPlayerViewModel());

    // Ajout de la vue du joueur au visuel de la grille
    spritePane.getChildren().add(playerView);
    return spritePane;
  }
}