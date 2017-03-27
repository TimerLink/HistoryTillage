package connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DBModel {

    public static boolean execute(String sql){
        Connection connection = DB.getInstance().getConnection();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            return stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)try {
                stmt.close();
            }catch (Exception ignored){}
        }
        return false;
    }

    public static void executeQuery(String sql, OnQueryResultListener listener){
        Connection connection = DB.getInstance().getConnection();
        ResultSet rs = null;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            if (listener != null) listener.onResult(rs);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (rs != null) try {
                rs.close();
            }catch (Exception ignored){}
            if (stmt != null) try {
                stmt.close();
            }catch (Exception ignored){}
        }
    }

    public abstract boolean commit();

    public interface OnQueryResultListener {
        void onResult (ResultSet rs) throws SQLException;
    }

}
