package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/** Branches of the current repository.
 *  @author Devyanshi Agarwal
 */

public class Branches implements Serializable {

    /** The String HEAD. */
    private String _HEAD;

    /** The String current branch. */
    private String _currentBranch;

    /** The HashMap of all branches. */
    private HashMap<String, String> _allBranches;

    /** The String last command. */
    private String _lastCommand;

    /** The String this command. */
    private String _thisCommand;

    /** The String file name. */
    private String fileName;

    /** Constructor of Branches creates new HashMap. */
    public Branches() {
        _allBranches = new HashMap<String, String>();
    }

    /** Takes HashMap of all branches and returns instance variable. */
    public HashMap<String, String> getAllBranches() {
        return _allBranches;
    }

    /** Takes string of current branch and returns instance variable. */
    public String getCurrentBranch() {
        return _currentBranch;
    }

    /** Sets String currentBranch to String BRANCH. */
    public void setCurrentBranch(String branch) {
        _currentBranch = branch;
    }

    /** Gets the String master branch and returns String master. */
    public String getMaster() {
        return _allBranches.get("master");
    }

    /** Returns the String HEAD. */
    public String getHEAD() {
        return _HEAD;
    }

    /** Sets the head to current String ID. */
    public void setHead(String id) {
        _HEAD = id;
    }
}
