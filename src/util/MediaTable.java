package util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MediaTable extends JTable {
    public MediaTable(DefaultTableModel model) {
        super(model);
    }
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
