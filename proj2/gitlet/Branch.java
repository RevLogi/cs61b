package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Branch {
    /** Branch initialization */
    public static void initBranch() {
        HEAD_DIR.mkdirs();
        try {
            HEAD.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Create a new branch with a branchName. */
    public static void branch(String branchName) {
        File branchFile = join(HEAD_DIR, branchName);
        if (branchFile.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        try {
            branchFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Update the branch that HEAD points at with new commit hash. */
    public static void updateHEAD(String hash) {
        String currBranch = readContentsAsString(HEAD);
        update(currBranch, hash);
    }

    public static void update(String branch, String hash) {
        File branchFile = join(HEAD_DIR, branch);
        writeContents(branchFile, hash);
    }

    public static void remove(String branch) {
        File branchFile = join(HEAD_DIR, branch);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String head = getHead();
        if (head.equals(branch)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        branchFile.delete();
    }

    /** Return the hash of current commit that HEAD points at. */
    public static String currHash() {
        String currBranch = readContentsAsString(HEAD);
        File branchFile = join(HEAD_DIR, currBranch);
        return readContentsAsString(branchFile);
    }

    /** Update the branch that HEAD points at. */
    public static void head(String curBranch) {
        writeContents(HEAD, curBranch);
    }

    public static String getHead() {
        return readContentsAsString(HEAD);
    }

    /** Find the split point using the hash of the commit that given branch points at. */
    public static String splitPoint(String givenHash, String currHash) {
        HashSet<String> currAncestors = new HashSet<>();
        currAncestors = Commit.getAncestors(currAncestors, currHash);
        Queue<String> queue = new LinkedList<>();
        queue.add(givenHash);
        while (!queue.isEmpty()) {
            String hash = queue.remove();
            if (currAncestors.contains(hash)) {
                return hash;
            }
            Commit commit = Commit.getCommit(hash);
            HashSet<String> parentHash = commit.getParentHash();
            queue.addAll(parentHash);
        }
        return null;
    }
}
