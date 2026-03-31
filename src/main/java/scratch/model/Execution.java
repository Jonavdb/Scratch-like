package scratch.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Execution {

    // Explication globale : Cette classe va nous permettre de parcourir les différentes parties de notre arbre d'actions. Une fois qu'on a rencontré un repeat, on descend d'un niveau et chaque niveau est composé
    // d'un index, d'une liste d'actions et du nombre de répétitions de ce niveau (initialisé à 1 pour le niveau principal). Le stack est donc une composition de plusieurs niveaux contenent des sous-niveaux à chaque
    // repeat rencontrés. Une fois un sous-niveau fini, on le "supprime" des niveaux et on reprend au niveau "précédent" en avançant l'index de 1.

    private static class Level {
        List<Actionable> list;
        int index;
        int remainingRepeats;
        int globalIndex;

        Level(List<Actionable> list, int index, int remainingRepeats, int globalIndex){
            this.index = index; // où on se trouve dans la liste de l'étage actuel
            this.list = list; // la liste d'action actuelle
            this.remainingRepeats = remainingRepeats; // le nombre de repeat restant pour la liste actuelle
            this.globalIndex = globalIndex;
        }
    }

    private final Stack<Level> levels = new Stack<>(); //
    private int globalIndex = 0;

    public void init(List<Actionable> listInitial){
        levels.clear(); // on vide entièrement le stack
        globalIndex = 0;
        List<Actionable> tree = listIntoTree(listInitial); // on transforme la liste plate en arbre et on initialise l'arbre
        levels.push(new Level(tree, 0, 1, 0)); // On commence au début de l'arbre, à l'index 0 et 1 seule répetition pour la raxine de l'arbre
    }

    public Actionable getNextAction() { // Méthode qui va nous permettre de réxupérer l'action suivante
        while (!levels.isEmpty()) {
            Level top = levels.peek(); // on regarde le niveau actuel

            if (top.index >= top.list.size()) {
                top.remainingRepeats--; // si l'index est plus grand que la liste de l'étage en cours, on décrémente le compteur
                if (top.remainingRepeats > 0) {
                    top.index = 0; // s'il reste des repeats, on repart au début mais dans cet étage
                } else {
                    levels.pop(); // si plus de repétitions, on sort de cet étage.
                    if (!levels.isEmpty()) {
                        levels.peek().index++; // si la pile n'est pas vide, on passe à l'action suivante de l'étage du dessus
                    }
                }
                continue;
            }
            Actionable current = top.list.get(top.index); // on récupère l'action courante de l'étage actuel

            if (current instanceof Repeat) {
                Repeat repeat = (Repeat) current;
                int n = Integer.parseInt(repeat.getTick()); // si l'action est un repeat, on récupère son nombre de répétitions
                levels.push(new Level(repeat.getChildren(), 0, n, globalIndex + 1));
            } else {
                top.index++; // si pas repeat on avance dans l'index
            }
            if (top.index >= top.list.size() && top.remainingRepeats > 1) {
                globalIndex = top.globalIndex;
            } else {
                globalIndex++;
            }
            return current; // on retourne l'action courante à executer
        }
        return null; // si pile vide, on quitte
    }

    public boolean isDone(){
        return levels.isEmpty(); // La pile est vide cad que tout le programme a été joué
    }


    private List<Actionable> listIntoTree(List<Actionable> basicList){ // méthode pour transformer une liste plate en arbre
        List<Actionable> tree = new ArrayList<>(); // on déclare une nouvelle liste
        Stack<List<Actionable>> stack = new Stack<>(); // on déclare un Stack qui sera un stack de List<Actionable>
        stack.push(tree); // on rentre dedans
        for(Actionable action : basicList) { // on itère sur tous les actionables de la liste
            if (action instanceof Repeat){ //
                Repeat repeat = (Repeat) action.copy();
                stack.peek().add(repeat); // si on croise un repeat, on ajoute ce repeat
                stack.push(repeat.getChildren()); // on rentre dedans
            } else if (action instanceof EndRepeat) {
                if (stack.size() > 1){
                    stack.pop(); // Si on croise un end repeat, on pop l'étage actuel pour revenir à l'étage supérieur
                    stack.peek().add(action.copy());
                }
            } else {
                stack.peek().add(action.copy()); // sinon, on ajoute simplement l'action à la pile
            }
        }
        return tree;
    }

    public int getGlobalIndex() {
        return globalIndex;
    }
}
