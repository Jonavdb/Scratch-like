package scratch.view;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class SceneView extends VBox {
  private Pane spritePane = new Pane();
  private SliderView sliderView = new SliderView();
  private VBox trackerContainer = new VBox();

  public SceneView() {
    this.setSpacing(10);
    this.setPadding(new Insets(0, 0, 0, 20));

    // Grille et panneau de dessin
    GridPane gridPane = createGrid(10, 10);
    spritePane.setMinSize(500, 500);
    Group board = new Group(gridPane, spritePane);

    this.getChildren().addAll(new Text("Scene"), board, trackerContainer, sliderView);
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

  // Getters
  public Pane getSpritePane() { return spritePane; }
  public SliderView getSliderView() { return sliderView; }
  public VBox getTrackerContainer() { return trackerContainer; }
}
