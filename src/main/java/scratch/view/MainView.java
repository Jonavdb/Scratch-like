package scratch.view;

import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.converter.NumberStringConverter;
import scratch.model.Actionable;
import scratch.viewmodel.MainViewModel;

import java.io.File;

public class MainView extends VBox {
  private final MainViewModel vm;
  private IntegerProperty currentBoundProperty = null;

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
  private final TextField actionParamField = new TextField();
  private final TitledPane detailPane = new TitledPane("Détail de l'action", new VBox(actionParamField));

  // Scene (droite)
  private final VBox sceneBox = new VBox();
  private final Button btnLoad = new Button("Charger");
  private final Button btnExecute = new Button("Exécuter");

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

    programBox.setSpacing(10);
    programBox.setPadding(new Insets(0, 0, 0, 10));
    programBox.getChildren().addAll(new Text("Programme"), chosenActions, btnContainerProgram, detailPane);
    HBox.setHgrow(programBox, Priority.ALWAYS);
  }

  private void configureScene() {
    GridPane gridPane = createGrid(10, 10);
    Pane spritePane = createSpritePane();
    Group board = new Group(gridPane, spritePane);

    HBox btnContainerScene = new HBox(10, btnLoad, btnExecute);
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

  // =========================================================================
  // BINDINGS (MVVM)
  // =========================================================================

  private void setupBindings() {
    bindLists();
    bindButtons();
    bindActionDetails();
    bindMenuActions();
  }

  private void bindLists() {
    // Remplissage
    availableActions.setItems(vm.getAllAction());
    chosenActions.setItems(vm.getChosenAction());

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
    btnDown.setOnAction(event -> vm.downAction());

    btnUp.disableProperty().bind(vm.canUp().not());
    btnUp.setOnAction(event -> vm.upAction());

    btnEmptyAll.disableProperty().bind(vm.canRemoveAll().not());
    btnEmptyAll.setOnAction(event -> vm.removeAll());

    btnDuplicate.setOnAction(event -> vm.copyAction());
  }

  private void bindActionDetails() {
    chosenActions.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
      // Nettoyer l'ancien binding
      if (currentBoundProperty != null) {
        actionParamField.textProperty().unbindBidirectional(currentBoundProperty);
        currentBoundProperty = null;
      }

      // Appliquer le nouveau
      if (newVal != null && newVal.getProperty() != null) {
        actionParamField.setDisable(false);
        currentBoundProperty = newVal.getProperty();
        actionParamField.textProperty().bindBidirectional(currentBoundProperty, new NumberStringConverter());
      } else {
        actionParamField.clear();
        actionParamField.setDisable(true);
      }
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
    Pane spritePane = new Pane();
    spritePane.setMinSize(500, 500);

    // Instance du joueur avec les paramètres : X = 250, Y = 250, Angle = -60
    PlayerSprite player = new PlayerSprite(250, 250, -60);

    spritePane.getChildren().add(player);
    return spritePane;
  }
}