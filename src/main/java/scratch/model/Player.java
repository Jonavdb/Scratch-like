package scratch.model;
import javafx.beans.property.*;


public class Player {
    private final ObjectProperty<CustomPoint2D> position = new SimpleObjectProperty<>(new CustomPoint2D(250,250));
    private final ObjectProperty<PolygonData2D> polygon = new SimpleObjectProperty<>(new PolygonData2D(0,0));
    private final DoubleProperty angle = new SimpleDoubleProperty(0);
    private final DoubleProperty size = new SimpleDoubleProperty(15);
    private final BooleanProperty penDown = new SimpleBooleanProperty(true);
    private final BooleanProperty canDraw = new SimpleBooleanProperty(false);

    public void setPosition(CustomPoint2D value){ position.set(value); }

    public void setPolygon(PolygonData2D data) {polygon.set(data);}
    public ObjectProperty<CustomPoint2D> positionProperty(){
        return position;
    }

    public ObjectProperty<PolygonData2D> polygonProperty(){
        return polygon;
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
    public boolean isDrawEnabled() {
        return canDraw.get();
    }

    public void setPenDown(boolean value){
        penDown.set(value);
    }
    public void setDrawEnabledProperty(boolean value) {canDraw.set(value);}


    public BooleanProperty penDownProperty() {
        return penDown;
    }
    public BooleanProperty drawEnabledProperty() {
        return canDraw;
    }

    public void resetVariables() {
        position.set(new CustomPoint2D(250, 250)); // on reinitialise la position du joueur au milieu de la grille
        penDown.set(true);
        angle.set(0); // on réinitialise l'angle du joueur (pointe vers le haut = 90°)
    }

    public void moveForward(int distance) {
        double radians = Math.toRadians(this.getAngle()) + Math.PI / 2;
        double diffX = distance * Math.cos(radians);
        double diffY = distance * Math.sin(radians);
        this.setPosition(new CustomPoint2D(this.getX() + diffX, this.getY() - diffY));
    }

    public void drawPolygon(int length, int nbSides) {
        setDrawEnabledProperty(true);
        this.setPolygon(new PolygonData2D(length, nbSides));
        setPosition(new CustomPoint2D(this.getX(), this.getY())); // on met le jouueur à sa position actuelle juste pour trigger le draw
                                                                // au moment du déplacement
        this.setDrawEnabledProperty(false);
    }

    public void turnLeft(int angle) {
        this.setAngle(this.getAngle() + angle);
    }

    public void turnRight(int angle) {
        this.setAngle(this.getAngle() - angle);
    }
}
