package gitlet;

import com.sun.tools.hat.internal.model.Root;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author xuzhipeng
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        checkArgsNotEmpty(args);
        String firstArg = args[0];
        switch(firstArg) {
            case "init": {
                checkArgsNum(args, 1);
                checkIfNeedGitletDirectory(false);
                Repository.GITLET_DIR.mkdirs();
                Repository.REFS_DIR.mkdirs();
                Repository.HEADS_DIR.mkdirs();
                Repository.OBJECTS_DIR.mkdirs();
                Repository.COMMITS_DIR.mkdirs();
                Repository.BLOBS_DIR.mkdirs();
                Commit initCommit = Commit.initialCommit();
                String initCommitId = initCommit.id();
                /** save the initial commit */
                Repository.storeCommit(initCommit);
                /** initialize master, points to the initial commit */;
                writeContents(Repository.MASTER, initCommitId);
                /** initialize HEAD, points to master */
                writeContents(Repository.HEAD, "master");
                /** initialize STAGE */
                writeObject(Repository.STAGE, Stage.initialStage());
                break;
            }
            case "add": {
                checkArgsNum(args, 2);
                checkIfNeedGitletDirectory(true);
                String fileName = args[1];
                File file = join(Repository.CWD, fileName);
                if (!file.exists()) {
                    System.out.println("File does not exist.");
                    System.exit(0);
                }
                byte[] fileContents = readContents(file);
                String fileId = sha1((Object) fileContents);
                Stage stagingArea = readObject(Repository.STAGE, Stage.class);
                /**
                 * If the current working version of the file is identical to
                 * the version in the current commit, do not stage it to be added,
                 * and remove it from the staging area if it is already there.
                 */
                Commit currentCommit = Repository.readCurrentCommit();
                boolean sameVersionTracked = false;
                TreeMap<String, String> trackedFiles = currentCommit.getTrackedFiles();
                if (trackedFiles.containsKey(fileName)) {
                    sameVersionTracked = trackedFiles.get(fileName).equals(fileId);
                }
                if (sameVersionTracked) {
                    stagingArea.removeFromAdditions(fileName);
                }
                /**
                 * Staging an already-staged file overwrites the previous entry
                 * in the staging area with the new contents.
                 */
                else if (stagingArea.getAdditions().containsKey(fileName) &&
                        !fileId.equals(stagingArea.idInAdditions(fileName))) {
                    stagingArea.putInAdditions(fileName, fileId);
                    Repository.storeBlob(fileContents, fileId);
                }
                /** cancel removal */
                stagingArea.removeFromRemovals(fileName);
                /** store stage */
                writeObject(Repository.STAGE, stagingArea);
                break;
            }
            case "commit": {
                checkArgsNum(args, 2);
                checkIfNeedGitletDirectory(true);
                Stage stagingArea = readObject(Repository.STAGE, Stage.class);
                if (stagingArea.isEmpty()) {
                    System.out.println("No changes added to the commit.");
                    System.exit(0);
                }
                String message = args[1];
                if (message.isEmpty()) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Commit currentCommit = Repository.readCurrentCommit();
                TreeMap<String, String> trackedFiles = currentCommit.getTrackedFiles();
                TreeMap<String, String> additions = stagingArea.getAdditions();
                TreeSet<String> removals = stagingArea.getRemovals();
                for (String fileName : additions.keySet()) {
                    trackedFiles.put(fileName, additions.get(fileName));
                }
                for (String fileName : removals) {
                    trackedFiles.remove(fileName);
                }
                Commit newCommit = new Commit(
                        message,
                        new Date(),
                        currentCommit.id(),
                        null,
                        trackedFiles
                );
                String newCommitId = newCommit.id();
                Repository.storeCommit(newCommit);
                /** Make current branch head point to the new commit */
                File head = join(Repository.HEADS_DIR, readContentsAsString(Repository.HEAD));
                writeContents(head, newCommitId);
                stagingArea.clear();
                break;
            }
            case "rm": {
                checkArgsNum(args, 2);
                checkIfNeedGitletDirectory(true);
                String fileName = args[1];
                Commit currentCommit = Repository.readCurrentCommit();
                File file = join(Repository.CWD, fileName);
                Stage stagingArea = readObject(Repository.STAGE, Stage.class);
                if (!currentCommit.tracksFile(fileName)
                        && !stagingArea.getAdditions().containsKey(fileName)) {
                    System.out.println("No reason to remove the file.");
                    System.exit(0);
                }
                if (currentCommit.tracksFile(fileName)) {
                    stagingArea.putInRemovals(fileName);
                    restrictedDelete(file);
                }
                if (stagingArea.getAdditions().containsKey(fileName)) {
                    stagingArea.removeFromAdditions(fileName);
                }
                writeObject(Repository.STAGE, stagingArea);
                break;
            }
            case "log": {
                checkArgsNum(args, 1);
                checkIfNeedGitletDirectory(true);
                Commit commitNode = Repository.readCurrentCommit();
                while (!(commitNode == null)) {
                    printCommit(commitNode);
                    if (!(commitNode.getParentId() == null)) {
                        commitNode = readObject(Repository.getCommitFile(commitNode.getParentId()), Commit.class);
                    }
                    else break;
                }
                break;
            }
            case "global-log": {
                checkArgsNum(args, 1);
                checkIfNeedGitletDirectory(true);
                File[] commitDirs = Repository.COMMITS_DIR.listFiles(File::isDirectory);
                if (commitDirs != null) {
                    for (File commitDir : commitDirs) {
                        File[] commitFiles = commitDir.listFiles(File::isFile);
                        if (commitFiles == null) {
                            continue;
                        }
                        for (File commitFile : commitFiles) {
                            Commit commit = readObject(commitFile, Commit.class);
                            printCommit(commit);
                            }
                        }
                    }
                }
                break;
            case "find": {
                checkArgsNum(args, 2);
                checkIfNeedGitletDirectory(true);
                String commitMessage = args[1];
                boolean suchCommitExists = false;
                File[] commitDirs = Repository.COMMITS_DIR.listFiles(File::isDirectory);
                if (commitDirs != null) {
                    for (File commitDir : commitDirs) {
                        File[] commitFiles = commitDir.listFiles(File::isFile);
                        if (commitFiles == null) {
                            continue;
                        }
                        for (File commitFile : commitFiles) {
                            Commit commit = readObject(commitFile, Commit.class);
                            if (commitMessage.equals(commit.getMessage())) {
                                System.out.println(commit.id());
                                suchCommitExists = true;
                            }
                        }
                    }
                }
                if (!suchCommitExists) {
                    System.out.println("Found no commit with that message.");
                    System.exit(0);
                }
                break;
            }
            case "status": {
                checkArgsNum(args, 1);
                checkIfNeedGitletDirectory(true);

                System.out.println("=== Branches ===");
                String currentBranchName = readContentsAsString(Repository.HEAD);
                List<String> branchNames = plainFilenamesIn(Repository.HEADS_DIR);
                if (branchNames != null) {
                    for (String branchName : branchNames) {
                        if (branchName.equals(currentBranchName)) {
                            System.out.println("*" + branchName);
                        } else {
                            System.out.println(branchName);
                        }
                    }
                }
                System.out.println();

                System.out.println("=== Staged Files ===");
                Stage stagingArea = readObject(Repository.STAGE, Stage.class);
                for (String fileName : stagingArea.getAdditions().keySet()) {
                    System.out.println(fileName);
                }
                System.out.println();

                System.out.println("=== Removed Files ===");
                for (String fileName : stagingArea.getRemovals()) {
                    System.out.println(fileName);
                }
                System.out.println();

                System.out.println("=== Modifications Not Staged For Commit ===");
                Commit currentCommit = Repository.readCurrentCommit();
                TreeMap<String, String> trackedFiles = currentCommit.getTrackedFiles();
                TreeMap<String, String> stagedFiles = stagingArea.getAdditions();
                TreeSet<String> stagedFilesForRemovals = stagingArea.getRemovals();
                List<String> fileNames = plainFilenamesIn(Repository.CWD);
                if (fileNames != null) { // avoid NullPointException when call fileNames instance method
                    /** iterate tracked files, may be modified or deleted in CWD */
                    for (String trackedFileName : trackedFiles.keySet()) {
                        boolean staged = stagedFiles.containsKey(trackedFileName);
                        boolean inCWD = fileNames.contains(trackedFileName);
                        if (!staged && inCWD) {
                            String fileId = sha1((Object) readContents(join(Repository.CWD, trackedFileName)));
                            if (!fileId.equals(trackedFiles.get(trackedFileName))) {
                                System.out.println(trackedFileName + " (modified)");
                            }
                        }
                        if (!inCWD && !stagedFilesForRemovals.contains(trackedFileName)) {
                            System.out.println(trackedFileName + " (deleted)");
                        }
                    }
                    /** iterate staged files, may be modified or deleted in CWD */
                    for (String stagedFileName : stagedFiles.keySet()) {
                        boolean inCWD = fileNames.contains(stagedFileName);
                        if (inCWD) {
                            String fileId = sha1((Object) readContents(join(Repository.CWD, stagedFileName)));
                            if (!fileId.equals(stagingArea.getAdditions().get(stagedFileName))) {
                                System.out.println(stagedFileName + " (modified)");
                            }
                        } else {
                            System.out.println(stagedFileName + " (deleted)");
                        }
                    }
                }
                System.out.println();

                System.out.println("=== Untracked Files ===");
                if (fileNames != null) {
                    /** iterate CWD files, may be untracked */
                    for (String fileName : fileNames) {
                        if (Repository.isUntracked(fileName)) {
                            System.out.println(fileName + " (Untracked)");
                        }
                    }
                }
                System.out.println();
                break;
            }
            case "checkout": {
                checkIfNeedGitletDirectory(true);
                if (args.length == 3 && args[1].equals("--")) {
                    /** checkout -- [file name] */
                    String fileName = args[2];
                    Commit currentCommit = Repository.readCurrentCommit();
                    TreeMap<String, String> trackedFiles = currentCommit.getTrackedFiles();
                    if (!trackedFiles.containsKey(fileName)) {
                        System.out.println("File does not exist in that commit.");
                        System.exit(0);
                    } else {
                        String trackedFileId = trackedFiles.get(fileName);
                        byte[] fileContents = readContents(Repository.getBlobFile(trackedFileId));
                        writeContents(join(Repository.CWD, fileName), (Object) fileContents);
                    }
                } else if (args.length == 4 && args[2].equals("--")) {
                    /** checkout [commit id] -- [file name] */
                    String fileName = args[3];
                    String commitId = args[1];
                    File commitFile = Repository.getCommitFile(commitId);
                    if (!commitFile.exists()) {
                        System.out.println("No commit with that id exists.");
                        System.exit(0);
                    } else {
                        Commit checkoutCommit = readObject(commitFile, Commit.class);
                        TreeMap<String, String> trackedFiles = checkoutCommit.getTrackedFiles();
                        if (!trackedFiles.containsKey(fileName)) {
                            System.out.println("File does not exist in that commit.");
                            System.exit(0);
                        } else  {
                            String trackedFileId = trackedFiles.get(fileName);
                            byte[] fileContents = readContents(Repository.getBlobFile(trackedFileId));
                            writeContents(join(Repository.CWD, fileName), (Object) fileContents);
                        }
                    }
                } else if (args.length == 2) {
                    /** checkout [branch] */
                    String branchName = args[1];
                    String currentBranchName = readContentsAsString(Repository.HEAD);
                    List<String> branchNames = plainFilenamesIn(Repository.HEADS_DIR);
                    if (!branchNames.contains(branchName)) {
                        System.out.println("No such branch exists.");
                        System.exit(0);
                    }
                    if (branchName.equals(currentBranchName)) {
                        System.out.println("No need to checkout the current branch.");
                        System.exit(0);
                    }
                    String checkoutCommitId = readContentsAsString(join(Repository.HEADS_DIR, branchName));
                    Commit checkoutCommit = readObject(Repository.getCommitFile(checkoutCommitId), Commit.class);
                    List<String> fileNamesInCWD = plainFilenamesIn(Repository.CWD);
                    Commit currentCommit = Repository.readCurrentCommit();
                    if (fileNamesInCWD != null) {
                        for (String fileName : checkoutCommit.getTrackedFiles().keySet()) {
                            if (fileNamesInCWD.contains(fileName) &&
                                    !currentCommit.getTrackedFiles().containsKey(fileName)) {
                                System.out.println(fileName +
                                        "There is an untracked file in the way; " +
                                        "delete it, or add and commit it first.");
                                System.exit(0);
                            }
                        }
                    }
                    /** Takes all files in the commit at the head of the given branch,
                     *  and puts them in the working directory, overwriting the versions
                     *  of the files that are already there if they exist.  */
                    for (String fileName : checkoutCommit.getTrackedFiles().keySet()) {
                        String fileId = checkoutCommit.getTrackedFiles().get(fileName);
                        byte[] fileContents = readContents(Repository.getBlobFile(fileId));
                        writeContents(join(Repository.CWD, fileName), (Object) fileContents);
                    }
                    /** Any files that are tracked in the current branch but are not
                     *  in the checked-out branch are deleted. */
                    if (fileNamesInCWD != null) {
                        for (String fileName : currentCommit.getTrackedFiles().keySet()) {
                            if (!checkoutCommit.getTrackedFiles().containsKey(fileName)) {
                                restrictedDelete(join(Repository.CWD, fileName));
                            }
                        }
                    }
                    /** The staging area is cleared. */
                    Stage stagingArea = readObject(join(Repository.STAGE), Stage.class);
                    stagingArea.clear();
                    /** The given branch will now be considered the current branch (HEAD). */
                    writeContents(Repository.HEAD, branchName);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            }
            case "branch": {
                checkIfNeedGitletDirectory(true);
                checkArgsNum(args, 2);
                String branchName = args[1];
                List<String> branchNames = plainFilenamesIn(Repository.HEADS_DIR);
                if (branchNames.contains(branchName)) {
                    System.out.println("A branch with that name already exists.");
                    System.exit(0);
                }
                Commit currentCommit = Repository.readCurrentCommit();
                writeContents(join(Repository.HEADS_DIR, branchName), currentCommit.id());
                break;
            }
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }

    private static void checkArgsNum(String[] args, int num) {
        if (args.length != num) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static void checkArgsNotEmpty(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
    }

    private static void checkIfNeedGitletDirectory(boolean need) {
        if (need && !Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static void printCommit(Commit commitNode) {
        System.out.println("===");
        System.out.println("commit " + commitNode.id());
        if (!(commitNode.getSecondParentId() == null)) {
            System.out.println("Merge: "
                    + commitNode.getParentId().substring(0, 7) + " "
                    + commitNode.getSecondParentId().substring(0, 7));
        }
        System.out.printf(
                Locale.ENGLISH,
                "Date: %1$ta %1$tb %1$te %1$tT %1$tY %1$tz%n",
                commitNode.getTimestamp());
        System.out.println(commitNode.getMessage());
        System.out.println();
    }

}
