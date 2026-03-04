package scratch.model;

import javafx.beans.property.*;
import javafx.geometry.Point2D;

public class Player {
    private final ObjectProperty<Point2D> position = new SimpleObjectProperty<>(new Point2D(250,250));
    private final DoubleProperty angle = new SimpleDoubleProperty(0);
    private final DoubleProperty size = new SimpleDoubleProperty(15);
    private final BooleanProperty penDown = new SimpleBooleanProperty(true);


    public Point2D getPosition(){
        return position.get();
    }

    public void setPosition(Point2D value){
        position.setValue(value);
    }

    public ObjectProperty<Point2D> positionProperty(){
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
}
