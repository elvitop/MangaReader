package com.NitroReader.utilities;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBAccess {
    private DBAccess() {
    }
    private static Connection connection = null;
    private static PropertiesReader props = PropertiesReader.getInstance();
    public static Connection getConnection() {
        try {
            if (connection == null) {
                Class.forName(props.getValue("dbDriver"));
                connection = DriverManager.getConnection(props.getValue("dbURL"),props.getValue("dbUser"),props.getValue("dbPassword"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(props.getValue("dbError") + " " + e.getMessage());
        }
        return connection;
    }

    public static DBAccess getInstance() {
        return null;
    }


    public void closeConnection(Connection con){
        if (con != null){
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection createConnection() {
        return null;
    }
}
