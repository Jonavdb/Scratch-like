package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import scratch.model.CustomPoint2D;
import scratch.viewmodel.PlayerViewModel;

import java.util.ArrayList;
import java.util.List;

/* Représente le personnage sur le plateau.
 * Hérite de Group pour pouvoir combiner le corps (triangle) et le nez (cercle).
 */
public class PlayerView extends Group {
    private final Polygon body = new Polygon();
    private final Circle head = new Circle(2, Color.BLACK);

    public PlayerView(PlayerViewModel vm) {
        Bindings.bindContent(body.getPoints(), vm.getBodyPoints());
        head.translateXProperty().bind(vm.headXProperty());
        head.translateYProperty().bind(vm.headYProperty());

        vm.positionProperty().addListener((obs, oldPos, newPos) -> {
            if (vm.penDowProperty().get()) {    drawPlayerTrail(oldPos, newPos);    }

            // dessine le polygone quand le joueur se déplace et que l'action de dessin a été initialisée
            if(vm.drawEnabledProperty().get())  {   drawPolygon(vm, oldPos);    }
        });
        drawPlayer();
    }

    private void drawPlayerTrail(CustomPoint2D oldPos, CustomPoint2D newPos) {
        var line = new Line(oldPos.getX(), oldPos.getY(), newPos.getX(), newPos.getY());
        line.setStroke(Color.FORESTGREEN);
        this.getChildren().add(line);
    }
    private void drawPlayer() {
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
    private void drawPolygon(PlayerViewModel vm, CustomPoint2D oldPos) {
        Line newLine = null;

        double currentAngle = Math.toRadians(vm.angleProperty().get());
        double stepAngle = 2 * Math.PI / vm.polygonProperty().get().getNbSides();

        double currentX = oldPos.getX();
        double currentY = oldPos.getY();

        for (int i = 0; i < vm.polygonProperty().get().getNbSides(); i++) {
            double nextX = currentX + vm.polygonProperty().get().getLenghSides() * Math.cos(currentAngle);
            double nextY = currentY + vm.polygonProperty().get().getLenghSides() * Math.sin(currentAngle);

            newLine = new Line(currentX, currentY, nextX, nextY);
            this.getChildren().add(newLine);

            currentX = nextX;
            currentY = nextY;

            currentAngle += stepAngle;
        }
    }
}
