package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.Map;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private final String message;
    /** Time at which this commit was created. */
    private final Date timestamp;
    /** IDs of this commit's parent commits; SECOND_PARENT is for merges. */
    private final String parent;
    private final String secondParent;
    /** Mapping from tracked file names to blob IDs. */
    private final TreeMap<String, String> trackedFiles;

    /** Creates a commit with the supplied metadata and tracked files. */
    public Commit(String message, Date timestamp, String parent,
                  String secondParent, Map<String, String> trackedFiles) {
        this.message = message;
        this.timestamp = new Date(timestamp.getTime());
        this.parent = parent;
        this.secondParent = secondParent;
        this.trackedFiles = new TreeMap<>(trackedFiles);
    }

    /** Returns the deterministic initial commit shared by all repositories. */
    public static Commit initialCommit() {
        return new Commit("initial commit", new Date(0), null, null,
                new TreeMap<>());
    }

    /** Returns this commit's SHA-1 object ID. */
    public String id() {
        return Utils.sha1(Utils.serialize(this));
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return new Date(timestamp.getTime());
    }

    public String getParent() {
        return parent;
    }

    public String getSecondParent() {
        return secondParent;
    }
    /* TODO: fill in the rest of this class. */
}
