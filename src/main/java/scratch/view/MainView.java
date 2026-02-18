package scratch.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import scratch.model.Actions;
import scratch.model.ChosenActions;
import scratch.viewmodel.MainViewModel;

public class MainView extends VBox {
  private final MainViewModel vm;

  // Hbox container
  private final HBox appContainer = new HBox();

  // MenuBar
  private final MenuBar menuBar = new MenuBar();
  private final Menu file = new Menu("File");
  private final MenuItem save = new MenuItem("Save");
  private final MenuItem load = new MenuItem("Load");

  // Palette d'actions
  private final VBox paletteBox = new VBox();
  private final Text paletteTitle = new Text("Palette d'actions");
  private final ListView<Actions> availableActions = new ListView<>();
  private final Button btnAdd = new Button("Ajouter au programme");

  // Programme
  private final VBox programBox = new VBox();
  private final Text programTitle = new Text("Programme");
  private final ListView<ChosenActions> chosenActions = new ListView<>();
  private final HBox btnContainerProgram = new HBox();
  private final Button btnUp = new Button("Monter");
  private final Button btnDown = new Button("Descendre");
  private final Button btnDuplicate = new Button("Dupliquer");
  private final Button btnDelete = new Button("Supprimer");
  private final Button btnEmptyAll = new Button("Vider tout");

  // Action details
  VBox actionDetails = new VBox();
  TitledPane detailPane = new TitledPane("Détail de l'action", actionDetails);

  // Scene
  private final VBox sceneBox = new VBox();
  private final Text sceneTitle = new Text("Scene");
  private final HBox btnContainerScene = new HBox();
  private final Button btnLoad = new Button("Charger");
  private final Button btnExecute = new Button("Exécuter");


  public MainView(MainViewModel vm) {
    this.vm = vm;

    // MenuBar
    menuBar.getMenus().addAll(file);
    file.getItems().addAll(save, load);

    // Palette Box
    paletteBox.setSpacing(10);
    paletteBox.getChildren().addAll(paletteTitle, availableActions, btnAdd);

    // Program Box
    programBox.setSpacing(10);
    btnContainerProgram.getChildren().addAll(btnUp, btnDown, btnDuplicate, btnDelete, btnEmptyAll);
    programBox.getChildren().addAll(programTitle, chosenActions, btnContainerProgram, detailPane);

    // Action details
    actionDetails.getChildren().add(new TextField());

    // Scene
    btnContainerScene.getChildren().addAll(btnLoad, btnExecute);

    // Grid
    GridPane gridPane = new GridPane();
    gridPane.setMinSize(500, 500);
    gridPane.setAlignment(Pos.CENTER);
      BorderStroke gridBorder = new BorderStroke(
              Color.BLACK,
              BorderStrokeStyle.SOLID,
              CornerRadii.EMPTY,
              new BorderWidths(0.6)
      );
    gridPane.setBorder(new Border(gridBorder));
    var nbHNodes = 10;
    var nbVNodes = 10;
    for(int i = 0; i < nbHNodes; ++i) {
        for(int j = 0; j < nbVNodes; ++j) {
            StackPane cell = new StackPane();
            cell.setMinWidth(50);
            cell.setMinHeight(50);
            BorderStroke stroke = new BorderStroke(
                    Color.LIGHTBLUE,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    new BorderWidths(0.3)
            );
            cell.setBorder(new Border(stroke));
            gridPane.add(cell, i, j);
        }
    }
    sceneBox.getChildren().addAll(sceneTitle, gridPane, btnContainerScene);

    // App
    appContainer.getChildren().addAll(paletteBox, programBox, sceneBox);
    VBox.setVgrow(appContainer, Priority.ALWAYS);
    HBox.setHgrow(paletteBox, Priority.ALWAYS);
    HBox.setHgrow(programBox, Priority.ALWAYS);
    this.setSpacing(20);

    this.getChildren().addAll(menuBar, appContainer);
  }
}
