package scratch.model;

public interface VariableMapListener<K,V> {
    void onUpdate(MapChangeObject change);
}
