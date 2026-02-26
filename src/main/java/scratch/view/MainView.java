package scratch.view;

import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.converter.NumberStringConverter;
import scratch.model.Actionable;
import scratch.model.Actions;
import scratch.model.ChosenActions;
import scratch.viewmodel.MainViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
  private final Text paletteTitle = new Text("Palette d'actions");
  private final ListView<Actionable> availableActions = new ListView<>();
  private final Button btnAdd = new Button("Ajouter au programme");

  // Programme (centre)
  private final VBox programBox = new VBox();
  private final Text programTitle = new Text("Programme");
  private final ListView<Actionable> chosenActions = new ListView<>();
  private final HBox btnContainerProgram = new HBox();
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
  private final Text sceneTitle = new Text("Scene");
  private final HBox btnContainerScene = new HBox();
  private final Button btnLoad = new Button("Charger");
  private final Button btnExecute = new Button("Exécuter");

  public MainView(MainViewModel vm) {
    this.vm = vm;

    configureMenu();
    configurePalette();
    configureProgram();
    configureScene();
    configureLayoutGlobal();

    this.getChildren().addAll(menuBar, appContainer);

    createBindings();
  }

  private void configureMenu() {
    fileMenu.getItems().addAll(saveItem, loadItem);
    menuBar.getMenus().add(fileMenu);
  }

  private void configurePalette() {
    Text paletteTitle = new Text("Palette d'actions");

    paletteBox.setSpacing(10);
    paletteBox.setPadding(new Insets(0, 10, 0, 0));
    paletteBox.getChildren().addAll(paletteTitle, availableActions, btnAdd);

    HBox.setHgrow(paletteBox, Priority.ALWAYS);
  }

  private void configureProgram() {
    Text programTitle = new Text("Programme");

    HBox btnContainerProgram = new HBox(10);
    btnContainerProgram.setPadding(new Insets(10, 0, 10, 0));
    btnContainerProgram.getChildren().addAll(btnUp, btnDown, btnDuplicate, btnDelete, btnEmptyAll);

    // Configuration du TitledPane
    programBox.setSpacing(10);
    programBox.setPadding(new Insets(0, 0, 0, 10));
    programBox.getChildren().addAll(programTitle, chosenActions, btnContainerProgram, detailPane);

    HBox.setHgrow(programBox, Priority.ALWAYS);
  }

  /* Méthode utilitaire pour le triangle (le personnage) */
  private Pane createSpritePane() {
    Pane spritePane = new Pane();
    spritePane.setMinSize(500, 500);

    // Instantiation
    double size = 15;
    double originX = 0;
    double originY = 0;
    double startX = 250;
    double startY = 250;
    double maxDegreesDemiCircle = 180; // visualise l'angle max d'un demi cercle trigonometrique
    double minDegreesDemiCircle = 1; // visualise l'angle minimal d'un demi cercle trigonometrique
    double angleOffsetRecalibror = 90; // parce que de base le triangle point vers le haut (90 degrees), mais le cercle trigonometrique commence à droite (0 degree), donc on rajoute le offset de 90 degrees
    double turningValue = -60; // valeur dans l'inspecteur
    double xP1 = originX + size * Math.sin(Math.toRadians(60)) * Math.cos(Math.toRadians(turningValue + angleOffsetRecalibror)),
            yP1 = originY - size * Math.sin(Math.toRadians(60)) * Math.sin(Math.toRadians(turningValue + angleOffsetRecalibror)),
            xP2 = originX - size * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(turningValue)),
            yP2 = originY + size * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(turningValue)),
            xP3 = originX + size * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(turningValue)),
            yP3 = originY - size * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(turningValue));

    Polygon triangle = new Polygon(
            xP1, yP1,  // Pointe du haut
            xP2,  yP2,  // Coin bas gauche
            xP3,  yP3   // Coin bas droite
    );
    triangle.setFill(Color.DEEPSKYBLUE);
    triangle.setStroke(Color.BLACK);

    // Position initiale au centre de la grille
    triangle.setLayoutX(startX);
    triangle.setLayoutY(startY);

    // Déplacement (rotations inclus)
    double distance = 100;
    double radians = Math.toRadians(turningValue) + Math.PI / 2;
    double diffX = distance * Math.cos(radians);
    double diffY = distance * Math.sin(radians);
    double realAngle = angleOffsetRecalibror + turningValue;

    // en gros on verifie si le personnage recule on avance
    boolean facingUp = realAngle < maxDegreesDemiCircle && realAngle > minDegreesDemiCircle;

    int directionY = facingUp ? -1 : 1;  // -1 parce que l'axe des Y est inversé donc pour monter, le Y est negatif
    double nextXPosition = triangle.getLayoutX() + diffX;
    double nextYPosition = triangle.getLayoutY() + (directionY * Math.abs(diffY));
    triangle.setLayoutX(nextXPosition);
    triangle.setLayoutY(nextYPosition);

    // Nez du personage
    Circle head = new Circle(2);
    head.setFill(Color.BLACK);
    head.setStroke(Color.BLACK);
    head.setTranslateX(xP1 + triangle.getLayoutX());
    head.setTranslateY(yP1 + triangle.getLayoutY()); // Repositionnement du nez en fonction de la position du personnage

    spritePane.getChildren().addAll(triangle, head); // Ajout du nez dans la liste des objets dessinés
    return spritePane;
  }

  /* Méthode utilitaire pour générer la grille */
  private GridPane createGrid(int rows, int cols) {
    GridPane grid = new GridPane();
    grid.setMinSize(500, 500);
    //grid.setAlignment(Pos.CENTER);

    // Bordure de chaque cellule
    Border cellBorder = new Border(new BorderStroke(
            Color.LIGHTBLUE,
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            new BorderWidths(0.3)
    ));

    // Bordure noire autour de la grille entière
    grid.setBorder(new Border(new BorderStroke(
            Color.BLACK,
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            new BorderWidths(1.0)
    )));

    // Génération des cases
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

  private void configureScene() {
    Text sceneTitle = new Text("Scene");

    // Plateau de jeu (Grille en dessous et Sprite au-dessus)
    GridPane gridPane = createGrid(10, 10);
    Pane spritePane = createSpritePane();
    Group board = new Group(gridPane, spritePane);

    // Boutons
    HBox btnContainerScene = new HBox(10, btnLoad, btnExecute);
    btnContainerScene.setAlignment(Pos.CENTER);

    // SceneBox
    sceneBox.setSpacing(10);
    sceneBox.setPadding(new Insets(0, 0, 0, 20));
    sceneBox.getChildren().addAll(sceneTitle, board, btnContainerScene);
  }

  private void configureLayoutGlobal() {
    appContainer.setPadding(new Insets(5, 20, 10, 20));
    appContainer.getChildren().addAll(paletteBox, programBox, sceneBox);

    this.setSpacing(20);
    VBox.setVgrow(appContainer, Priority.ALWAYS);
  }

  private void createBindings(){
      availableActions.setItems(vm.getAllAction());
      vm.selectedActionProperty().bind(availableActions.getSelectionModel().selectedItemProperty());
      chosenActions.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
          if (newValue != null) {
              vm.setIdChosenAction(newValue.intValue());
          }
      });
      btnAdd.disableProperty().bind(vm.canAdd().not());
      btnAdd.setOnAction( event -> vm.addAction());
      btnDelete.disableProperty().bind(vm.canRemove().not());
      btnDelete.setOnAction(event -> vm.removeAction());
      btnDown.disableProperty().bind(vm.canDown().not());
      btnDown.setOnAction(event -> vm.downAction());
      btnUp.disableProperty().bind(vm.canUp().not());
      btnUp.setOnAction(event -> vm.upAction());
      btnEmptyAll.disableProperty().bind(vm.canRemoveAll().not());
      btnEmptyAll.setOnAction(event -> vm.removeAll());
      btnDuplicate.setOnAction(event -> vm.copyAction());
      chosenActions.setItems(vm.getChosenAction());

      chosenActions.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
          if (currentBoundProperty != null) {
              actionParamField.textProperty().unbindBidirectional(currentBoundProperty);
              currentBoundProperty = null;
          }
          if (newValue != null && newValue.getProperty() != null){
              actionParamField.setDisable(false);
              currentBoundProperty = newValue.getProperty();
              actionParamField.textProperty().bindBidirectional(currentBoundProperty, new NumberStringConverter());
          } else {
              actionParamField.clear();
              actionParamField.setDisable(true);
          }
      });
      saveItem.setOnAction(event -> {
          FileChooser fileChooser = new FileChooser();
          fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("fichier texte", "*.txt"));
          File file = fileChooser.showSaveDialog(this.getScene().getWindow());
          vm.saveInFile(file);
      });

      loadItem.setOnAction(event -> {
          FileChooser fileChooser = new FileChooser();
          fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("fichier texte", "*.txt"));
          File file = fileChooser.showOpenDialog(this.getScene().getWindow());
          vm.loadFile(file);
      });
  }
}
