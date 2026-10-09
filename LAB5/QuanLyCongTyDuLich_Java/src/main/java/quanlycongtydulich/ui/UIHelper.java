package quanlycongtydulich.ui;

import java.awt.Component;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import quanlycongtydulich.services.Models.ProcessResult;

public class UIHelper {
    public static class ComboItem {
        public String display;
        public String value;
        public Object extra;

        public ComboItem(String display, String value) {
            this.display = display;
            this.value = value;
        }

        public ComboItem(String display, String value, Object extra) {
            this.display = display;
            this.value = value;
            this.extra = extra;
        }

        @Override
        public String toString() {
            return display;
        }
    }

    public static boolean bao(Component parent, ProcessResult k) {
        if (k == null) return false;
        JOptionPane.showMessageDialog(parent, k.getMessage(),
                k.isSuccess() ? "Thông báo" : "Không thực hiện được",
                k.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        return k.isSuccess();
    }

    public static void napCombo(JComboBox<ComboItem> cbo, DefaultTableModel model, String colDisplay, String colValue) {
        cbo.removeAllItems();
        int idxDisp = -1, idxVal = -1;
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (model.getColumnName(i).equalsIgnoreCase(colDisplay)) idxDisp = i;
            if (model.getColumnName(i).equalsIgnoreCase(colValue)) idxVal = i;
        }
        if (idxDisp == -1 || idxVal == -1) return;

        for (int r = 0; r < model.getRowCount(); r++) {
            String d = String.valueOf(model.getValueAt(r, idxDisp));
            String v = String.valueOf(model.getValueAt(r, idxVal));
            cbo.addItem(new ComboItem(d, v));
        }
    }

    public static String getComboValue(JComboBox<ComboItem> cbo) {
        ComboItem item = (ComboItem) cbo.getSelectedItem();
        return item == null ? "" : item.value;
    }

    public static String getSelectedValue(JTable table, String colName) {
        int row = table.getSelectedRow();
        if (row == -1) return "";
        for (int c = 0; c < table.getColumnCount(); c++) {
            if (table.getColumnName(c).equalsIgnoreCase(colName)) {
                Object val = table.getValueAt(row, c);
                return val == null ? "" : val.toString();
            }
        }
        return "";
    }
}
