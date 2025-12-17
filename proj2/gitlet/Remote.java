package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Remote {
    public Remote(String remoteName, String remotePath) {
        File newRemote = join(REMOTE_DIR, remoteName);
        if (!REMOTE_DIR.exists()) {
            REMOTE_DIR.mkdirs();
        }
        if (newRemote.exists()) {
            throw error("A remote with that name already exists.");
        }
        try {
            newRemote.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        remotePath = remotePath.replace("/", File.separator);
        writeContents(newRemote, remotePath);
    }

    public static void remove(String remoteName) {
        File rmRemote = join(REMOTE_DIR, remoteName);
        if (!rmRemote.exists()) {
            throw error("A remote with that name does not exist.");
        }
        rmRemote.delete();
    }

    public static void push(String remoteName, String branchName) {
        File remotePath = remotePath(remoteName);
        if (remotePath == null) {
            return;
        }
        File remoteBranchFile = join(remotePath, "refs", "heads", branchName);
        if (!remoteBranchFile.exists()) {
            try {
                remoteBranchFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeContents(remoteBranchFile, "");
        }
        String remoteHash = readContentsAsString(remoteBranchFile);
        String currHash = Branch.currHash();
        Set<String> historyBuffer = commitCollector(currHash, remoteHash);
        if (historyBuffer == null) {
            throw error("Please pull down remote changes before pushing.");
        }
        for (String hash : historyBuffer) {
            HashMap<String, String> blobs = Commit.getBlob(hash);
            for (String blobName : blobs.values()) {
                File blob = join(remotePath, "objects", blobName);
                if (!blob.exists()) {
                    File localBlob = join(OB_DIR, blobName);
                    byte[] blobContents = readContents(localBlob);
                    try {
                        blob.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    writeContents(blob, blobContents);
                }
            }
            File copyCommit = join(remotePath, "objects", "commits", hash);
            try {
                copyCommit.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File commit = join(CM_DIR, hash);
            String content = readContentsAsString(commit);
            writeContents(copyCommit, content);
        }
        writeContents(remoteBranchFile, currHash);
    }

    private static Set<String> commitCollector(String currHash, String remoteHash) {
        Set<String> historyBuffer = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        historyBuffer.add(currHash);
        queue.add(currHash);
        while (!queue.isEmpty()) {
            String hash = queue.remove();
            if (hash.equals(remoteHash)) {
                return historyBuffer;
            }
            Commit commit = Commit.getCommit(hash);
            HashSet<String> parents = commit.getParentHash();
            for (String pHash : parents) {
                if (!historyBuffer.contains(pHash)) {
                    historyBuffer.add(pHash);
                    queue.add(pHash);
                }
            }
        }
        if (remoteHash.isEmpty()) {
            return historyBuffer;
        }
        return null;
    }

    public static void fetch(String remoteName, String branchName) {
        File remotePath = remotePath(remoteName);
        if (remotePath == null) {
            return;
        }
        File remoteBranchFile = join(remotePath, "refs", "heads", branchName);
        if (!remoteBranchFile.exists()) {
            throw error("That remote does not have that branch.");
        }
        String remoteHash = readContentsAsString(remoteBranchFile);
        Queue<String> queue = new LinkedList<>();
        HashSet<String> prev = new HashSet<>();
        queue.add(remoteHash);
        prev.add(remoteHash);
        while (!queue.isEmpty()) {
            String hash = queue.remove();
            File local = join(CM_DIR, hash);
            if (local.exists()) {
                continue;
            }
            try {
                local.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File remote = join(remotePath, "objects", "commits", hash);
            Commit commit = readObject(remote, Commit.class);
            HashMap<String, String> remoteBlobs = commit.getBlob();
            for (String blobNames : remoteBlobs.values()) {
                File blob = join(OB_DIR, blobNames);
                if (!blob.exists()) {
                    File remoteBlob = join(remotePath, "objects", blobNames);
                    byte[] blobContent = readContents(remoteBlob);
                    try {
                        blob.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    writeContents(blob, blobContent);
                }
            }
            byte[] commitContent = readContents(remote);
            writeContents(local, commitContent);
            HashSet<String> parents = commit.getParentHash();
            for (String pHash : parents) {
                if (!prev.contains(pHash)) {
                    prev.add(pHash);
                    queue.add(pHash);
                }
            }
        }
        String localBranchName = remoteName + "/" + branchName;
        File remoteBranchDir = join(HEAD_DIR, remoteName);
        if (!remoteBranchDir.exists()) {
            remoteBranchDir.mkdirs();
        }
        File branchFile = join(remoteBranchDir, branchName);
        if (!branchFile.exists()) {
            try {
                branchFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Branch.update(localBranchName, remoteHash);
    }

    private static File remotePath(String remoteName) {
        File remoteConfig = join(REMOTE_DIR, remoteName);
        if (!remoteConfig.exists()) {
            return null;
        }
        String remotePathString = readContentsAsString(remoteConfig);
        File remotePath = new File(remotePathString);
        if (!remotePath.exists()) {
            throw error("Remote directory not found.");
        }
        return remotePath;
    }
}
