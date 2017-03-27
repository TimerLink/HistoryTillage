package connection;

import model.DotEnv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static DB instance;
    private static Connection connection;

    private DB(){}

    public static DB getInstance(){
        if (instance == null){
            instance = new DB();
        }
        return instance;
    }

    private static void connect(){
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL = String.format("jdbc:sqlserver://%s:1433;DatabaseName=%s",
                DotEnv.get("SERVER_NAME"),
                DotEnv.get("DB_NAME"));
        String userName = DotEnv.get("DB_USER_NAME");
        String userPwd = DotEnv.get("DB_USER_PWD");
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(dbURL, userName, userPwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        if (connection == null){
            connect();
        }
        return connection;
    }



}
