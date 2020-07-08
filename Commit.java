package gitlet;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/** Commits of the current repository.
 *  @author Devyanshi Agarwal
 */

public class Commit implements Serializable {

    /** Constructor of Commit takes in STRING MESSAGE, DATE DATE, STRING PARENT,
     * STRING ID, HASHMAP BLOB.
     */
    public Commit(String message, Date date, String parent,
                  String id, HashMap<String, String> blob) {
        _message = message;
        _date = date;
        _parent = parent;
        _id = id;
        _blobs = blob;
    }

    /** RETURNS HASHMAP BLOB. */
    public HashMap<String, String> getBlob() {
        return _blobs;
    }

    /** RETURNS STRING MESSAGE. */
    public String getMsg() {
        return _message;
    }

    /** RETURNS DATE DATE. */
    public Date getDate() {
        return _date;
    }

    /** RETURNS STRING PARENT. */
    public String getParent() {
        return _parent;
    }

    /** RETURNS STRING ID. */
    public String getId() {
        return _id;
    }

    /** STRING MESSAGE. */
    private String _message;

    /** DATE DATE. */
    private Date _date;

    /** STRING PARENT. */
    private String _parent;

    /** STRING ID. */
    private String _id;

    /** HASHMAP BLOB. */
    private HashMap<String, String> _blobs;
}
