package scratch.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SliderView extends VBox {
  private final Slider slider = new Slider(0.01, 5, 0.01);
  private final Button btnLoad = new Button("Charger");
  private final Button btnExecute = new Button("Exécuter");
  private final Button btnStop = new Button("Arreter");
  private final ToggleGroup groupOfExecute = new ToggleGroup();
  private final RadioButton automaticExecute = new RadioButton("Exécution automatique");
  private final RadioButton manualExecute = new RadioButton("Exécution manuelle");

  public SliderView() {
    this.setSpacing(10);
    this.setAlignment(Pos.CENTER);

    HBox btnContainerScene = new HBox(10, btnLoad, btnExecute, btnStop);
    btnContainerScene.setAlignment(Pos.CENTER);

    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setMajorTickUnit(1);
    slider.setBlockIncrement(0.1f);
    slider.setMaxWidth(Double.MAX_VALUE);
    VBox.setMargin(slider, new Insets(0, 5, 0, 5));

    automaticExecute.setToggleGroup(groupOfExecute);
    automaticExecute.setSelected(true);
    manualExecute.setToggleGroup(groupOfExecute);

    HBox btnsExecuteContainer = new HBox(10, automaticExecute, manualExecute);
    btnsExecuteContainer.setAlignment(Pos.CENTER);

    this.getChildren().addAll(btnsExecuteContainer, btnContainerScene, slider);
  }

  // Getters
  public Button getBtnLoad() { return btnLoad; }
  public Button getBtnExecute() { return btnExecute; }
  public Button getBtnStop() { return btnStop; }
  public Slider getSlider() { return slider; }
  public ToggleGroup getGroupOfExecute() { return groupOfExecute; }
  public RadioButton getAutomaticExecute() { return automaticExecute; }
  public RadioButton getManualExecute() { return manualExecute; }
}
