package gitlet;

import java.util.Objects;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.error;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        try {
            String firstArg = args[0];
            if (args[0].equals("init")) {
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
            switch (firstArg) {
                case "add":
                    if (args.length != 2) {
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
                    } else if (args[1].equals("")) {
                        System.out.println("Please enter a commit message.");
                        System.exit(0);
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
                    } else if (args.length == 4 && args[2].equals("--")) {
                        Repository.checkFile(args[1], args[3]);
                    } else {
                        incorrectOps();
                    }
                    break;
                case "branch":
                    if (args.length != 2) {
                        incorrectOps();
                    }
                    Repository.branch(args[1]);
                    break;
                case "rm-branch":
                    if (args.length != 2) {
                        incorrectOps();
                    }
                    Repository.rmBranch(args[1]);
                    break;
                case "reset":
                    if (args.length != 2) {
                        incorrectOps();
                    }
                    Repository.reset(args[1]);
                    break;
                case "merge":
                    if (args.length != 2) {
                        incorrectOps();
                    }
                    Repository.merge(args[1]);
                    break;
                case "add-remote":
                    if (args.length != 3) {
                        incorrectOps();
                    }
                    Repository.addRemote(args[1], args[2]);
                    break;
                case "rm-remote":
                    if (args.length != 2) {
                        incorrectOps();
                    }
                    Repository.rmRemote(args[1]);
                    break;
                case "push":
                    if (args.length != 3) {
                        incorrectOps();
                    }
                    Repository.push(args[1], args[2]);
                    break;
                case "fetch":
                    if (args.length != 3) {
                        incorrectOps();
                    }
                    Repository.fetch(args[1], args[2]);
                    break;
                case "pull":
                    if (args.length != 3) {
                        incorrectOps();
                    }
                    Repository.pull(args[1], args[2]);
                    break;

                default:
                    System.out.println("No command with that name exists.");
                    System.exit(0);
            }
        } catch (GitletException | IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.exit(0);
        }
    }

    public static void incorrectOps() {
        throw error("Incorrect operands.");
    }
}
