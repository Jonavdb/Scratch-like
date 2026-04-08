package scratch.view.components;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import scratch.model.*;

// Cette classe fabriquee la bonne vue en fonction de l'action passée
// On repassera toujours ici en fonction des nouvelles features à faire apparaître à l'écran

public class DetailViewFactory {
  public static Node createDetailView(Actionable chosenAction) {
    if (chosenAction == null) {
      return new Label("(aucune action sélectionnée)");
    }

    Label errorLabel = new Label("Erreur valeur");
    errorLabel.setTextFill(Color.RED);

    return switch (chosenAction) {
      // Actions à 1 paramètre
      case DeclareVariable dv -> new SingleParamView("Déclaration de la variable", dv, dv.getProperty(), errorLabel, "");
      case Move move -> new SingleParamView("Avancer de", move, move.getProperty(), errorLabel, "pixels");
      case TurnLeft tl -> new SingleParamView("Tourner à gauche de", tl, tl.getProperty(), errorLabel, "degrés");
      case TurnRight tr -> new SingleParamView("Tourner à droite de", tr, tr.getProperty(), errorLabel, "degrés");
      case Repeat r -> new SingleParamView("Répéter", r, r.getProperty(), errorLabel, "fois");

      // Actions à 2 paramètres
      case AssignVariable av -> new DoubleParamView("Assignation de la variable", " valeur :", av, av.getProperty(), av.getParamProperty(), errorLabel);
      case Increment inc -> new DoubleParamView("Incrémentation de la variable", " de", inc, inc.getProperty(), inc.getIncrementProperty(), errorLabel);

      // Actions sans paramètre
      case Up up -> new NoParamView("Lever stylo");
      case Down down -> new NoParamView("Abaisser stylo");
      case EndRepeat endRe -> new NoParamView("Arreter répétition");

      default -> new Label("Détail de : " + chosenAction.getProperty());
    };
  }
}
