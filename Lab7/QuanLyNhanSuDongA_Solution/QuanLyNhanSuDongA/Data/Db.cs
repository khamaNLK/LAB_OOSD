using System;
using System.Configuration;
using System.Data;
using System.Data.SqlClient;
using System.IO;
using System.Text.RegularExpressions;

namespace QuanLyNhanSuDongA.Data
{
    public static class Db
    {
        private static string _resolvedConnectionString;

        public static string ConnectionString
        {
            get
            {
                if (!string.IsNullOrEmpty(_resolvedConnectionString))
                    return _resolvedConnectionString;

                var cs = ConfigurationManager.ConnectionStrings["QuanLyNhanSuDongADB"];
                if (cs != null && !string.IsNullOrWhiteSpace(cs.ConnectionString))
                    return cs.ConnectionString;

                return @"Data Source=(localdb)\MSSQLLocalDB;Initial Catalog=QuanLyNhanSuDongA;Integrated Security=True;TrustServerCertificate=True";
            }
        }

        /// <summary>
        /// Tự động kiểm tra kết nối SQL Server và tự động khởi tạo CSDL QuanLyNhanSuDongA
        /// kèm dữ liệu mẫu nếu người dùng chưa chạy script SQL trước đó.
        /// </summary>
        public static void EnsureDatabaseReady()
        {
            string[] candidateServers = new[]
            {
                GetDataSourceFromConfig(),
                @"(localdb)\MSSQLLocalDB",
                @".\SQLEXPRESS",
                @"localhost\SQLEXPRESS",
                @"."
            };

            Exception lastEx = null;
            foreach (string server in candidateServers)
            {
                if (string.IsNullOrWhiteSpace(server)) continue;
                string targetCs = $"Data Source={server};Initial Catalog=QuanLyNhanSuDongA;Integrated Security=True;TrustServerCertificate=True;Connect Timeout=5";
                try
                {
                    using (var cn = new SqlConnection(targetCs))
                    {
                        cn.Open();
                        using (var cmd = new SqlCommand("SELECT COUNT(*) FROM dbo.PhongBan", cn))
                        {
                            cmd.ExecuteScalar();
                        }
                    }
                    _resolvedConnectionString = $"Data Source={server};Initial Catalog=QuanLyNhanSuDongA;Integrated Security=True;TrustServerCertificate=True";
                    return;
                }
                catch (Exception ex)
                {
                    lastEx = ex;
                    // Thử kết nối vào master để tự động tạo Database từ script QuanLyNhanSuDongA.sql
                    try
                    {
                        string masterCs = $"Data Source={server};Initial Catalog=master;Integrated Security=True;TrustServerCertificate=True;Connect Timeout=5";
                        using (var mcn = new SqlConnection(masterCs))
                        {
                            mcn.Open();
                            string sqlFile = FindSqlScriptPath();
                            if (!string.IsNullOrEmpty(sqlFile) && File.Exists(sqlFile))
                            {
                                string script = File.ReadAllText(sqlFile);
                                string[] batches = Regex.Split(script, @"^\s*GO\s*$", RegexOptions.Multiline | RegexOptions.IgnoreCase);
                                foreach (string batch in batches)
                                {
                                    if (string.IsNullOrWhiteSpace(batch)) continue;
                                    using (var cmd = new SqlCommand(batch, mcn))
                                    {
                                        cmd.CommandTimeout = 60;
                                        cmd.ExecuteNonQuery();
                                    }
                                }
                                _resolvedConnectionString = $"Data Source={server};Initial Catalog=QuanLyNhanSuDongA;Integrated Security=True;TrustServerCertificate=True";
                                return;
                            }
                        }
                    }
                    catch (Exception innerEx)
                    {
                        lastEx = innerEx;
                    }
                }
            }

            if (lastEx != null)
                throw lastEx;
        }

        private static string GetDataSourceFromConfig()
        {
            try
            {
                var cs = ConfigurationManager.ConnectionStrings["QuanLyNhanSuDongADB"];
                if (cs != null && !string.IsNullOrWhiteSpace(cs.ConnectionString))
                {
                    var builder = new SqlConnectionStringBuilder(cs.ConnectionString);
                    return builder.DataSource;
                }
            }
            catch { }
            return @"(localdb)\MSSQLLocalDB";
        }

        private static string FindSqlScriptPath()
        {
            string baseDir = AppDomain.CurrentDomain.BaseDirectory;
            string[] paths = new[]
            {
                Path.Combine(baseDir, "Database", "QuanLyNhanSuDongA.sql"),
                Path.Combine(baseDir, "..", "..", "Database", "QuanLyNhanSuDongA.sql"),
                Path.Combine(baseDir, "QuanLyNhanSuDongA.sql")
            };
            foreach (var p in paths)
            {
                if (File.Exists(p)) return Path.GetFullPath(p);
            }
            return null;
        }

        public static SqlConnection OpenConnection()
        {
            var cn = new SqlConnection(ConnectionString);
            cn.Open();
            return cn;
        }

        public static SqlParameter P(string name, object value)
        {
            if (value == null) return new SqlParameter(name, DBNull.Value);
            if (value is string s && string.IsNullOrWhiteSpace(s)) return new SqlParameter(name, DBNull.Value);
            return new SqlParameter(name, value);
        }

        public static DataTable Query(string sql, params SqlParameter[] ps)
        {
            using (var cn = OpenConnection())
            using (var cmd = new SqlCommand(sql, cn))
            {
                if (ps != null && ps.Length > 0) cmd.Parameters.AddRange(ps);
                using (var da = new SqlDataAdapter(cmd))
                {
                    var dt = new DataTable();
                    da.Fill(dt);
                    return dt;
                }
            }
        }

        public static int Execute(string sql, params SqlParameter[] ps)
        {
            using (var cn = OpenConnection())
            using (var cmd = new SqlCommand(sql, cn))
            {
                if (ps != null && ps.Length > 0) cmd.Parameters.AddRange(ps);
                return cmd.ExecuteNonQuery();
            }
        }

        public static int ExecuteInTran(SqlConnection cn, SqlTransaction tr, string sql, params SqlParameter[] ps)
        {
            using (var cmd = new SqlCommand(sql, cn, tr))
            {
                if (ps != null && ps.Length > 0) cmd.Parameters.AddRange(ps);
                return cmd.ExecuteNonQuery();
            }
        }

        public static object Scalar(string sql, params SqlParameter[] ps)
        {
            using (var cn = OpenConnection())
            using (var cmd = new SqlCommand(sql, cn))
            {
                if (ps != null && ps.Length > 0) cmd.Parameters.AddRange(ps);
                return cmd.ExecuteScalar();
            }
        }
    }
}
