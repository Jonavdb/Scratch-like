package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import scratch.model.*;
import scratch.view.components.DetailViewFactory;
import scratch.viewmodel.MainViewModel;
import javafx.util.Callback;
import java.io.File;

public class MainView extends VBox {
  private final MainViewModel vm;
  private PlayerView playerView;
  private TrackerView trackerView;
  private Pane spritePane = new Pane();

  // Conteneur Principal
  private final HBox appContainer = new HBox();

  // MenuBar
  private final MenuBar menuBar = new MenuBar();
  private final Menu fileMenu = new Menu("File");
  private final MenuItem saveItem = new MenuItem("Save");
  private final MenuItem loadItem = new MenuItem("Load");

  // Palette d'actions (gauche)
  private final ActionPaletteView actionPalette = new ActionPaletteView();

  // Programme (centre)
  private final ProgramView programView = new ProgramView();

  // Scene (droite)
  private final SceneView sceneView = new SceneView();

  public MainView(MainViewModel vm) {
    this.vm = vm;

    // Mise en place de l'UI
    configureMenu();
    configureLayoutGlobal();
    this.getChildren().addAll(menuBar, appContainer);

    // Le Joueur
    this.playerView = new PlayerView(vm.getPlayerViewModel());
    sceneView.getSpritePane().getChildren().add(playerView);

    // Le Tracker
    this.trackerView = new TrackerView(vm.getTrackerViewModel());
    sceneView.getTrackerContainer().getChildren().add(trackerView);

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

  private void configureLayoutGlobal() {
    appContainer.setPadding(new Insets(5, 20, 10, 20));
    appContainer.getChildren().addAll(actionPalette, programView, sceneView);
    this.setSpacing(20);
    VBox.setVgrow(appContainer, Priority.ALWAYS);
  }

  private void updateDetailContainer(Actionable chosenAction) {
    // nettoyage
    programView.getDetailContentContainer().getChildren().clear();
    // la factory crée la vue dynamiquement
    var viewToAdd = DetailViewFactory.createDetailView(chosenAction);
    // ajoute la vue à l'écran
    programView.getDetailContentContainer().getChildren().add(viewToAdd);
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
          setStyle(null);
          styleProperty().unbind();
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

  // =========================================================================
  // BINDINGS (MVVM) && UTILS
  // =========================================================================
  private void setupBindings() {
    bindLists();
    bindButtons();
    bindMenuActions();

    vm.currActionIndexProperty().addListener((obs, oldVal, newVal) -> {
      programView.getChosenActions().getSelectionModel().select(newVal.intValue());
    });

    programView.getRuntimeErrorLabel().textProperty().bind(vm.getErrorRuntimeMessageProperty());
    var hasRuntimeError = Bindings.createBooleanBinding(
            () -> vm.getErrorRuntimeMessageProperty().get() != null && !vm.getErrorRuntimeMessageProperty().get().isEmpty(),
            vm.getErrorRuntimeMessageProperty()
    );

    programView.getRuntimeErrorLabel().visibleProperty().bind(hasRuntimeError);
    programView.getRuntimeErrorLabel().managedProperty().bind(hasRuntimeError);
  }

  private void bindLists() {
    // Remplissage
    actionPalette.getAvailableActions().setItems(vm.getAllAction());
    programView.getChosenActions().setItems(vm.getChosenAction());

    actionPalette.getAvailableActions().setCellFactory(createPaletteCellFactory());
    programView.getChosenActions().setCellFactory(listView -> new ActionCellView(vm));

    // Synchronisation des sélections avec le VM
    vm.selectedActionProperty().bind(actionPalette.getAvailableActions().getSelectionModel().selectedItemProperty());

    programView.getChosenActions().getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
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
    actionPalette.getAvailableActions().setOnMouseClicked(event -> {
      if(event.getClickCount() == 2) vm.addAction();
    });

    updateDetailContainer(null);
  }

  private void bindButtons() {
    // On passe par sceneView pour récupérer le SliderView affiché à l'écran
    SliderView sliderView = sceneView.getSliderView();

    actionPalette.getBtnAdd().disableProperty().bind(vm.canAdd().not());
    actionPalette.getBtnAdd().setOnAction(event -> vm.addAction());

    programView.getBtnDelete().disableProperty().bind(vm.canRemove().not());
    programView.getBtnDelete().setOnAction(event -> {
        int indexToDelete = programView.getChosenActions().getSelectionModel().getSelectedIndex();
        int listSize = programView.getChosenActions().getItems().size();
        vm.removeAction();
        if (indexToDelete >= 0 && indexToDelete < listSize - 1) {
            programView.getChosenActions().getSelectionModel().select(indexToDelete);
        }
    });

    programView.getBtnDown().disableProperty().bind(vm.canDown().not());
    programView.getBtnDown().setOnAction(event -> {
      vm.downAction();
      programView.getChosenActions().getSelectionModel().select(vm.getIdChosenAction());
    });

    programView.getBtnUp().disableProperty().bind(vm.canUp().not());
    programView.getBtnUp().setOnAction(event -> {
      vm.upAction();
      programView.getChosenActions().getSelectionModel().select(vm.getIdChosenAction());
    });

    programView.getBtnEmptyAll().disableProperty().bind(vm.canRemoveAll().not());
    programView.getBtnEmptyAll().setOnAction(event -> vm.removeAll());

    programView.getBtnDuplicate().setOnAction(event -> vm.copyAction());
    programView.getBtnDuplicate().disableProperty().bind(vm.canDuplicate().not());

    sliderView.getBtnLoad().setOnAction(event -> {
      vm.resetCommands();
      sceneView.getSpritePane().getChildren().clear();  // ca efface tout le contenu de la grille

      playerView = new PlayerView(vm.getPlayerViewModel());
      sceneView.getSpritePane().getChildren().add(playerView);

      programView.getChosenActions().getSelectionModel().select(0);
    });

    sliderView.getBtnLoad().textProperty().bind(vm.getLoadTextButton());
    sliderView.getBtnLoad().disableProperty().bind(vm.canLoadProgram().not());

    sliderView.getBtnExecute().setOnAction(event -> {
      if(sliderView.getGroupOfExecute().getSelectedToggle() == sliderView.getManualExecute()) {
        vm.executeAction();
      } else if(sliderView.getGroupOfExecute().getSelectedToggle() == sliderView.getAutomaticExecute()) {
        vm.startAutoExecute();
      }
    });

    sliderView.getBtnExecute().disableProperty().bind(vm.canExecuteProgram().not().or(vm.errorActionIndexProperty().isNotEqualTo(-1)));
    sliderView.getBtnExecute().textProperty().bind(vm.getExecuteTextButton());

    sliderView.getBtnStop().setOnAction(event -> vm.stopAutoExecute());
    sliderView.getBtnStop().disableProperty().bind(vm.canStop().not());
    sliderView.getBtnStop().visibleProperty().bind(sliderView.getGroupOfExecute().selectedToggleProperty().isEqualTo(sliderView.getAutomaticExecute()));
    sliderView.getBtnStop().managedProperty().bind(sliderView.getBtnStop().visibleProperty());

    sliderView.getSlider().valueProperty().bindBidirectional(vm.speedProperty());
    sliderView.getSlider().visibleProperty().bind(sliderView.getBtnStop().visibleProperty());
    sliderView.getSlider().managedProperty().bind(sliderView.getBtnStop().visibleProperty());
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
}