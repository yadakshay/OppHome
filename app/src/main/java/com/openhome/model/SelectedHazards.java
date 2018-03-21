package com.openhome.model;

/**
 * Created by Bhargav on 10/17/2016.
 */

public class SelectedHazards {

    private String superParent;
    private String parent;
    private String child;
    private String id;
    private boolean checked;

    public SelectedHazards(String superParent, String parent, String child, String id, boolean checked) {
        this.superParent = superParent;
        this.parent = parent;
        this.child = child;
        this.id = id;
        this.checked = checked;
    }

    public String getSuperParent() {
        return superParent;
    }

    public String getParent() {
        return parent;
    }

    public String getChild() {
        return child;
    }

    public String getId() {
        return id;
    }
}
