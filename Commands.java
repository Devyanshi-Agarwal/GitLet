package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;


/** All the commands of gitlet, a mini version control system.
 *  @author Devyanshi Agarwal
 */

public class Commands implements Serializable {

    /** Initializes the current repository. */
    public void init() throws IOException {
        File gitlet = new File(".gitlet");
        gitlet.mkdir();
        File commits = new File(".gitlet/commits");
        commits.mkdir();
        String initialMsg = "initial commit";
        Date initialDate = new Date(0);
        String id = Utils.sha1(initialDate.toString(), initialMsg);
        HashMap<String, String> blob = new HashMap<>();
        blob.put("initial", "");
        Commit initial = new Commit(initialMsg, initialDate, null, id, blob);
        File initialCommit = new File(commits, id);
        Utils.writeObject(initialCommit, initial);
        File head = new File(".gitlet/HEAD");
        Utils.writeObject(head, initial);
        File staging = new File(".gitlet/staging");
        staging.mkdir();
        File stagingADD = new File(".gitlet/staging/add");
        stagingADD.mkdir();
        File stagingRM = new File(".gitlet/staging/remove");
        stagingRM.mkdir();
        File branches = new File(".gitlet/branches");
        branches.mkdir();
        File master = new File(".gitlet/branches/master");
        Utils.writeObject(master, initial);
        Branches repository = new Branches();
        repository.setCurrentBranch("master");
        repository.setHead(id);
        repository.getAllBranches().put("master", id);
        File repo = new File(".gitlet/repo");
        Utils.writeObject(repo, repository);
    }

    /** Adds a copy of the file as it currently exists to the staging
     * area with parameter String NAME. **/
    public void add(String name) throws IOException {
        File cwd = new File(System.getProperty("user.dir"));
        File newFile = new File(cwd, name);
        File removed = new File(".gitlet/staging/remove");
        File[] removedFiles = removed.listFiles();
        if (removedFiles != null) {
            if (removedFiles.length != 0) {
                for (File f : removedFiles) {
                    if (f.getName().equals(name)) {
                        f.delete();
                        return;
                    }
                }
            }
        }
        if (!newFile.exists()) {
            Utils.message("File does not exist");
            System.exit(0);
        }
        File stagingADD = new File(".gitlet/staging/add");
        stagingADD.mkdir();
        String path = ".gitlet/staging/add/";
        path += name;
        String contents = Utils.readContentsAsString(newFile);
        File head = new File(".gitlet/HEAD");
        Commit headCommit = Utils.readObject(head, Commit.class);
        String headContent = headCommit.getBlob().get(name);
        if (contents.equals(headContent)) {
            return;
        }
        File add = new File(path);
        Utils.writeContents(add, contents);
    }

    /** Commits the tracked files by taking in a String MESSAGE. */
    public void commit(String message) throws IOException {
        File stageAdd = new File(".gitlet/staging/add");
        File[] files = stageAdd.listFiles();
        File commits = new File(".gitlet/commits");
        String path = commits.getPath();
        if (files != null) {
            if (files.length == 0) {
                commitRm(message);
                return;
            }
            HashMap<String, String> blob = new HashMap<>();
            for (File name : files) {
                String naam = name.getName();
                String matter = Utils.readContentsAsString(name);
                blob.put(naam, matter);
            }
            File head = new File(".gitlet/HEAD");
            Date date = new Date();
            String id = Utils.sha1(date.toString(), message);
            File r = new File(".gitlet/repo");
            Branches repo = Utils.readObject(r, Branches.class);
            String currentBranch = repo.getCurrentBranch();
            String parent = repo.getAllBranches().get(currentBranch);
            repo.getAllBranches().replace(currentBranch, id);
            repo.setHead(id);
            Commit commit = new Commit(message, date, parent, id, blob);
            path += "/" + id;
            File newCommit = new File(path);
            Utils.writeObject(newCommit, commit);
            Utils.writeObject(head, commit);
            Utils.writeObject(r, repo);
            for (File tmp : files) {
                tmp.delete();
            }

        } else {
            Utils.message("No changes added to the commit");
        }
    }

    /** Helper function for removing. Takes in String MESSAGE. */
    public void commitRm(String message) throws IOException {
        File rm = new File(".gitlet/staging/remove");
        File[] allrm = rm.listFiles();
        ArrayList<String> names = new ArrayList<>();
        if (allrm != null) {
            if (allrm.length != 0) {
                for (File name : allrm) {
                    names.add(name.getName());
                }
                HashMap<String, String> blob = new HashMap<>();
                File head = new File(".gitlet/HEAD");
                Commit headCommit = Utils.readObject(head, Commit.class);
                Set<String> files = headCommit.getBlob().keySet();
                for (String keys : files) {
                    if (!names.contains(keys)) {
                        blob.put(keys, headCommit.getBlob().get(keys));
                    }
                }
                Date date = new Date();
                String id = Utils.sha1(date.toString(), message);
                File r = new File(".gitlet/repo");
                Branches repo = Utils.readObject(r, Branches.class);
                String currentBranch = repo.getCurrentBranch();
                String parent = repo.getAllBranches().get(currentBranch);
                repo.getAllBranches().replace(currentBranch, id);
                repo.setHead(id);
                Commit commit = new Commit(message, date, parent, id, blob);
                File newCommit = new File(".gitlet/commits/" + id);
                Utils.writeObject(newCommit, commit);
                Utils.writeObject(head, commit);
                Utils.writeObject(r, repo);
                for (File remove : allrm) {
                    remove.delete();
                }
            } else {
                Utils.message("No changes added to the commit.");
                System.exit(0);
            }
        } else {
            Utils.message("No changes added to the commit.");
            System.exit(0);
        }
    }

    /** Unstage STRING NAME if it is currently staged for addition. */
    public void rm(String name) {
        File stageAdd = new File(".gitlet/staging/add");
        File[] filesStaged = stageAdd.listFiles();
        File head = new File(".gitlet/HEAD");
        Commit headCommit = Utils.readObject(head, Commit.class);
        if (filesStaged != null && filesStaged.length != 0) {
            for (File tmp : filesStaged) {
                if (tmp.getName().equals(name)) {
                    File file = new File(".gitlet/staging/add/" + name);
                    file.delete();
                    File cwd = new File(System.getProperty("user.dir"));
                    File newFile = new File(cwd, name);
                }
            }
        } else if (headCommit.getBlob().containsKey(name)) {
            String content = headCommit.getBlob().get(name);
            File removalStage = new File(
                    ".gitlet/staging/remove/" + name);
            Utils.writeContents(removalStage, content);
            File cwd = new File(System.getProperty("user.dir"));
            File wkdFile = new File(cwd, name);
            if (wkdFile.exists()) {
                wkdFile.delete();
            }
        } else {
            Utils.message("No reason to remove the file.");
        }
    }

    /** Displays the log of commits. */
    public void log() {
        String pattern = "EEE MMM dd HH:mm:ss yyyy Z";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        File head = new File(".gitlet/HEAD");
        Commit commit = Utils.readObject(head, Commit.class);
        while (true) {
            String parent = commit.getParent();
            System.out.println("===");
            System.out.println("commit " + commit.getId());
            String date = format.format(commit.getDate());
            System.out.println("Date: " + date);
            System.out.println(commit.getMsg());
            System.out.println("");
            File tmp = new File(".gitlet/commits/" + parent);
            if (commit.getParent() == null) {
                break;
            }
            commit = Utils.readObject(tmp, Commit.class);
        }
    }

    /** Displays all the commits ever made. */
    public void globalLog() {
        String pattern = "EEE MMM dd HH:mm:ss yyyy Z";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        File commits = new File(".gitlet/commits");
        File[] allCommits = commits.listFiles();
        if (allCommits != null) {
            for (File file : allCommits) {
                Commit commit = Utils.readObject(file, Commit.class);
                System.out.println("===");
                System.out.println("commit " + commit.getId());
                String date = format.format(commit.getDate());
                System.out.println("Date: " + date);
                System.out.println(commit.getMsg());
                System.out.println("");
            }
        }
    }

    /** Finds the commit that contains String MSG. */
    public void find(String msg) {
        File commits = new File(".gitlet/commits");
        File[] allCommits = commits.listFiles();
        boolean printed = false;
        if (allCommits != null) {
            for (File file : allCommits) {
                Commit commit = Utils.readObject(file, Commit.class);
                if (msg.equals(commit.getMsg())) {
                    printed = true;
                    System.out.println(commit.getId());
                }
            }
        }
        if (!printed) {
            Utils.message("Found no commit with that message");
        }
    }

    /** Shows the current status of the repository. */
    public void status() {
        File r = new File(".gitlet/repo");
        Branches repo = Utils.readObject(r, Branches.class);
        System.out.println("=== Branches ===");
        HashMap<String, String> branches = repo.getAllBranches();
        Set<String> keys = branches.keySet();
        for (String key : keys) {
            if (key.equals(repo.getCurrentBranch())) {
                System.out.println("*" + key);
            } else {
                System.out.println(key);
            }
        }
        System.out.println();
        File addFolder = new File(".gitlet/staging/add");
        File[] allAdd = addFolder.listFiles();
        ArrayList<String> addNames = new ArrayList<>();
        System.out.println("=== Staged Files ===");
        if (allAdd != null) {
            for (File add : allAdd) {
                addNames.add(add.getName());
                System.out.println(add.getName());
            }
        }
        System.out.println();
        File remFolder = new File(".gitlet/staging/remove");
        File[] remAll = remFolder.listFiles();
        ArrayList<String> remNames = new ArrayList<>();
        System.out.println("=== Removed Files ===");
        if (remAll != null) {
            for (File rem : remAll) {
                remNames.add(rem.getName());
                System.out.println(rem.getName());
            }
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    /** Returns short id of STring ID. */
    public String shortID(String id) {
        if (!(id.length() == Utils.UID_LENGTH)) {
            File commits = new File(".gitlet/commits");
            File[] listcom = commits.listFiles();
            assert listcom != null;
            for (File tmp : listcom) {
                if (tmp.getName().contains(id)) {
                    return tmp.getName();
                }
            }
            Utils.message("No commit with that id exists.");
            System.exit(0);
        }
        return id;
    }

    /** Checks out the commit with STRING[] MSG. */
    public void checkout(String[] msg) {
        String id = null;
        String filename = null;
        Commit commit = null;
        if (msg.length == 2) {
            if (!msg[0].equals("--")) {
                Utils.message("Incorrect operands.");
                System.exit(0);
            }
            filename = msg[1];
            File head = new File(".gitlet/HEAD");
            commit = Utils.readObject(head, Commit.class);
        } else if (msg.length == 3) {
            if (!msg[1].equals("--")) {
                Utils.message("Incorrect operands.");
                System.exit(0);
            }
            id = shortID(msg[0]);
            filename = msg[2];
            File commits = new File(".gitlet/commits");
            File[] filesC = commits.listFiles();
            if (filesC != null) {
                for (File file : filesC) {
                    if (id.equals(file.getName())) {
                        commit = Utils.readObject(file, Commit.class);
                    }
                }
            }

        }
        if (commit == null || filename == null) {
            Utils.message("No commit with that id exists. ");
        } else {
            if (commit.getBlob().containsKey(filename)) {
                File cwd = new File(System.getProperty("user.dir"));
                File newFile = new File(cwd, filename);
                String content = commit.getBlob().get(filename);
                Utils.writeContents(newFile, content);
            } else {
                Utils.message("File does not exist in that commit");
            }
        }
    }

    /** Checks out the String NAME. */
    public void checkoutBranch(String name) {
        File r = new File(".gitlet/repo");
        Branches repo = Utils.readObject(r, Branches.class);
        String currentBranch = repo.getCurrentBranch();
        if (repo.getCurrentBranch().equals(name)) {
            Utils.message("No need to checkout the current branch.");
        } else if (!repo.getAllBranches().containsKey(name)) {
            Utils.message("No such branch exists");
        } else {
            repo.setCurrentBranch(name);
            Utils.writeObject(r, repo);
            String currID = repo.getAllBranches().get(name);
            File commit = new File(".gitlet/commits/" + currID);
            Commit curr = Utils.readObject(commit, Commit.class);
            Set<String> keys = curr.getBlob().keySet();
            File head = new File(".gitlet/HEAD");
            Commit headCommit = Utils.readObject(head, Commit.class);
            for (String key : keys) {
                File cwd = new File(System.getProperty("user.dir"));
                File newFile = new File(cwd, key);
                String contents = curr.getBlob().get(key);
                Utils.writeContents(newFile, contents);
            }
            String tmpID = repo.getAllBranches().get(currentBranch);
            File tmpCommit = new File(".gitlet/commits/" + tmpID);
            Commit tmp = Utils.readObject(tmpCommit, Commit.class);
            Set<String> tmpkeys = tmp.getBlob().keySet();
            for (String tmpk : tmpkeys) {
                if (!curr.getBlob().containsKey(tmpk)) {
                    File cwd = new File(System.getProperty("user.dir"));
                    File tmpnew = new File(cwd, tmpk);
                    tmpnew.delete();
                }
            }
            File stagingAdd = new File(".gitlet/staging/add");
            File[] added = stagingAdd.listFiles();
            if (added != null) {
                for (File fileadd : added) {
                    fileadd.delete();
                }
            }
            File removeal = new File(".gitlet/staging/remove");
            File[] rem = removeal.listFiles();
            if (rem != null) {
                for (File remove : rem) {
                    remove.delete();
                }
            }
            // added functionality
            setHead();
        }
    }

    public void setHead() {
        File r = new File(".gitlet/repo");
        Branches repo = Utils.readObject(r, Branches.class);
        String current = repo.getCurrentBranch();
        String c = repo.getAllBranches().get(current);
        if (!c.equals(repo.getHEAD())) {
            repo.setHead(c);
            File head = new File(".gitlet/HEAD");
            File commit = new File(".gitlet/commits/" + c);
            Commit com = Utils.readObject(commit, Commit.class);
            Utils.writeObject(head, com);
        }
    }

    /** Sets a new branch with String NAME. */
    public  void branch(String name) {
        File r = new File(".gitlet/repo");
        Branches repo = Utils.readObject(r, Branches.class);
        String headCommit = repo.getHEAD();
        if (repo.getAllBranches().containsKey(name)) {
            Utils.message("A branch with that name already exists.");
        } else {
            repo.getAllBranches().put(name, headCommit);
            File newfile = new File(".gitlet/branches/" + name);
            File master = new File(".gitlet/branches/master");
            Commit masterCommit = Utils.readObject(master, Commit.class);
            Utils.writeObject(newfile, masterCommit);
            Utils.writeObject(r, repo);
        }
    }

    /** Removes the branch with String NAME. */
    public void rmBranch(String name) {
        File r = new File(".gitlet/repo");
        Branches repo = Utils.readObject(r, Branches.class);
        HashMap<String, String> branches = repo.getAllBranches();
        Set<String> keys = branches.keySet();
        if (name.equals(repo.getCurrentBranch())) {
            Utils.message("Cannot remove the current branch.");
            System.exit(0);
        }
        for (String key : keys) {
            if (key.equals(name)) {
                repo.getAllBranches().remove(name);
                Utils.writeObject(r, repo);
                System.exit(0);
            }
        }
        Utils.writeObject(r, repo);
        Utils.message("A branch with that name does not exist.");
    }

    /** Resets HEAD to commit with String ID. */
    public void reset(String id) {
        File c = new File(".gitlet/commits/" + id);
        if (!c.exists()) {
            Utils.message("No commit with that id exists.");
        }
        File head = new File(".gitlet/HEAD");
        File stagingAdd = new File(".gitlet/staging/add");
        File stagingRem = new File(".gitlet/staging/remove");
        Commit headCommit = Utils.readObject(head, Commit.class);
        Set<String> tracked = headCommit.getBlob().keySet();
        ArrayList<String> untracked = new ArrayList<>();
        Commit commit = Utils.readObject(c, Commit.class);
        Set<String> toTrack = commit.getBlob().keySet();
        for (String key : toTrack) {
            String uid = commit.getId();
            String[] tmp = new String[3];
            tmp[0] = uid;
            tmp[1] = "--";
            tmp[2] = key;
            checkout(tmp);
        }
        for (String k : tracked) {
            if (!toTrack.contains(k)) {
                rm(k);
            }
        }
        File[] filesAdd = stagingAdd.listFiles();
        File[] filesRem = stagingRem.listFiles();
        if (filesAdd != null) {
            for (File a : filesAdd) {
                a.delete();
            }
        }
        if (filesRem != null) {
            for (File r : filesRem) {
                r.delete();
            }
        }
        Utils.writeObject(head, commit);
    }
}
