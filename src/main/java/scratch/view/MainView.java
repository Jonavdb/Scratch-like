package scratch.view;

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
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import scratch.model.Actions;
import scratch.model.ChosenActions;
import scratch.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainView extends VBox {
  private final MainViewModel vm;

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
  private final ListView<Actions> availableActions = new ListView<>();
  private final Button btnAdd = new Button("Ajouter au programme");

  // Programme (centre)
  private final VBox programBox = new VBox();
  private final Text programTitle = new Text("Programme");
  private final ListView<ChosenActions> chosenActions = new ListView<>();
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

//     TODO
//    createBindings();
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

  private void configureScene() {
    Text sceneTitle = new Text("Scene");
    GridPane gridPane = createGrid(10, 10); // Grille (générée par une méthode dédiée)

    Pane spritePane = new Pane();
    spritePane.setMinSize(500, 500);
    Polygon triangle = new Polygon();
    List<Double> points = new ArrayList<>();
    points.add(0, 0.0);
    points.add(1, -8.0);
    points.add(2, -8.0);
    points.add(3, 8.0);
    points.add(4, 8.0);
    points.add(5, 8.0); // pivot is at the belly of the triangle
    triangle.getPoints().setAll(points);
    triangle.setFill(Color.rgb(0, 182, 249));
    triangle.setStroke(Color.BLACK);
    triangle.setStrokeWidth(1);
    triangle.setLayoutX(252.5); // TODO -> SHOULD BE 250 (exact horizontal center of the grid) => find out why i need offset x of 2.5
    triangle.setLayoutY(245); // TODO -> SHOULD BE 242 (250 - half height of triangle which is 8) => ind out why extra 3 pixels
    spritePane.getChildren().add(triangle);
    Group board = new Group();
    board.getChildren().addAll(gridPane, spritePane);

    HBox btnContainerScene = new HBox(10); // Boutons
    btnContainerScene.getChildren().addAll(btnLoad, btnExecute);
    btnContainerScene.setAlignment(Pos.CENTER);

    sceneBox.setSpacing(10);
    sceneBox.setPadding(new Insets(0, 0, 0, 20));
    sceneBox.getChildren().addAll(sceneTitle, board, btnContainerScene);
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

  private void configureLayoutGlobal() {
    appContainer.setPadding(new Insets(5, 20, 10, 20));
    appContainer.getChildren().addAll(paletteBox, programBox, sceneBox);

    this.setSpacing(20);
    VBox.setVgrow(appContainer, Priority.ALWAYS);
  }
}
