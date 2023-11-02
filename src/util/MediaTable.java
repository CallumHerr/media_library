package util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MediaTable extends JTable {
    /**
     * If not parameters then pass nothing to the JTable parent class
     */
    public MediaTable() { super(); }

    /**
     * If a DefaultTableModel is provided then pass it into the super
     * @param model
     */
    public MediaTable(DefaultTableModel model) {
        super(model);
    }

    /**
     * Stops the cells on the JTable from being editable
     * @param row      the row whose value is to be queried
     * @param column   the column whose value is to be queried
     * @return false
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
