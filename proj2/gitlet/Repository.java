package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
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
    /** The remote directory. */
    public static final File REMOTE_DIR = join(GITLET_DIR, "remote");


    public static void init() {
        // Check if it is already initialized
        if (GITLET_DIR.exists()) {
            String msg = "A Gitlet version-control system "
                    + "already exists in the current directory.";
            throw error(msg);
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
            byte[] contents = readContents(newFile);
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

    public static void mergeCommit(String message, String parentHash2) {
        Commit newCommit = new Commit(message);
        newCommit.addNewParent(parentHash2);
        newCommit.create();
        StagingArea.clear();
    }

    public static void remove(String fileName) {
        StagingArea stage = new StagingArea();
        stage.remove(fileName);
        stage.save();
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
        throw error("File does not exist.");
    }

    public static void find(String message) {
        Commit.find(message);
    }

    public static void status() {

        System.out.println("=== Branches ===");
        printBranches();

        // Print the staged files
        System.out.println("=== Staged Files ===");
        printStagedFiles();

        // Print the removedFile
        System.out.println("=== Removed Files ===");
        printRemovedFiles();

        System.out.println("=== Modifications Not Staged For Commit ===");
        printNotStaged();

        System.out.println("=== Untracked Files ===");
        printUntracked();
    }

    private static HashMap<String, String> getAddedFiles() {
        // Get the added and removed files and sort them by their filenames
        File index = join(SA_DIR, "index");
        StagingArea currArea = readObject(index, StagingArea.class);
        return currArea.getAddedFile();
    }

    private static List<String> getFiles() {
        HashMap<String, String> addedFile = getAddedFiles();
        Set<String> files =  addedFile.keySet();
        List<String> fileList = new ArrayList<>(files);
        Collections.sort(fileList);
        return fileList;
    }

    private static List<String> getRemovedFile() {
        File index = join(SA_DIR, "index");
        StagingArea currArea = readObject(index, StagingArea.class);
        Set<String> rmFiles = currArea.getRemovedFile();
        List<String> rmFileList = new ArrayList<>(rmFiles);
        Collections.sort(rmFileList);
        return rmFileList;
    }

    private static void printBranches() {
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
    }

    private static void printStagedFiles() {
        List<String> fileList = getFiles();
        for (String fileName : fileList) {
            File file = join(CWD, fileName);
            if (file.exists()) {
                System.out.println(fileName);
            }
        }
        System.out.println();
    }

    private static void printRemovedFiles() {
        List<String> rmFileList = getRemovedFile();
        for (String rmFileName : rmFileList) {
            System.out.println(rmFileName);
        }
        System.out.println();
    }

    private static void printNotStaged() {
        // Get all the filenames in current commit
        HashMap<String, String> currBlobs = Commit.currBlobs();
        Set<String> blobFiles = currBlobs.keySet();

        HashMap<String, String> addedFile = getAddedFiles();
        List<String> files = getFiles();
        for (String fileName : files) {
            File file = join(CWD, fileName);
            // Staged for addition, but deleted in the CWD
            if (!file.exists()) {
                System.out.println(fileName + " (deleted)");
            }
            // Staged for addition, but changed again in the CWD
            if (file.exists()) {
                String currContent = readContentsAsString(file);
                String currHash = sha1(currContent);
                if (!addedFile.get(fileName).equals(currHash)) {
                    System.out.println(fileName + " (modified)");
                }
            }
        }
        for (String fileName : blobFiles) {
            List<String> rmFiles = getRemovedFile();
            File file = join(CWD, fileName);
            // Tracked in the current commit and deleted from the CWD
            // without being staged for removal
            if (!file.exists() && !rmFiles.contains(fileName)) {
                System.out.println(fileName + " (deleted)");
            }
            // Tracked in the current commit, changed in the CWD, but not staged.
            if (file.exists()) {
                String currContent = readContentsAsString(file);
                String currHash = sha1(currContent);
                if (!files.contains(fileName) && !currBlobs.get(fileName).equals(currHash)) {
                    System.out.println(fileName + " (modified)");
                }
            }
        }
        System.out.println();
    }

    private static void printUntracked() {
        // Get all the filenames in current commit
        HashMap<String, String> currBlobs = Commit.currBlobs();
        Set<String> blobFiles = currBlobs.keySet();

        for (String fileName : plainFilenamesIn(CWD)) {
            List<String> files = getFiles();
            List<String> rmFiles = getRemovedFile();
            if (!files.contains(fileName)
                    && (!blobFiles.contains(fileName) || rmFiles.contains(fileName))) {
                System.out.println(fileName);
            }
        }
        System.out.println();
    }

    /** Replace the CWD file with its head commit version. */
    public static void checkFile(String fileName) {
        HashMap<String, String> currBlob = Commit.currBlobs();
        replaceFile(currBlob, fileName);
    }

    public static void checkFile(String commitHash, String fileName) {
        if (commitHash.length() < 40) {
            commitHash = Commit.findFullHash(commitHash);
            if (commitHash == null) {
                throw error("No commit with that id exists.");
            }
        }
        HashMap<String, String> currBlob = Commit.getBlob(commitHash);
        replaceFile(currBlob, fileName);
    }

    public static void replaceFile(HashMap<String, String> currBlob, String fileName) {
        String fileHash = currBlob.get(fileName);
        if (fileHash == null) {
            throw error("File does not exist in that commit.");
        }
        File file = join(OB_DIR, fileHash);
        byte[] contents = readContents(file);
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
            throw error("No such branch exists.");
        }
        String head = Branch.getHead();
        if (branchName.equals(head)) {
            throw error("No need to checkout the current branch.");
        }
        String commitHash = readContentsAsString(branchFile);
        checkAllFiles(commitHash);
        Branch.head(branchName);
    }

    public static void checkAllFiles(String commitHash) {
        HashMap<String, String> currBlobs = Commit.currBlobs();
        HashMap<String, String> blobs = Commit.getBlob(commitHash);
        // Check for untracked file
        for (String fileName : blobs.keySet()) {
            File dirFile = join(CWD, fileName);
            if (dirFile.exists()) {
                if (!currBlobs.containsKey(fileName)) {
                    String msg = "There is an untracked file in the way; "
                            + "delete it, or add and commit it first.";
                    throw error(msg);
                }
            }
        }
        // If there is no untracked file, then replace
        for (String fileName : blobs.keySet()) {
            replaceFile(blobs, fileName);
        }
        // Delete verbose files
        for (String fileName : currBlobs.keySet()) {
            if (!blobs.containsKey(fileName)) {
                File file = join(CWD, fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        StagingArea.clear();
    }

    public static void branch(String branchName) {
        Branch.branch(branchName);
        String hash = Branch.currHash();
        Branch.update(branchName, hash);
    }

    public static void rmBranch(String branchName) {
        Branch.remove(branchName);
    }

    public static void reset(String commitHash) {
        if (commitHash.length() < 40) {
            commitHash = Commit.findFullHash(commitHash);
            if (commitHash == null) {
                throw error("No commit with that id exists.");
            }
        }
        File cmFile = join(CM_DIR, commitHash);
        if (!cmFile.exists()) {
            throw error("No commit with that id exists.");
        }
        // Update files
        checkAllFiles(commitHash);
        // Update pointer
        String currBranch = Branch.getHead();
        Branch.update(currBranch, commitHash);
        StagingArea.clear();
    }

    public static void merge(String branchName) {
        // Check Staging Area
        StagingArea sa = new StagingArea();
        checkMergeFail(branchName, sa);
        File branchFile = join(HEAD_DIR, branchName);
        // Check if it is a real split
        String currBranchName = Branch.getHead();
        String currHash = Branch.currHash();
        String givenHash = readContentsAsString(branchFile);
        String splitHash = Branch.splitPoint(givenHash, currHash);
        if (currBranchName.equals(branchName)) {
            throw error("Cannot merge a branch with itself.");
        }
        if (splitHash.equals(givenHash)) {
            throw error("Given branch is an ancestor of the current branch.");
        }
        if (splitHash.equals(currHash)) {
            checkCommit(branchName);
            throw error("Current branch fast-forwarded.");
        }
        // Collect all the file names
        Set<String> allFile = new HashSet<>();
        HashMap<String, String> cBlobs = Commit.getBlob(currHash);
        HashMap<String, String> gBlobs = Commit.getBlob(givenHash);
        HashMap<String, String> sBlobs = Commit.getBlob(splitHash);
        allFile.addAll(cBlobs.keySet());
        allFile.addAll(gBlobs.keySet());
        allFile.addAll(sBlobs.keySet());

        boolean conflict = false;
        // Check for untracked file
        for (String fileName: allFile) {
            File mergeFile = join(CWD, fileName);
            if (mergeFile.exists() && !cBlobs.containsKey(fileName)) {
                String message = "There is an untracked file in the way; "
                        + "delete it, or add and commit it first.";
                throw error(message);
            }
        }
        // Iterate per file
        for (String fileName : allFile) {
            String cVal = cBlobs.get(fileName);
            String gVal = gBlobs.get(fileName);
            String sVal = sBlobs.get(fileName);

            if (isSame(sVal, cVal) && !isSame(sVal, gVal)) {
                if (gVal == null) {
                    remove(fileName);
                } else {
                    checkFile(givenHash, fileName);
                    add(fileName);
                }
                continue;
            }
            if (isSame(gVal, sVal) && !isSame(sVal, cVal)) {
                continue;
            }
            if (!isSame(gVal, cVal)) {
                conflict = true;
                String currContent = getContent(cVal, fileName, currHash);
                String givenContent = getContent(gVal, fileName, givenHash);
                String conflictContent = "<<<<<<< HEAD\n" + currContent
                                       + "=======\n" + givenContent
                                       + ">>>>>>>\n";
                File mergeFile = join(CWD, fileName);
                writeContents(mergeFile, conflictContent);
                add(fileName);
            }
        }
        String message = "Merged " + branchName + " into " + currBranchName + ".";
        mergeCommit(message, givenHash);

        mergeConflict(conflict);
    }

    private static void mergeConflict(boolean conflict) {
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private static String getContent(String val, String fileName, String hash) {
        if (val == null) {
            return "";
        } else {
            return Commit.readCommitContent(fileName, hash);
        }
    }

    private static void checkMergeFail(String branchName, StagingArea sa) {
        HashMap<String, String> addedFile = sa.getAddedFile();
        HashSet<String> removedFile = sa.getRemovedFile();
        if (!addedFile.isEmpty() || !removedFile.isEmpty()) {
            throw error("You have uncommitted changes.");
        }
        // Check if branch exists
        File branchFile = join(HEAD_DIR, branchName);
        if (!branchFile.exists()) {
            throw error("A branch with that name does not exist.");
        }
    }

    private static boolean isSame(String val1, String val2) {
        if (val1 == null && val2 == null) {
            return true;
        }
        if (val1 == null || val2 == null) {
            return false;
        }
        return val1.equals(val2);
    }

    public static void addRemote(String remoteName, String remotePath) {
        new Remote(remoteName, remotePath);
    }

    public static void rmRemote(String remoteName) {
        Remote.remove(remoteName);
    }

    public static void push(String remoteName, String branchName) {
        Remote.push(remoteName, branchName);
    }
}
