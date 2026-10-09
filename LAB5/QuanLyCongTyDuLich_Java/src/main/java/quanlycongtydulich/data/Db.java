package quanlycongtydulich.data;

import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class Db {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyCongTyDuLich;encrypt=false;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "Khampro321!";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            // driver loaded
        }
    }

    public static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static DefaultTableModel query(String sql, Object... params) {
        DefaultTableModel model = new DefaultTableModel();
        try (Connection cn = openConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                for (int i = 1; i <= colCount; i++) {
                    model.addColumn(meta.getColumnLabel(i));
                }
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= colCount; i++) {
                        row.add(rs.getObject(i));
                    }
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Db.query error: " + ex.getMessage());
        }
        return model;
    }

    public static int execute(String sql, Object... params) throws SQLException {
        try (Connection cn = openConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            setParams(ps, params);
            return ps.executeUpdate();
        }
    }

    public static Object scalar(String sql, Object... params) {
        try (Connection cn = openConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getObject(1);
            }
        } catch (SQLException ex) {
            System.err.println("Db.scalar error: " + ex.getMessage());
        }
        return null;
    }

    public static void setParams(PreparedStatement ps, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                if (params[i] == null || (params[i] instanceof String && ((String)params[i]).trim().isEmpty())) {
                    ps.setNull(i + 1, Types.NULL);
                } else {
                    ps.setObject(i + 1, params[i]);
                }
            }
        }
    }
}
