package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
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
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The objects(blobs and commits) directory. */
    public static final File OB_DIR = join(GITLET_DIR, "objects");
    public static final File CM_DIR = join(OB_DIR, "commits");
    /** The staging area directory. */
    public static final File SA_DIR = join(GITLET_DIR, "staging_area");
    /** The branch directory. */
    public static final File HEAD_DIR = join(GITLET_DIR, "refs", "heads");
    public static final File HEAD = join(GITLET_DIR, "HEAD");

    public static void init() {
        // Check if it is already initialized
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        // Initialize the .gitlet
        GITLET_DIR.mkdirs();
        // Create an initial commit
        OB_DIR.mkdir();
        Commit initCommit = new Commit();
        initCommit.create();
    }

    /** Read the contents of the file. */
    public static void add(String fileName) {
        File newFile = join(CWD, fileName);
        if (newFile.exists()) {
            String contents = readContentsAsString(newFile);
            String blobHashName = sha1(contents);
            StagingArea stage = new StagingArea();
            stage.add(fileName, blobHashName, contents);
            stage.save();
        } else {
            noFile();
        }
    }

    /** Create a commit. */
    public static void commit(String message) {
        Commit newCommit = new Commit(message);
        newCommit.create();
        StagingArea.clear();
    }

    public static void remove(String fileName) {
        File rmFile = join(CWD, fileName);
        if (rmFile.exists()) {
            String contents = readContentsAsString(rmFile);
            String blobHashName = sha1(contents);
            StagingArea stage = new StagingArea();
            stage.remove(fileName, blobHashName, rmFile);
            stage.save();
        } else {
            noFile();
        }
    }

    public static void log() {
        String currHash = Branch.currHash();
        Commit.log(currHash);
    }

    public static void global() {
        for (String hash : plainFilenamesIn(CM_DIR)) {
            Commit.logPrint(hash);
        }
    }

    public static void noFile() {
        System.out.println("File does not exist.");
        System.exit(0);
    }

    public static void find(String message) {
        Commit.find(message);
    }

    public static void status() {
        System.out.println("=== Branches ===");
        String head = Branch.getHead();
        List<String> branchList = plainFilenamesIn(HEAD_DIR);
        if (branchList != null) {
            Collections.sort(branchList);
            for (String branchName : branchList) {
                if (branchName.equals(head)) {
                    System.out.print("*");
                }
                System.out.println(branchName);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        File index = join(SA_DIR, "index");
        StagingArea currArea = readObject(index, StagingArea.class);
        HashMap<String, String> addedFile = currArea.getAddedFile();
        Set<String> files = addedFile.keySet();
        List<String> fileList = new ArrayList<>(files);
        Collections.sort(fileList);
        List<String> removedFileList = new ArrayList<>();
        for (String fileName : fileList) {
            File file = join(CWD, fileName);
            if (file.exists()) {
                System.out.println(fileName);
            } else {
                removedFileList.add(fileName);
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String rmFileName : removedFileList) {
            System.out.println(rmFileName);
        }
        System.out.println();
    }

    /** Replace the CWD file with its head commit version. */
    public static void checkFile(String fileName) {
        HashMap<String, String> currBlob = Commit.currBlobs();
        replaceFile(currBlob, fileName);
    }

    public static void checkFile(String commitHash, String fileName) {
        HashMap<String, String> currBlob = Commit.getBlob(commitHash);
        replaceFile(currBlob, fileName);
    }

    public static void replaceFile (HashMap<String, String> currBlob, String fileName) {
        String fileHash = currBlob.get(fileName);
        File file = join(OB_DIR, fileHash);
        if (!file.exists()) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String contents = readContentsAsString(file);
        File replacedFile = join(CWD, fileName);
        if (!replacedFile.exists()) {
            try {
                replacedFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writeContents(replacedFile, contents);
    }

    public static void checkCommit(String branchName) {
        File branchFile = join(HEAD_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        String head = Branch.getHead();
        if (branchName.equals(head)) {
            System.out.println("No need to checkout the current branch.");
        }
        String commitHash = readContentsAsString(branchFile);
        HashMap<String, String> currBlobs = Commit.currBlobs();
        HashMap<String, String> blobs = Commit.getBlob(commitHash);
        for (String fileName : blobs.keySet()) {
            File dirFile = join(CWD, fileName);
            if (dirFile.exists()) {
                String dirContents = readContentsAsString(dirFile);
                String currFileHash = currBlobs.get(fileName);
                File currFile = join(OB_DIR, currFileHash);
                String currContents = readContentsAsString(currFile);
                if (!currContents.equals(dirContents)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
            replaceFile (blobs, fileName);
        }
        Branch.head(branchName);
        StagingArea.clear();
    }

    public static void branch(String branchName) {
        Branch.branch(branchName);
        String hash = Branch.currHash();
        Branch.update(branchName, hash);
    }
}
