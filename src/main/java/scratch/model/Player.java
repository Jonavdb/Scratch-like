package scratch.model;
import javafx.beans.property.*;

import java.util.HashMap;
import java.util.Map;


public class Player {
    private final ObjectProperty<CustomPoint2D> position = new SimpleObjectProperty<>(new CustomPoint2D(250,250));
    private final DoubleProperty angle = new SimpleDoubleProperty(0);
    private final DoubleProperty size = new SimpleDoubleProperty(15);
    private final BooleanProperty penDown = new SimpleBooleanProperty(true);

    public void setPosition(CustomPoint2D value){
        position.setValue(value);
    }

    public ObjectProperty<CustomPoint2D> positionProperty(){
        return position;
    }

    public double getX() {
        return position.get().getX();
    }

    public double getY() {
        return position.get().getY();
    }

    public double getAngle() {
        return angle.get();
    }

    public void setAngle(double value){
        angle.set(value);
    }

    public DoubleProperty angleProperty() {
        return angle;
    }

    public double getSize() {return size.get();}
    public void setSize(double value) {size.set(value);}
    public DoubleProperty sizeProperty() {return size;}

    public boolean isPenDown() {
        return penDown.get();
    }

    public void setpenDown(boolean value){
        penDown.set(value);
    }

    public BooleanProperty penDownProperty() {
        return penDown;
    }

    public void resetVariables() {
        position.set(new CustomPoint2D(250, 250)); // on reinitialise la position du joueur au milieu de la grille
        angle.set(0); // on réinitialise l'angle du joueur (pointe vers le haut = 90°)
    }
}
