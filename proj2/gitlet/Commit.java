package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.util.Date; // TODO: You'll likely use this in this class
import java.io.Serializable;
import java.util.Formatter;
import java.util.HashMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

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
    private String message;
    private Date timeStamp;
    private String parentHash;
    private HashMap<String, String> blobs;

    public Commit() {
        this.timeStamp = new Date(0);
        this.message = "initial commit";
        this.parentHash = null;
        this.blobs = new HashMap<>();
        CM_DIR.mkdirs();
        // Create the master branch
        Branch.initBranch();
        Branch.branch("master");
        Branch.head("master");
        // Initial the StagingArea
        StagingArea.initSA();
    }

    public Commit(String message) {
        this.timeStamp = new Date();
        this.message = message;
        this.parentHash = Branch.currHash();
        // Get the blobs of prev commit
        this.blobs = Commit.currCommit().blobs;
        // Add the blobs in staging area into current commit
        File index = join(SA_DIR, "index");
        StagingArea currArea = readObject(index, StagingArea.class);
        HashMap<String, String> addedFiles = currArea.getAddedFile();
        removedFile(addedFiles);
        if (addedFiles.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        for (String fileName : addedFiles.keySet()) {
            File file = join(CWD, fileName);
            if (file.exists()) {
                String blobHashName = addedFiles.get(fileName);
                this.blobs.put(fileName, blobHashName);
            }
        }
    }

    public void removedFile(HashMap<String, String> addedFiles) {
        for (String fileName : addedFiles.keySet()) {
            File file = join(CWD, fileName);
            if (!file.exists()) {
                this.blobs.remove(fileName);
            }
        }
    }

    /** Create a commit object inside a file named by its hash code. */
    public void create() {
        // Generate the hash code for current commit
        byte[] hashable = serialize(this);
        String hash = sha1(hashable);
        // Create the commit object
        File newCommit = join(CM_DIR, hash);
        try {
            newCommit.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(newCommit, this);
        // Refresh the current branch
        Branch.updateHEAD(hash);
    }

    public static void log(String hash) {
        logPrint(hash);
        Commit currCommit = getCommit(hash);
        if (currCommit.parentHash == null) {
            return;
        } else {
            log(currCommit.parentHash);
        }
    }

    /** Print a log content of a commit by its hash code. */
    public static void logPrint(String hash) {
        Commit currCommit = getCommit(hash);
        String commitMessage = currCommit.message;
        String date = String.format(java.util.Locale.US, "Date: %1$ta %1$tb %1$te %1$tT %1$tY %1$tz", currCommit.timeStamp);
        System.out.println("===");
        System.out.print("commit ");
        System.out.println(hash);
        System.out.println(date);
        System.out.println(commitMessage);
        System.out.println();
    }

    /** Find the specific commit according to its message. */
    public static void find(String message) {
        boolean i = false;
        for (String hash : plainFilenamesIn(CM_DIR)) {
            Commit currCommit = getCommit(hash);
            String currMessage = currCommit.message;
            if (currMessage.equals(message)) {
                System.out.println(hash);
                i = true;
            }
        }
        if (!i) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    /** Get the current commit that HEAD points at. */
    public static Commit currCommit() {
        String currHash = Branch.currHash();
        return getCommit(currHash);
    }

    /** Get a commit by its hash code. */
    public static Commit getCommit(String hash) {
        File currCommitFile = join(CM_DIR, hash);
        if (!currCommitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        return readObject(currCommitFile, Commit.class);
    }

    /** Get the blobs of current commit. */
    public static HashMap<String, String> currBlobs() {
        Commit currCommit = currCommit();
        return currCommit.blobs;
    }

    /** Get the blobs by the hash code of its commit. */
    public static HashMap<String, String> getBlob(String hash) {
        Commit currCommit = getCommit(hash);
        return currCommit.blobs;
    }
}
