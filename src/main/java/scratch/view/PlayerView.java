package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import scratch.viewmodel.PlayerViewModel;

/* Représente le personnage sur le plateau.
 * Hérite de Group pour pouvoir combiner le corps (triangle) et le nez (cercle).
 */
public class PlayerView extends Group {
    private final Polygon body = new Polygon();
    private final Circle head = new Circle(2, Color.BLACK);

    // Debug
    private final double size = 15;


    public PlayerView(PlayerViewModel vm) {
        Bindings.bindContent(body.getPoints(), vm.getBodyPoints());
        head.translateXProperty().bind(vm.headXProperty());
        head.translateYProperty().bind(vm.headYProperty());

        vm.positionProperty().addListener((obs, oldPos, newPos) -> {
            if (vm.penDowProperty().get()) {
                var line = new Line(oldPos.getX(), oldPos.getY(), newPos.getX(), newPos.getY());
                line.setStroke(Color.FORESTGREEN);
                this.getChildren().add(line);
            }
        });

        // =========================================================================
        // CRÉATION DE LA FORME
        // =========================================================================
        body.setFill(Color.DEEPSKYBLUE);
        body.setStroke(Color.BLACK);

        // =========================================================================
        // CRÉATION DU NEZ (Repère visuel)
        // =========================================================================
        this.getChildren().addAll(body, head);  // Ajout du nez dans la liste des objets dessinés
    }
}
