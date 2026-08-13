package gitlet;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;

public class Stage implements Serializable {
    private final TreeMap<String, String> additions;
    private final TreeSet<String> removals;

    public Stage(TreeMap<String, String> additions, TreeSet<String> removals) {
        this.additions = additions;
        this.removals = removals;
    }

    public static Stage initialStage() {
        return new Stage(new TreeMap<>(), new TreeSet<>());
    }

    public void clear() {
        additions.clear();
        removals.clear();
    }

    public String idInAdditions(String fileName) {
        return additions.get(fileName);
    }

    public void putInAdditions(String fileName, String fileId) {
        additions.put(fileName, fileId);
    }

    public void  putInRemovals(String fileName) {
        removals.add(fileName);
    }

    public void removeFromAdditions(String fileName) {
        additions.remove(fileName);
    }

    public void removeFromRemovals(String fileName) {
        removals.remove(fileName);
    }

    public boolean isEmpty() {
        return additions.isEmpty() && removals.isEmpty();
    }

    public TreeMap<String, String> getAdditions() {
        return additions;
    }

    public TreeSet<String> getRemovals() {
        return removals;
    }
}
