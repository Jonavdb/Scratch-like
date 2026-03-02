package scratch.view;

import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import scratch.model.PlayerState;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/* Représente le personnage sur le plateau.
 * Hérite de Group pour pouvoir combiner le corps (triangle) et le nez (cercle).
 */
public class PlayerSprite extends Group {
  private final Polygon body;
  private final Circle head;

  // Debug
  private final double size = 15;
  private final PlayerState state;

  public PlayerSprite(PlayerState state) {
      this.state = state;

      // =========================================================================
      // CRÉATION DE LA FORME
      // =========================================================================
      body = new Polygon();
      body.setFill(Color.DEEPSKYBLUE);
      body.setStroke(Color.BLACK);

      // =========================================================================
      // CRÉATION DU NEZ (Repère visuel)
      // =========================================================================
      head = new Circle(2, Color.BLACK);
      redrawPlayer(state.getX(), state.getY(), state.getAngle());
      this.getChildren().addAll(body, head);  // Ajout du nez dans la liste des objets dessinés

      state.positionProperty().addListener((obs, oldPos, newPos) -> {
          if (state.isPenDown()) {
              var line = new Line(oldPos.getX(), oldPos.getY(), newPos.getX(), newPos.getY());
              line.setStroke(Color.FORESTGREEN);
              this.getChildren().add(line);
          }
          redrawPlayer(newPos.getX(), newPos.getY(), state.getAngle());
      });

      state.angleProperty().addListener((obs, oldAngle, newAngle) -> {
          redrawPlayer(state.getX(), state.getY(), newAngle.doubleValue());
      });
  }

  // Ajouter des méthodes plus tard ici pour le faire bouger etc...
  // public void moveTo(double newX, double newY) { ... }

    private void redrawPlayer(double x, double y, double angle){
        double xP1 = x + size * Math.sin(Math.toRadians(60)) * Math.cos(Math.toRadians(angle + 90));
        double yP1 = y - size * Math.sin(Math.toRadians(60)) * Math.sin(Math.toRadians(angle + 90));
        double xP2 = x - size * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(angle));
        double yP2 = y + size * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(angle));
        double xP3 = x + size * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(angle));
        double yP3 = y - size * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(angle));

        body.getPoints().clear();
        body.getPoints().setAll(xP1, yP1, xP2, yP2, xP3, yP3);

        head.setTranslateX(xP1);
        head.setTranslateY(yP1);
    }
}