import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    // Lưu ý: Tên database tôi đang để là ThuVien_Lab02 theo bài học. 
    // Nếu bạn thực sự muốn dùng database TrekBikes như trong JSON của bạn, hãy sửa lại databaseName=TrekBikes
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=ThuVien_UC03;user=sa;password=Khampro321!;encrypt=true;trustServerCertificate=true;";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}