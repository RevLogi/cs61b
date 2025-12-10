package gitlet;

import java.util.Objects;

import static gitlet.Repository.GITLET_DIR;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command."); // FIX: Specific message required
            System.exit(0);
        }
        String firstArg = args[0];
        if (args[0].equals("init")){
            if (args.length != 1) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            Repository.init();
            return;
        }
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        switch(firstArg) {
            case "add":
                if (args.length !=2) {
                    incorrectOps();
                }
                Repository.add(args[1]);
                break;
            case "commit":
                if (args.length == 1) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                } else if (args.length != 2) {
                    incorrectOps();
                }
                Repository.commit(args[1]);
                break;
            case "rm":
                if (args.length != 2) {
                    incorrectOps();
                }
                Repository.remove(args[1]);
                break;
            case "log":
                if (args.length != 1) {
                    incorrectOps();
                }
                Repository.log();
                break;
            case "global-log":
                if (args.length != 1) {
                    incorrectOps();
                }
                Repository.global();
                break;
            case "find":
                if (args.length != 2) {
                    incorrectOps();
                }
                Repository.find(args[1]);
                break;
            case "status":
                if (args.length != 1) {
                    incorrectOps();
                }
                Repository.status();
                break;
            case "checkout":
                if (args.length == 3 && args[1].equals("--")) {
                    Repository.checkFile(args[2]);
                } else if (args.length == 2) {
                    Repository.checkCommit(args[1]);
                } else if (args.length == 4 && args[2].equals("--")){
                    Repository.checkFile(args[1], args[3]);
                } else {
                    System.out.println("Incorrect operands");
                    System.exit(0);
                }
                break;
            case "branch":
                if (args.length != 2) {
                    incorrectOps();
                }
                Repository.branch(args[1]);
                break;

            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    public static void incorrectOps() {
        System.out.println("Incorrect operands.");
        System.exit(0);
    }
}
