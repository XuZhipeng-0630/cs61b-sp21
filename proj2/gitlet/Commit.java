package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Utils.serialize;
import static gitlet.Utils.sha1;

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
    private final String parentId;
    private final String secondParentId;
    /** Mapping from tracked file names to blob IDs. */
    private final TreeMap<String, String> trackedFiles;

    /** Creates a commit with the supplied metadata and tracked files. */
    public Commit(String message, Date timestamp, String parentId,
                  String secondParentId, Map<String, String> trackedFiles) {
        this.message = message;
        this.timestamp = new Date(timestamp.getTime());
        this.parentId = parentId;
        this.secondParentId = secondParentId;
        this.trackedFiles = new TreeMap<>(trackedFiles);
    }

    /** Returns the deterministic initial commit shared by all repositories. */
    public static Commit initialCommit() {
        return new Commit("initial commit", new Date(0), null, null,
                new TreeMap<>());
    }

    /** Returns this commit's SHA-1 object ID. */
    public String id() {
        return sha1(serialize(this));
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return new Date(timestamp.getTime());
    }

    public String getParentId() {
        return parentId;
    }

    public String getSecondParentId() {
        return secondParentId;
    }

    public TreeMap<String, String> getTrackedFiles() {
        return trackedFiles;
    }

    public boolean tracksFile(String fileName) {
        return trackedFiles.containsKey(fileName);
    }
    /* TODO: fill in the rest of this class. */
}
