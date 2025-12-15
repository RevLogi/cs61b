package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.io.Serializable;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author RevLogi
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timeStamp;
    private String parentHash;
    private String anotherParenHash;
    private HashMap<String, String> blobs;

    public Commit() {
        this.timeStamp = new Date(0);
        this.message = "initial commit";
        this.parentHash = null;
        this.anotherParenHash = null;
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
        this.anotherParenHash = null;
        // Get the blobs of prev commit
        this.blobs = new HashMap<>(Commit.currCommit().blobs);
        // Add the blobs in staging area into current commit
        File index = join(SA_DIR, "index");
        StagingArea currArea = readObject(index, StagingArea.class);
        HashMap<String, String> addedFiles = currArea.getAddedFile();
        HashSet<String> removedFiles = currArea.getRemovedFile();
        // Check if empty commit
        if (addedFiles.isEmpty() && removedFiles.isEmpty()) {
            throw error("No changes added to the commit.");
        }
        // Handle addition
        for (String fileName : addedFiles.keySet()) {
            File file = join(CWD, fileName);
            if (file.exists()) {
                String blobHashName = addedFiles.get(fileName);
                this.blobs.put(fileName, blobHashName);
            }
        }
        // Handle deletion
        for (String fileName : removedFiles) {
            this.blobs.remove(fileName);
        }
    }

    public void addNewParent(String parentHash2) {
        this.anotherParenHash = parentHash2;
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
        String dateFormat = "Date: %1$ta %1$tb %1$te %1$tT %1$tY %1$tz";
        String date = String.format(java.util.Locale.US, dateFormat, currCommit.timeStamp);
        System.out.println("===");
        System.out.print("commit ");
        System.out.println(hash);
        System.out.println(date);
        System.out.println(commitMessage);
        System.out.println();
    }

    /** Use BFS to get all the ancestors of a commit by its hash and add them into a HashSet. */
    public static HashSet<String> getAncestors(HashSet<String> currAncestors, String currHash) {
        currAncestors.add(currHash);
        Queue<String> queue = new LinkedList<>();
        queue.add(currHash);
        while (!queue.isEmpty()) {
            String hash = queue.remove();
            Commit commit = Commit.getCommit(hash);
            HashSet<String> parents = commit.getParentHash();
            for (String pHash : parents) {
                if (!currAncestors.contains(pHash)) {
                    currAncestors.add(pHash);
                    queue.addAll(parents);
                }
            }
        }
        return currAncestors;
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
            throw error("Found no commit with that message.");
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
            throw error("No commit with that id exists.");
        }
        return readObject(currCommitFile, Commit.class);
    }

    public static String findFullHash(String hash) {
        List<String> allCommit = plainFilenamesIn(CM_DIR);
        for (String realHash : allCommit) {
            if (realHash.startsWith(hash)) {
                return realHash;
            }
        }
        return null;
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

    public HashSet<String> getParentHash() {
        HashSet<String> parentHash = new HashSet<>();
        if (this.parentHash != null) {
            parentHash.add(this.parentHash);
        }
        if (this.anotherParenHash != null) {
            parentHash.add(this.anotherParenHash);
        }
        return parentHash;
    }

    public static String readCommitContent(String fileName, String hash) {
        HashMap<String, String> blobs = getBlob(hash);
        String hashName = blobs.get(fileName);
        File targetFile = join(OB_DIR, hashName);
        return readContentsAsString(targetFile);
    }
}
