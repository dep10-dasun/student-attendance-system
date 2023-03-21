package lk.ijse.dep10;

import javafx.application.Application;
import javafx.stage.Stage;
import lk.ijse.dep10.db.DBConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {

                if (!DBConnection.getInstance().getConnection().isClosed() && DBConnection.getInstance().getConnection() != null) {
                    System.out.println("Database conncetion is about to close");
                    DBConnection.getInstance().getConnection().close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        generateTables();
    }

    private void generateTables() {

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SHOW TABLES");

            Connection connection1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/dep10_Students", "root", "mysql");
            HashSet<String> tableNameSet = new HashSet<>();
            while (rst.next()) {
                tableNameSet.add(rst.getString(1));

            }
            boolean tableExists = tableNameSet.containsAll(Set.of("Attendance", "Picture", "Student", "User"));
            System.out.println(tableExists);

            if (!tableExists) {
                System.out.println("Schema is about to auto generate");
                stm.execute(readSchemaScript());
            }

//            DatabaseMetaData metaData = connection1.getMetaData();
//            String[] types = {"TABLE"};
//            ResultSet rs = metaData.getTables("dep10_Students", null, "%", types);
//            while (rs.next()) {
//                String tableName = rs.getString("TABLE_NAME");
//                System.out.println(tableName);
//            }

//            if (rst.next()) {
//
//                InputStream is = getClass().getResourceAsStream("/schema.sql");
//                BufferedReader br = new BufferedReader(new InputStreamReader(is));
//                String line;
//                StringBuilder dbScript = new StringBuilder();
//                while ((line = br.readLine()) != null) {
//                    dbScript.append(line).append("\n");
//                }
//                br.close();
//                stm.execute(dbScript.toString());
//            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String readSchemaScript() throws IOException {
        InputStream is = getClass().getResourceAsStream("/schema.sql");
        try (
            BufferedReader br = new BufferedReader(new InputStreamReader(is))){
            String line;
            StringBuilder dbScript = new StringBuilder();
            while ((line = br.readLine()) != null) {
                dbScript.append(line);
            }
            return dbScript.toString();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
