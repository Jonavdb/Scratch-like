package scratch.model;
import javafx.beans.property.*;


public class Player {
    private final ObjectProperty<CustomPoint2D> position = new SimpleObjectProperty<>(new CustomPoint2D(250,250));
    private final DoubleProperty angle = new SimpleDoubleProperty(0);
    private final DoubleProperty size = new SimpleDoubleProperty(15);
    private final BooleanProperty penDown = new SimpleBooleanProperty(true);


    public CustomPoint2D getPosition(){
        return position.get();
    }

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
}
