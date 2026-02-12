package scratch.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import scratch.model.Actions;
import scratch.model.ChosenActions;
import scratch.viewmodel.MainViewModel;

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
    Text canvasPlaceholder = new Text("canva ici"); // Placeholder

    HBox btnContainerScene = new HBox(10);
    btnContainerScene.getChildren().addAll(btnLoad, btnExecute);

    sceneBox.setSpacing(10);
    sceneBox.setPadding(new Insets(0, 0, 0, 20));
    sceneBox.getChildren().addAll(sceneTitle, canvasPlaceholder, btnContainerScene);
  }

  private void configureLayoutGlobal() {
    appContainer.setPadding(new Insets(5, 20, 10, 20));
    appContainer.getChildren().addAll(paletteBox, programBox, sceneBox);

    this.setSpacing(20);
    VBox.setVgrow(appContainer, Priority.ALWAYS);
  }

  private void createBindings() {
    // TODO
    // availableActions.setItems(vm.getActions());
    // btnAdd.setOnAction(e -> ...);
    // actionParamField.textProperty().bindBidirectional(...);
  }
}
