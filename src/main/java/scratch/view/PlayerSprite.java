package scratch.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

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
  private double currTurnValue;
  private final double size = 15;
  private final double angleOffsetRecalibror = 90;
  private double headX;
  private double headY;
  private boolean hasTrail = true;

  public PlayerSprite(double startX, double startY, double initialTurnValue) {
    currTurnValue = initialTurnValue;

    // =========================================================================
    // CALCUL DES POINTS DU TRIANGLE (Géométrie + Rotation)
    // =========================================================================

    // POINT 1 (xP1, yP1) : C'est la pointe du triangle (le nez/l'avant).
    // angleOffsetRecalibror (90°) pour que la pointe regarde vers le haut par défaut.
    double xP1 = size * Math.sin(Math.toRadians(60)) * Math.cos(Math.toRadians(initialTurnValue + angleOffsetRecalibror));
    double yP1 = -size * Math.sin(Math.toRadians(60)) * Math.sin(Math.toRadians(initialTurnValue + angleOffsetRecalibror));
    headX = xP1;
    headY = yP1;
    // Le "-" devant 'size' pour yP1 sert à inverser l'axe Y de l'écran (pour pointer vers le "vrai" haut).

    // POINT 2 (xP2, yP2) : C'est le coin arrière GAUCHE du triangle.
    double xP2 = -size * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(initialTurnValue));
    double yP2 = size * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(initialTurnValue));

    // POINT 3 (xP3, yP3) : C'est le coin arrière DROIT du triangle.
    double xP3 = size * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(initialTurnValue));
    double yP3 = -size * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(initialTurnValue));


    // =========================================================================
    // CRÉATION DE LA FORME
    // =========================================================================
    body = new Polygon(xP1, yP1, xP2, yP2, xP3, yP3);
    body.setFill(Color.DEEPSKYBLUE);
    body.setStroke(Color.BLACK);
    body.setLayoutX(startX);
    body.setLayoutY(startY);

    // =========================================================================
    // CALCUL DU DÉPLACEMENT (Vecteur de mouvement)
    // =========================================================================
    double distance = 0; // Le personnage fait un bond de 100 pixels vers l'avant

    // On calcule l'angle de déplacement (en radians). On ajoute PI/2 (90 degrés)
    // car l'avant de notre triangle est décalé par rapport au zéro mathématique.
    double radians = Math.toRadians(initialTurnValue) + Math.PI / 2;

    // Trigonométrie pour avancer
    double diffX = distance * Math.cos(radians);  // NouveauX = cosinus(angle) * vitesse
    double diffY = distance * Math.sin(radians);  // NouveauY = sinus(angle) * vitesse

    // L'angle réel sur le cercle (0 à 360)
    double realAngle = angleOffsetRecalibror + initialTurnValue;

    // On vérifie si l'angle fait pointer le personnage vers le "haut" de l'écran
    boolean facingUp = realAngle < 180 && realAngle > 1;

    int directionY = facingUp ? -1 : 1; // L'angle réel sur le cercle (0 à 360)

    // Appliquer le déplacement à la position actuelle
    body.setLayoutX(body.getLayoutX() + diffX);
    body.setLayoutY(body.getLayoutY() + (directionY * Math.abs(diffY)));

    // =========================================================================
    // CRÉATION DU NEZ (Repère visuel)
    // =========================================================================
    head = new Circle(2, Color.BLACK);
    head.setTranslateX(xP1 + body.getLayoutX());
    head.setTranslateY(yP1 + body.getLayoutY());

    this.getChildren().addAll(body, head);  // Ajout du nez dans la liste des objets dessinés
  }

  // Ajouter des méthodes plus tard ici pour le faire bouger etc...
  // public void moveTo(double newX, double newY) { ... }
  public void turnRight(int turnValue) {
      if(turnValue < 0 || turnValue > 180)
          return;

      currTurnValue -=turnValue;
      // =========================================================================
      // CALCUL DES POINTS DU TRIANGLE (Géométrie + Rotation)
      // =========================================================================

      // POINT 1 (xP1, yP1) : C'est la pointe du triangle (le nez/l'avant).
      // angleOffsetRecalibror (90°) pour que la pointe regarde vers le haut par défaut.
      double xP1 = size * Math.sin(Math.toRadians(60)) * Math.cos(Math.toRadians(currTurnValue + angleOffsetRecalibror));
      double yP1 = -size * Math.sin(Math.toRadians(60)) * Math.sin(Math.toRadians(currTurnValue + angleOffsetRecalibror));
      headX = xP1;
      headY = yP1;
      // Le "-" devant 'size' pour yP1 sert à inverser l'axe Y de l'écran (pour pointer vers le "vrai" haut).

      // POINT 2 (xP2, yP2) : C'est le coin arrière GAUCHE du triangle.
      double xP2 = -size * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(currTurnValue));
      double yP2 = size * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(currTurnValue));

      // POINT 3 (xP3, yP3) : C'est le coin arrière DROIT du triangle.
      double xP3 = size * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(currTurnValue));
      double yP3 = -size * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(currTurnValue));


      // =========================================================================
      // CRÉATION DE LA FORME
      // =========================================================================
      var currX = body.getLayoutX();
      var currY = body.getLayoutY();
      body.getPoints().clear();
      body.getPoints().setAll(xP1, yP1, xP2, yP2, xP3, yP3);
      body.setFill(Color.DEEPSKYBLUE);
      body.setStroke(Color.BLACK);
      body.setLayoutX(currX);
      body.setLayoutY(currY);

      // =========================================================================
      // CRÉATION DU NEZ (Repère visuel)
      // =========================================================================
      head.setTranslateX(xP1 + body.getLayoutX());
      head.setTranslateY(yP1 + body.getLayoutY());
  }

  public void turnLeft(int turnValue) {
      if(turnValue < 0 || turnValue > 180)
          return;

      currTurnValue += turnValue;
      // =========================================================================
      // CALCUL DES POINTS DU TRIANGLE (Géométrie + Rotation)
      // =========================================================================

      // POINT 1 (xP1, yP1) : C'est la pointe du triangle (le nez/l'avant).
      // angleOffsetRecalibror (90°) pour que la pointe regarde vers le haut par défaut.
      double xP1 = size * Math.sin(Math.toRadians(60)) * Math.cos(Math.toRadians(currTurnValue + angleOffsetRecalibror));
      double yP1 = -size * Math.sin(Math.toRadians(60)) * Math.sin(Math.toRadians(currTurnValue + angleOffsetRecalibror));
      headX = xP1;
      headY = yP1;
      // Le "-" devant 'size' pour yP1 sert à inverser l'axe Y de l'écran (pour pointer vers le "vrai" haut).

      // POINT 2 (xP2, yP2) : C'est le coin arrière GAUCHE du triangle.
      double xP2 = -size * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(currTurnValue));
      double yP2 = size * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(currTurnValue));

      // POINT 3 (xP3, yP3) : C'est le coin arrière DROIT du triangle.
      double xP3 = size * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(currTurnValue));
      double yP3 = -size * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(currTurnValue));


      // =========================================================================
      // CRÉATION DE LA FORME
      // =========================================================================
      var currX = body.getLayoutX();
      var currY = body.getLayoutY();
      body.getPoints().clear();
      body.getPoints().setAll(xP1, yP1, xP2, yP2, xP3, yP3);
      body.setFill(Color.DEEPSKYBLUE);
      body.setStroke(Color.BLACK);
      body.setLayoutX(currX);
      body.setLayoutY(currY);

      // =========================================================================
      // CRÉATION DU NEZ (Repère visuel)
      // =========================================================================
      head.setTranslateX(xP1 + body.getLayoutX());
      head.setTranslateY(yP1 + body.getLayoutY());

  }

  public void moveForward(int distance) {
      if(distance <= 0 || distance > 100)
          return;

      // For a potential line
      var currX = body.getLayoutX();
      var currY = body.getLayoutY();

      // On calcule l'angle de déplacement (en radians). On ajoute PI/2 (90 degrés)
      // car l'avant de notre triangle est décalé par rapport au zéro mathématique.
      double radians = Math.toRadians(currTurnValue) + Math.PI / 2;

      // Trigonométrie pour avancer
      double diffX = distance * Math.cos(radians);  // NouveauX = cosinus(angle) * vitesse
      double diffY = distance * Math.sin(radians);  // NouveauY = sinus(angle) * vitesse



      // L'angle réel sur le cercle (0 à 360)
      double realAngle = angleOffsetRecalibror + currTurnValue;

      // On vérifie si l'angle fait pointer le personnage vers le "haut" de l'écran
      var normalized = (realAngle % 360 + 360) % 360;
      boolean facingUp = normalized < 180 && normalized > 1;

      int directionY = facingUp ? -1 : 1; // L'angle réel sur le cercle (0 à 360)
      double nextX = currX + diffX;
      double nextY = currY + (directionY * Math.abs(diffY));

      // Appliquer le déplacement à la position actuelle
      body.setLayoutX(body.getLayoutX() + diffX);
      body.setLayoutY(body.getLayoutY() + (directionY * Math.abs(diffY)));

      // When pen is down
      if(hasTrail) {
          var line = new Line(currX, currY, nextX, nextY);
          line.setStroke(Color.FORESTGREEN);
          this.getChildren().add(line);
      }

      // =========================================================================
      // CRÉATION DU NEZ (Repère visuel)
      // =========================================================================
      head.setTranslateX(headX + body.getLayoutX());
      head.setTranslateY(headY + body.getLayoutY());
  }

  public void penUp() {
      hasTrail = false;
  }

  public void penDown() {
      hasTrail = true;
  }

}