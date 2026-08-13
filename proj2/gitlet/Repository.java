package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author xuzhipeng
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet/ directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The .gitlet/objects/ directory. */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /** The .gitlet/objects/commits/ directory. */
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");
    /** The .gitlet/objects/blobs/ directory. */
    public static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");
    /** The .gitlet/refs/ directory. */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** The .gitlet/HEAD file. */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /** The .gitlet/refs/heads/ directory. */
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    /** The .gitlet/refs/heads/master file. */
    public static final File MASTER = join(HEADS_DIR, "master");
    /** The .gitlet/stage file. */
    public static final File STAGE = join(GITLET_DIR, "stage");

    /* TODO: fill in the rest of this class. */
    /**
     * stores object(commit or blob) in .gitlet/objects/commits/xx/yyyy....
     * or in .gitlet/objects/blobs/xx/yyyy....
     *
     * @param path file path, .gitlet/objects/commits/ or .gitlet/objects/blobs/
     * @param object commit or blob
     * @param objectId xxyyyy....
     */
    private static void storeObject(File path, Serializable object, String objectId) {
        File subdirectory = join(path, objectId.substring(0, 2));
        subdirectory.mkdirs();

        File objectFile =
                join(subdirectory, objectId.substring(2));
        writeObject(objectFile, object);
    }

    public static void storeCommit(Commit commit) {
        storeObject(COMMITS_DIR, commit, commit.id());
    }

    public static void storeBlob(byte[] contents, String blobId) {
        storeObject(BLOBS_DIR, contents, blobId);
    }

    public static Commit readCurrentCommit() {
        String currentBranch = readContentsAsString(HEAD);
        File head = join(HEADS_DIR, currentBranch);
        String currentCommitId = readContentsAsString(head);
        return readObject(getCommitFile(currentCommitId), Commit.class);
    }

    public static File getCommitFile(String commitId) {
        return join(COMMITS_DIR, commitId.substring(0, 2), commitId.substring(2));
    }

    public static File getBlobFile(String blobId) {
        return join(BLOBS_DIR, blobId.substring(0, 2), blobId.substring(2));
    }

    public static boolean isUntracked(String fileName) {
        Commit currentCommit = readCurrentCommit();
        Stage stagingArea = readObject(STAGE, Stage.class);
        boolean tracked = currentCommit.getTrackedFiles().containsKey(fileName);
        boolean staged = stagingArea.getAdditions().containsKey(fileName);
        if (!tracked && !staged) {
            return true;
        }
        return stagingArea.getRemovals().contains(fileName);
    }

}
