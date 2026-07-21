package gitlet;

import java.io.File;
import java.util.Objects;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author xuzhipeng
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        checkArgsNotEmpty(args);
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                checkValidArgsNum(args, 1);
                checkIfNeedGitletDirectoryInitialized(false);
                Repository.GITLET_DIR.mkdir();
                Commit initCommit = Commit.initialCommit();
                File f = Repository.getObjectFile(initCommit.id());
                f.mkdir();
                Utils.writeObject(f, initCommit);
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }

    private static void checkValidArgsNum(String[] args, int num) {
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

    private static void checkIfNeedGitletDirectoryInitialized(boolean needGitletDirectoryInitialized) {
        if (needGitletDirectoryInitialized && !gitletDirectoryExisted()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static boolean gitletDirectoryExisted() {
        return Repository.GITLET_DIR.exists();
    };
}
