package ch.elca.el4j.seam.generic;

import java.io.Serializable;

/**
 * Helper class to store name and "linked"-property of a column.
 * 
 * @author Baeni Christoph (CBA)
 */
public class TableColumn implements Comparable<TableColumn>, Serializable {
    private String m_name;
    private boolean m_linked;

    public TableColumn(String decoratedName, boolean forceLinked) {
        m_linked = forceLinked
            || ((decoratedName.length() >= 1) && (decoratedName.charAt(0) == '@'));
        m_name = decoratedName.replaceFirst("^@", "");
    }

    public TableColumn(String decoratedName) {
        this(decoratedName, false);
    }

    public String getName() {
        return m_name;
    }

    public String getLinked() {
        return m_linked ? "y" : "n";
    }

    public int compareTo(TableColumn other) {
        return m_name.compareTo(other.getName());
    }
}