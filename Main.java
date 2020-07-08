package gitlet;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Devyanshi Agarwal
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        Commands c = new Commands();
        if (args.length == 0) {
            Utils.message("Please enter a command.");
            System.exit(0);
        }
        if (args[0].equals("init")) {
            if (initialized()) {
                Utils.message("A Gitlet version control "
                        + "system already exists in the current directory.");
            } else {
                c.init();
            }
        } else {
            if (!initialized()) {
                Utils.message("Not in an initialized Gitlet directory.");
                System.exit(0);
            } else {
                otherCmnds(args);
            }
        }
    }

    /** Checks if repo is initialized returns BOOLEAN. */
    public static boolean initialized() {
        File cwd = new File(System.getProperty("user.dir"));
        File gitlet = new File(cwd, ".gitlet");
        return gitlet.exists();
    }

    /** Other commands String ARGS. */
    public static void otherCmnds(String... args) throws IOException {
        Commands c = new Commands();
        if (args[0].equals("add")) {
            if (args.length == 1) {
                Utils.message("Please enter file name");
            } else {
                c.add(args[1]);
                System.exit(0);
            }
        } else if (args[0].equals("commit")) {
            if (args.length == 1 || args[1].trim().equals("")) {
                Utils.message("Please enter a commit message");
            } else {
                c.commit(args[1]);
                System.exit(0);
            }
        } else if (args[0].equals("rm")) {
            if (args.length == 1) {
                Utils.message("Please enter the file name");
            } else {
                c.rm(args[1]);
                System.exit(0);
            }
        } else if (args[0].equals("log")) {
            c.log();
            System.exit(0);
        } else if (args[0].equals("global-log")) {
            c.globalLog();
            System.exit(0);
        } else {
            restCmnds(args);
        }
    }

    /** Other commands String ARGS. */
    public static void restCmnds(String... args) {
        Commands c = new Commands();
        if (args[0].equals("find")) {
            if (args.length == 1) {
                Utils.message("Please enter a commit message");
            } else {
                c.find(args[1]);
                System.exit(0);
            }
        } else if (args[0].equals("checkout")) {
            if (args.length == 4 || args.length == 3) {
                c.checkout(Arrays.copyOfRange(args, 1, args.length));
            } else if (args.length == 2) {
                c.checkoutBranch(args[1]);
                System.exit(0);
            }
        } else if (args[0].equals("status")) {
            c.status();
            System.exit(0);
        } else if (args[0].equals("branch")) {
            if (args.length == 1) {
                Utils.message("Incorrect Operands");
            } else {
                c.branch(args[1]);
                System.exit(0);
            }
        }  else if (args[0].equals("rm-branch")) {
            if (args.length != 2) {
                Utils.message("Incorrect Operands");
            } else {
                c.rmBranch(args[1]);
                System.exit(0);
            }
        } else if (args[0].equals("reset")) {
            if (args.length != 2) {
                Utils.message("Incorrect operands.");
            } else {
                c.reset(args[1]);
                System.exit(0);
            }
        } else if (args[0].equals("merge")) {
            if (args.length != 2) {
                Utils.message("Incorrect operands.");
            }
        } else {
            Utils.message("No command with that name exists.");
        }
    }
}
