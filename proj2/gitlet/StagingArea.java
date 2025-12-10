package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import static gitlet.Repository.OB_DIR;
import static gitlet.Repository.SA_DIR;
import static gitlet.Utils.*;

public class StagingArea implements Serializable {
    private HashMap<String, String> addedFile;

    /**
     * Loading the prev SA.
     */
    public StagingArea() {
        File index = join(SA_DIR, "index");
        if (index.exists()) {
            StagingArea prev = readObject(index, StagingArea.class);
            addedFile = prev.addedFile;
        } else {
            this.addedFile = new HashMap<>();
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
        initialArea.save();
    }

    /**
     * Add new file into SA.
     */
    public void add(String fileName, String blobHashName, String contents) {
        HashMap blobs = Commit.currBlobs();
        if (!blobs.containsKey(fileName) || !blobs.get(fileName).equals(blobHashName)) {
            addedFile.put(fileName, blobHashName);
            File newFile = join(OB_DIR, blobHashName);
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeContents(newFile, contents);
        }
    }

    /**
     * Remove file in SA or add a file to remove file in commit.
     */
    public void remove(String fileName, String blobHashName, File rmFile) {
        HashMap blobs = Commit.currBlobs();
        if (blobs.containsKey(fileName)) {
            addedFile.put(fileName, blobHashName);
            rmFile.delete();
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
}