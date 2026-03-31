package scratch.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VariableMap {
    private final Map<String, Integer> map = new HashMap<>();

    public VariableMap() {}

    public boolean declare(String variable) {
        // check : le paramètre ne doit pas être une valeur littérale
        var finalVal = tryGetLiteral(variable);
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
        return true;
    }

    public boolean assign(String variable, String param) {
        // check : la variable à laquelle on doit assigner une valeur ne doit pas être une valeur littérale
        var parsedVariable = tryGetLiteral(variable);
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
        Integer parsedParameter = tryGetLiteral(param)
                .orElseGet(() -> getValue(param));

        // arrive quand le paramètre n'est pas une valeur littérale, et qu'il n'existe pas dans la map
        if(parsedParameter == null)
            return false;

        // on assigne la valeur récupérée à la variable
        map.put(variable, parsedParameter);
        return true;
    }

    public boolean increment(String variable, String param) {
        // check : la variable à laquelle on doit incrémenter une valeur ne doit pas être une valeur littérale
        var parsedVariable = tryGetLiteral(variable);
        boolean isLiteralValue = parsedVariable.isPresent();
        if(isLiteralValue) {
            System.out.println("la variable ne doit pas être une valeur littérale");
            return false;
        }

        // check : la variable doit exister
        if(map.get(variable) == null)
            return false;

        // si la valeur assignée à la variable est une valeur littérale, on incrémente la variable de cette valeur
        // si ce n'est pas une valeur littérale, c'est obligatoirement une variable qui a été entrée paramètre,
        // dans ce cas là, on tente de récupérer la valeur de la variable dans la map, si elle existe. sinon rien n'est fait.
        Integer parsedParameter = tryGetLiteral(param)
                .orElseGet(() -> getValue(param));

        // arrive quand le paramètre n'est pas une valeur littérale, qu'il  n'existe pas dans la map
        if(parsedParameter == null)
            return false;

        // on incrémente la variable avec la valeur récupérée de l'increment
        Integer currValue = map.get(variable);
        currValue += parsedParameter;
        map.put(variable, currValue);
        return true;
    }

    public Optional<Integer> tryGetLiteral(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Integer getValue(String param) {
        return map.get(param);
    }
}
