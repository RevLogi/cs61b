package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class StagingArea implements Serializable {
    private HashMap<String, String> addedFile;
    private HashSet<String> removedFile;

    /**
     * Loading the prev SA.
     */
    public StagingArea() {
        File index = join(SA_DIR, "index");
        if (index.exists()) {
            try {
                StagingArea prev = readObject(index, StagingArea.class);
                this.addedFile = prev.addedFile;
                if (prev.removedFile == null) {
                    this.removedFile = new HashSet<>();
                } else {
                    this.removedFile = prev.removedFile;
                }
            } catch (IllegalArgumentException e) {
                // If readObject fails (corruption), start fresh to avoid crash
                this.addedFile = new HashMap<>();
                this.removedFile = new HashSet<>();
            }
        } else {
            this.addedFile = new HashMap<>();
            this.removedFile = new HashSet<>();
        }
    }

    /**
     * Initialize the StagingArea (create directory and blank SA).
     */
    public static void initSA() {
        SA_DIR.mkdirs();
        clear();
    }

    public static void clear() {
        StagingArea initialArea = new StagingArea();
        initialArea.addedFile = new HashMap<>();
        initialArea.removedFile = new HashSet<>();
        initialArea.save();
    }

    /**
     * Add new file into SA.
     */
    public void add(String fileName, String blobHashName, byte[] contents) {
        HashMap<String, String> blobs = Commit.currBlobs();
        // No longer be staged for removal
        if (removedFile.contains(fileName)) {
            removedFile.remove(fileName);
        }
        // Check if the adding file is same as commit version
        if (!blobs.containsKey(fileName) || !blobs.get(fileName).equals(blobHashName)) {
            addedFile.put(fileName, blobHashName);
            File newFile = join(OB_DIR, blobHashName);
            try {
                if (!newFile.exists()) {
                    newFile.createNewFile();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeContents(newFile, contents);
        } else {
            // Handle the case: a file is changed back to original version
            addedFile.remove(fileName);
        }
    }

    /**
     * Remove file in SA or add a file to remove file in commit.
     */
    public void remove(String fileName) {
        HashMap<String, String> blobs = Commit.currBlobs();
        File rmFile = join(CWD, fileName);
        if (blobs.containsKey(fileName)) {
            removedFile.add(fileName);
            Utils.restrictedDelete(rmFile);
        } else if (addedFile.containsKey(fileName)) {
            addedFile.remove(fileName);
        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    /**
     * Save the currSA for next loading.
     */
    public void save() {
        File index = join(SA_DIR, "index");
        writeObject(index, this);
    }

    public HashMap<String, String> getAddedFile() {
        return addedFile;
    }

    public HashSet<String> getRemovedFile() {
        return removedFile;
    }
}
