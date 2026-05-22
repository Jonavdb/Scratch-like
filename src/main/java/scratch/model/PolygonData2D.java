package scratch.model;

public class PolygonData2D {
    private final int lenghSides;
    private final int nbSides;

    public PolygonData2D(int lenghSides, int nbSides) {
        this.lenghSides = lenghSides;
        this.nbSides = nbSides;
    }

    public int getLenghSides() {
        return this.lenghSides;
    }
    public int getNbSides() {
        return this.nbSides;
    }
}
