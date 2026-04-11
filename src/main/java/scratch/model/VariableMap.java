package scratch.model;


import java.util.*;

public class VariableMap {
    private final Map<String, Integer> map = new HashMap<>();
    private final List<VariableMapListener<String, Integer>> listeners = new ArrayList<>();

    public VariableMap() {}

    public boolean declare(String variable) {
        // check : le paramètre ne doit pas être une valeur littérale
        var finalVal = getParamValue(variable);
        boolean isLiteralValue = finalVal.isPresent();
        if(isLiteralValue) {
            System.out.println("la variable ne doit pas être une valeur littérale");
            return false;
        }

        // check : la variable déclarée ne doit pas exister
        if(map.get(variable) != null)
            return false;

        // on ajoute la variable à la map, valeur par défaut 0
        map.put(variable, 0);

        // on notifie chaque listener
        for (var listener : listeners) {
            // on crée l'objet qui contient les infos de la déclaration de variable
            MapChangeObject change = new MapChangeObject(variable, 0, ActionType.DECLARE);

            // on déclenche la réponse du listener au changement
            listener.onUpdate(change);
        }
        return true;
    }

    public boolean assign(String variable, String param) {
        // check : la variable à laquelle on doit assigner une valeur ne doit pas être une valeur littérale
        Optional<Integer> parsedVariable = getParamValue(variable);

        boolean isLiteralValue = parsedVariable.isPresent();

        if(isLiteralValue) {
            System.out.println("la variable ne doit pas être une valeur littérale");
            return false;
        }

        // check : la variable doit exister
        if(map.get(variable) == null)
            return false;

        // si la valeur assignée à la variable est une valeur littérale, on assigne à la variable cette valeur
        // sinon ce n'est pas une valeur littérale, c'est obligatoirement une variable qui a été entrée paramètre,
        // dans ce cas là, on tente de récupérer la valeur de la variable dans la map, si elle existe. sinon rien n'est fait.
        Integer parsedParameter = getParamValue(param).orElseGet(() -> getValue(param));

        // arrive quand le paramètre n'est pas une valeur littérale, et qu'il n'existe pas dans la map
        if(parsedParameter == null) {
            return false;
        }

        // on assigne la valeur récupérée à la variable
        map.put(variable, parsedParameter);

        // on notifie chaque listener
        for (var listener : listeners) {
            // on crée l'objet qui contient les infos de la déclaration de variable
            MapChangeObject change = new MapChangeObject(variable, parsedParameter, ActionType.ASSIGN);

            // on déclenche la réponse du listener au changement
            listener.onUpdate(change);
        }
        return true;
    }

    public boolean increment(String variable, String param) {
        // check : la variable à laquelle on doit incrémenter une valeur ne doit pas être une valeur littérale
        Optional<Integer> parsedVariable = getParamValue(variable);
        boolean isLiteralValue = parsedVariable.isPresent();

        if(isLiteralValue) {
            System.out.println("la variable ne doit pas être une valeur littérale");
            return false;
        }

        // check : la variable doit exister
        if(map.get(variable) == null) {
            return false;
        }

        // si la valeur assignée à la variable est une valeur littérale, on incrémente la variable de cette valeur
        // si ce n'est pas une valeur littérale, c'est obligatoirement une variable qui a été entrée paramètre,
        // dans ce cas là, on tente de récupérer la valeur de la variable dans la map, si elle existe. sinon rien n'est fait.
        Integer parsedParameter = getParamValue(param).orElseGet(() -> getValue(param));

        // arrive quand le paramètre n'est pas une valeur littérale, qu'il  n'existe pas dans la map
        if(parsedParameter == null) {
            return false;
        }

        // on incrémente la variable avec la valeur récupérée de l'increment
        Integer currValue = map.get(variable);
        currValue += parsedParameter;
        map.put(variable, currValue);

        // on notifie chaque listener
        for (var listener : listeners) {
            // on crée l'objet qui contient les infos de la déclaration de variable
            MapChangeObject change = new MapChangeObject(variable, currValue, ActionType.INCREMENT);

            // on déclenche la réponse du listener au changement
            listener.onUpdate(change);
        }
        return true;
    }

    public Optional<Integer> getParamValue(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Integer getValue(String param) {
        return map.get(param);
    }

    public void addListener(VariableMapListener<String, Integer> listener) {
        listeners.add(listener);
    }

    public void reset() {
        map.clear();
    }
}
