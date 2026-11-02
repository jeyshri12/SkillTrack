
package com.skilltrack;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/skilltrack";

    private static final String USER = "root";

    private static final String PASSWORD = System.getenv("SKILLTRACK_DB_PASSWORD");

    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(
                    URL, USER, PASSWORD);

            System.out.println("Database Connected Successfully!");

            return con;

        } catch (Exception e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }
}
