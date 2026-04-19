package scratch.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import scratch.model.Actionable;

public class ProgramView extends VBox {
  private final ListView<Actionable> chosenActions = new ListView<>();
  private final Button btnUp = new Button("Monter");
  private final Button btnDown = new Button("Descendre");
  private final Button btnDuplicate = new Button("Dupliquer");
  private final Button btnDelete = new Button("Supprimer");
  private final Button btnEmptyAll = new Button("Vider tout");

  private final Label runtimeErrorLabel = new Label();
  private final VBox detailContentContainer = new VBox(5);
  private final TitledPane detailPane = new TitledPane(); // Contenu défini plus bas

  public ProgramView() {
    HBox btnContainerProgram = new HBox(10, btnUp, btnDown, btnDuplicate, btnDelete, btnEmptyAll);
    btnContainerProgram.setPadding(new Insets(10, 0, 10, 0));

    // Programme
    this.setSpacing(10);
    this.setPadding(new Insets(0, 0, 0, 10));

    // Détail de l'action
    runtimeErrorLabel.setTextFill(Color.RED);
    runtimeErrorLabel.setStyle("-fx-font-size: 16px;");

    detailPane.setText("Détail de l'action");
    VBox detailWrapper = new VBox(10, detailContentContainer, runtimeErrorLabel);
    detailPane.setContent(detailWrapper);

    this.getChildren().addAll(new Text("Programme"), chosenActions, btnContainerProgram, detailPane);
    HBox.setHgrow(this, Priority.ALWAYS);
  }

  // Getters
  public ListView<Actionable> getChosenActions() { return chosenActions; }
  public Button getBtnUp() { return btnUp; }
  public Button getBtnDown() { return btnDown; }
  public Button getBtnDuplicate() { return btnDuplicate; }
  public Button getBtnDelete() { return btnDelete; }
  public Button getBtnEmptyAll() { return btnEmptyAll; }
  public VBox getDetailContentContainer() { return detailContentContainer; }
  public Label getRuntimeErrorLabel() { return runtimeErrorLabel; }
}
