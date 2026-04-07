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
import scratch.view.components.DoubleParamView;
import scratch.view.components.NoParamView;
import scratch.view.components.SingleParamView;
import scratch.viewmodel.MainViewModel;
import java.io.File;

public class MainView extends VBox {
  private final MainViewModel vm;
  private PlayerView playerView;

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
    programView.getDetailContentContainer().getChildren().clear();

    if(chosenAction == null) {
      programView.getDetailContentContainer().getChildren().add(new Label("(aucune action sélectionnée)"));
      return;
    }

    Label errorLabel = new Label("Erreur valeur");
    errorLabel.setTextFill(Color.RED);

    javafx.scene.Node viewToAdd = switch(chosenAction) {
      // Actions à 1 paramètre
      case DeclareVariable dv -> new SingleParamView("Déclaration de la variable ", dv, dv.getProperty(), errorLabel);
      case Move move -> new SingleParamView("Avancer de ", move, move.getProperty(), errorLabel);
      case TurnLeft tl -> new SingleParamView("Tourner à gauche de ", tl, tl.getProperty(), errorLabel);
      case TurnRight tr -> new SingleParamView("Tourner à droite de ", tr, tr.getProperty(), errorLabel);
      case Repeat r -> new SingleParamView("Répéter ", r, r.getProperty(), errorLabel);

      // Actions à 2 paramètres
      case AssignVariable av ->
              new DoubleParamView("Assignation de la variable ", " valeur : ", av, av.getProperty(), av.getParamProperty(), errorLabel);
      case Increment inc ->
              new DoubleParamView("Incrémentation de la variable ", " de ", inc, inc.getProperty(), inc.getIncrementProperty(), errorLabel);

      // Actions sans paramètre
      case Up up -> new NoParamView("Lever stylo");
      case Down down -> new NoParamView("Abaisser stylo");
      case EndRepeat endRe -> new NoParamView("Arreter répétition");

      default -> new Label("Détail de : " + chosenAction.getProperty());
    };

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

  // Méthode pour générer des cellules personnalisées (cercle de couleur + texte) dans la colonne du MILIEU
  private Callback<ListView<Actionable>, ListCell<Actionable>> createActionCellFactory() {
    return actionableListView -> new ListCell<Actionable>() {
      @Override
      protected void updateItem(Actionable item, boolean empty) {
        super.updateItem(item, empty);
        styleProperty().unbind();
        setStyle(null);
        textProperty().unbind();  // Coupe l'ancien binding car les cellules sont recyclées

        // Pas d'action à afficher
        if(empty || item == null) {
          setText(null);
          setGraphic(null);
          setPadding(new javafx.geometry.Insets(3,5,3,5));
          return;
        }

        // Indentation pour les boucles Repeat
        int gap = 0;
        int currentIndex = getIndex();
        if (currentIndex >= 0 && getListView() != null) {
            var allItems = getListView().getItems();
            for (int i = 0; i < currentIndex; i++) { // on boucle sur toutes les actions avant l'action courante
                Actionable pastItem = allItems.get(i);
                if (pastItem instanceof Repeat){
                    gap++;
                } else if (pastItem instanceof EndRepeat) {
                    gap--;
                }
            }
        }

        if (item instanceof EndRepeat) {
            gap--;
        }

        double leftPadding = 5 + (gap * 20);
        setPadding(new javafx.geometry.Insets(3,5,3,leftPadding));

        // Dessine un cadre rouge si le programme plante (runtime error)
        styleProperty().bind(Bindings.createStringBinding(() -> {
          // si la cellule courante a planté
          if(currentIndex == vm.errorActionIndexProperty().get()) {
            return "-fx-border-color: red;";
          } else {
            return ""; // on retire le css
          }
        }, vm.errorActionIndexProperty()));

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
    programView.getChosenActions().setCellFactory(createActionCellFactory());

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
    SliderView controls = sceneView.getSliderView();

    actionPalette.getBtnAdd().disableProperty().bind(vm.canAdd().not());
    actionPalette.getBtnAdd().setOnAction(event -> vm.addAction());

    programView.getBtnDelete().disableProperty().bind(vm.canRemove().not());
    programView.getBtnDelete().setOnAction(event -> vm.removeAction());

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

    controls.getBtnLoad().setOnAction(event -> {
      vm.resetCommands();
      sceneView.getSpritePane().getChildren().clear();
      playerView = new PlayerView(vm.getPlayerViewModel());
      sceneView.getSpritePane().getChildren().add(playerView);
      programView.getChosenActions().getSelectionModel().select(0);
    });
    controls.getBtnLoad().textProperty().bind(vm.getLoadTextButton());
    controls.getBtnLoad().disableProperty().bind(vm.canLoadProgram().not());

    controls.getBtnExecute().setOnAction(event -> {
      if(controls.getGroupOfExecute().getSelectedToggle() == controls.getStepExecute()) {
        vm.executeAction();
      } else if(controls.getGroupOfExecute().getSelectedToggle() == controls.getAutomaticExecute()) {
        vm.startAutoExecute();
      }
    });
    controls.getBtnExecute().disableProperty().bind(vm.canExecuteProgram().not().or(vm.errorActionIndexProperty().isNotEqualTo(-1)));
    controls.getBtnExecute().textProperty().bind(vm.getExecuteTextButton());

    controls.getBtnStop().setOnAction(event -> vm.stopAutoExecute());
    controls.getBtnStop().disableProperty().bind(vm.canStop().not());
    controls.getBtnStop().visibleProperty().bind(controls.getGroupOfExecute().selectedToggleProperty().isEqualTo(controls.getAutomaticExecute()));
    controls.getBtnStop().managedProperty().bind(controls.getBtnStop().visibleProperty());

    controls.getSlider().valueProperty().bindBidirectional(vm.speedProperty());
    controls.getSlider().visibleProperty().bind(controls.getBtnStop().visibleProperty());
    controls.getSlider().managedProperty().bind(controls.getBtnStop().visibleProperty());
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

    for(Actionable action : programView.getChosenActions().getItems()) {
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
}