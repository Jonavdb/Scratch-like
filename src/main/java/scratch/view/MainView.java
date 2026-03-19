package scratch.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
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
import javafx.util.Duration;
import scratch.model.*;
import scratch.viewmodel.MainViewModel;

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
  private final Label labelPrefix = new Label("(aucune action selectionnée)");
  private final TextField actionParamField = new TextField("");
  private final Label labelSuffix = new Label();
  private final Label errorLabel = new Label("Erreur valeur");

  private final HBox detailContent = new HBox(5, labelPrefix, actionParamField, labelSuffix);
  private final VBox detailContentContainer = new VBox(detailContent, errorLabel);

  private final TitledPane detailPane = new TitledPane("Détail de l'action", detailContentContainer);

  // Scene (droite)
  private final VBox sceneBox = new VBox();
  private final Button btnLoad = new Button("Charger");
  private final Button btnExecute = new Button("Exécuter");
  private final Button btnExecuteAll = new Button("Exécuter tout");

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
    actionParamField.setVisible(false);
    actionParamField.setManaged(false);
    actionParamField.setPrefWidth(40);
    detailContent.setAlignment(Pos.CENTER_LEFT);

    errorLabel.setVisible(false);
    errorLabel.setManaged(false);
    errorLabel.setTextFill(Color.RED);

    programBox.getChildren().addAll(new Text("Programme"), chosenActions, btnContainerProgram, detailPane);
    HBox.setHgrow(programBox, Priority.ALWAYS);
  }

  private void configureScene() {
    GridPane gridPane = createGrid(10, 10);
    spritePane = createSpritePane();
    Group board = new Group(gridPane, spritePane);

    HBox btnContainerScene = new HBox(10, btnLoad, btnExecute, btnExecuteAll);
    btnContainerScene.setAlignment(Pos.CENTER);

    sceneBox.setSpacing(10);
    sceneBox.setPadding(new Insets(0, 0, 0, 20));
    sceneBox.getChildren().addAll(new Text("Scene"), board, btnContainerScene);
  }

  private void configureLayoutGlobal() {
    appContainer.setPadding(new Insets(5, 20, 10, 20));
    appContainer.getChildren().addAll(paletteBox, programBox, sceneBox);
    this.setSpacing(20);
    VBox.setVgrow(appContainer, Priority.ALWAYS);
  }

  // Méthode pour générer des cellules personnalisées (cercle de couleur + texte)
  private Callback<ListView<Actionable>, ListCell<Actionable>> createActionCellFactory() {
    return actionableListView -> new ListCell<Actionable>() {
      @Override
      protected void updateItem(Actionable item, boolean empty) {
        super.updateItem(item, empty);

        // Coupe l'ancien binding car les cellules sont recyclées
        textProperty().unbind();

        // Pas d'action à afficher
        if(empty || item == null) {
          setText(null);
          setGraphic(null);
          return;
        }

        if(item.getProperty() != null) {
          // permet de fabriquer un texte à chaque fois que la propriété change
          textProperty().bind(Bindings.createStringBinding(
                  () -> item.getName() + " " + item.getProperty().get(),
                  item.getProperty()
          ));
        } else {
          setText(item.getPrefix());  // Pour les actions sans propriété (stylo)
        }

        Color color = item.getColor();
        Circle circle = new Circle(5, color);

        setTextFill(color);
        setGraphic(circle);
      }
    };
  }

  // =========================================================================
  // BINDINGS (MVVM)
  // =========================================================================

  private void setupBindings() {
    bindLists();
    bindButtons();
    bindActionDetails();
    bindMenuActions();
    errorLabel.visibleProperty().bind(vm.errorProperty());
    errorLabel.managedProperty().bind(vm.errorProperty());
  }

  private void bindLists() {
    // Remplissage
    availableActions.setItems(vm.getAllAction());
    chosenActions.setItems(vm.getChosenAction());

    availableActions.setCellFactory(createActionCellFactory());
    chosenActions.setCellFactory(createActionCellFactory());

    // Synchronisation des sélections avec le VM
    vm.selectedActionProperty().bind(availableActions.getSelectionModel().selectedItemProperty());

    chosenActions.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal != null) {
        vm.setIdChosenAction(newVal.intValue());
      }
    });

    // Double clic sur la palette
    availableActions.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2) vm.addAction();
    });
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
        vm.executeAction();
        chosenActions.getSelectionModel().select( vm.getCurrentActionIndex());
    });
    btnExecute.disableProperty().bind(vm.canExecuteProgram().not());
    btnExecute.textProperty().bind(vm.getExecuteTextButton());

    btnExecuteAll.setOnAction(event -> {
      vm.setIsPlaying(true);
        Timeline timeline = new Timeline();
      KeyFrame keyframe = new KeyFrame(Duration.millis(150), event_1 -> {
          if (!vm.getIsPlaying()){
              timeline.stop();
              return;
          }
          if (vm.getCurrentActionIndex() < vm.getChosenAction().size()) {
              vm.executeAction();
              chosenActions.getSelectionModel().select(vm.getCurrentActionIndex());
          } else {
              timeline.stop();
              vm.setIsPlaying(false);
          }
      });
      timeline.getKeyFrames().add(keyframe);
      timeline.setCycleCount(Timeline.INDEFINITE);
      timeline.play();
    });

    btnExecuteAll.disableProperty().bind(vm.canExecuteProgram().not());
  }

  private void bindActionDetails() {
    var selectedItemProp = chosenActions.getSelectionModel().selectedItemProperty();

    // Binding du Prefix
    labelPrefix.textProperty().bind(Bindings.createStringBinding(() -> {
      Actionable action = selectedItemProp.get();
      return action != null ? action.getPrefix() : "(aucune action sélectionnée)";
    }, selectedItemProp));

    // Binding du Suffix
    labelSuffix.textProperty().bind(Bindings.createStringBinding(() -> {
      Actionable action = selectedItemProp.get();
      return action != null ? action.getSuffix() : "";
    }, selectedItemProp));

    // Binding de la visibilité
    var hasParameterBinding = Bindings.createBooleanBinding(() -> {
      Actionable action = selectedItemProp.get();
      return action != null && action.getProperty() != null;
    }, selectedItemProp);

    actionParamField.visibleProperty().bind(hasParameterBinding);
    actionParamField.managedProperty().bind(hasParameterBinding);

    // Listener (quand on clique sur une nouvelle action, on met à jour le texte de la case)
    selectedItemProp.addListener((obs, oldVal, newVal) -> {
      if (newVal != null && newVal.getProperty() != null) {
        actionParamField.setText(String.valueOf(newVal.getProperty().get()));
      } else {
       actionParamField.clear();
      }
    });

    // Listener (quand on tape au clavier, on envoie au ViewModel)
    actionParamField.textProperty().addListener((obs, oldText, newText) -> {
      Actionable selectedAction = selectedItemProp.get();
      var sanitized = newText.strip();
      vm.validateAndUpdateValue(selectedAction, sanitized);
    });
  }

  private void bindMenuActions() {
    saveItem.setOnAction(event -> handleSaveFile());
    loadItem.setOnAction(event -> handleLoadFile());
  }

  // =========================================================================
  // FILE CHOOSER & DESSIN
  // =========================================================================

  private void handleSaveFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier texte", "*.txt"));
    File file = fileChooser.showSaveDialog(this.getScene().getWindow());
    if (file != null) {
      vm.saveInFile(file);
    }
  }

  private void handleLoadFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier texte", "*.txt"));
    File file = fileChooser.showOpenDialog(this.getScene().getWindow());
    if (file != null) {
      vm.loadFile(file);
    }
  }

  private GridPane createGrid(int rows, int cols) {
    GridPane grid = new GridPane();
    grid.setMinSize(500, 500);

    Border cellBorder = new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.3)));
    grid.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.0))));

    for (int x = 0; x < cols; x++) {
      for (int y = 0; y < rows; y++) {
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