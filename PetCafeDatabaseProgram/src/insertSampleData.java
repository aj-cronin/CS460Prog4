/*
 * To compile and execute this program on lectura:
 *
 *   Add the Oracle JDBC driver to your CLASSPATH environment variable:
 *
 *         export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
 *
 *     (or whatever shell variable set-up you need to perform to add the
 *     JAR file to your Java CLASSPATH)
 *
 *   Compile this file:
 *
 *         javac JDBC.java
 *
 *   Finally, run the program:
 *
 *         java JDBC <oracle username> <oracle password>
 */


import java.io.*;
import java.sql.*;                 // For access to the SQL interaction methods

public class insertSampleData {
        public static void main (String [] args){

        final String oracleURL =   // Magic lectura -> aloe access spell
                        "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

        String username = null,    // Oracle DBMS username
               password = null;    // Oracle DBMS password


        if (args.length == 2) {    // get username/password from cmd line args
            username = args[0];
            password = args[1];
        } else {
            System.out.println("\nUsage:  java JDBC <username> <password>\n"
                             + "    where <username> is your Oracle DBMS"
                             + " username,\n    and <password> is your Oracle"
                             + " password (not your system password).\n");
            System.exit(-1);
        }


            // load the (Oracle) JDBC driver by initializing its base
            // class, 'oracle.jdbc.OracleDriver'.

        try {

                Class.forName("oracle.jdbc.OracleDriver");

        } catch (ClassNotFoundException e) {

                System.err.println("*** ClassNotFoundException:  "
                    + "Error loading Oracle JDBC driver.  \n"
                    + "\tPerhaps the driver is not on the Classpath?");
                System.exit(-1);

        }


            // make and return a database connection to the user's
            // Oracle database

        Connection dbconn = null;

        try {
                dbconn = DriverManager.getConnection
                               (oracleURL,username,password);

        } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not open JDBC connection.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

        }


            // Send the query to the DBMS, and get and display the results

        Statement stmt = null;

        try {

            stmt = dbconn.createStatement();

            executeSQLFile("dropTables", stmt);
            executeSQLFile("createTables", stmt);

            insertData("Adoption_Application", new int[] {3 , 6}, stmt);
            insertData("Adoption", new int[] {4, 6}, stmt);
            // insertSampleEventRegistration(stmt);
            // insertSampleEvent(stmt);
            // insertSampleHealthRecord(stmt);
            // insertSampleMembershipTier(stmt);
            // insertSampleMenuItem(stmt);
            // insertSampleOrderItem(stmt);
            // insertSampleOrder(stmt);
            // insertSamplePet(stmt);
            // insertSampleReservation(stmt);
            // insertSampleRoom(stmt);
            // insertSampleStaff(stmt);

            stmt.close();
            dbconn.close();

        } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

        }

    }

    private static void executeSQLFile(String fileName, Statement stmt) throws SQLException {
         File fileContent = new File(String.format("initialSQLfiles/%s.sql", fileName));

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileContent));
        } catch (FileNotFoundException e) {
            System.out.println(e);
            System.exit(-1);
        }

        try {
            // Skip Header
            String currLine = reader.readLine();

            String allLines = "";

            while (currLine != null) {
                allLines += currLine;
                currLine = reader.readLine();
            }

            for (String d : allLines.split(";")) {
                stmt.executeQuery(d);
            }

            System.out.println(String.format("Successfully exexuted %s.sql", fileName));
        } catch (IOException e){
            System.out.println(e);
            System.exit(-1);
        }

    }

    
    private static void insertData(String tableName, int[] dateColumnIndicies, Statement stmt) throws SQLException {
        File fileContent = new File(String.format("SampleDataCSVs/%s.csv", tableName));

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileContent));
        } catch (FileNotFoundException e) {
            System.out.println(e);
            System.exit(-1);
        }

        try {
            // Skip Header
            String currLine = reader.readLine();

            currLine = reader.readLine();

            while (currLine != null) {
                String[] splitLine = currLine.split(",");

                for (int i = 0; i < splitLine.length; i++) {

                    if (splitLine[i].equals("")) {
                        splitLine[i] = "NULL";
                    }

                    if(!contains(dateColumnIndicies, i)) {
                        if (!isNumeric(splitLine[i])) splitLine[i] = String.format("'%s'", splitLine[i]);
                    } else splitLine[i] = splitLine[i] = String.format("TO_DATE('%s', 'MM-DD-YYYY')", splitLine[i]);
                }

                String valueList = String.join(",", splitLine);

                String currInsert = String.format("INSERT INTO ajcronin.%s VALUES (%s)", tableName, valueList);

                stmt.executeQuery(currInsert);

                currLine = reader.readLine();
            }

            System.out.println(String.format("Successfully imported for %s Data", tableName));
        } catch (IOException e){
            System.out.println(e);
            System.exit(-1);
        }
    }

    private static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private static boolean contains(int[] numArr, int num) {
        for (int i : numArr) {
            if (i == num) return true;
        }

        return false;
    }

    private static void insertSampleEventRegistration(Statement stmt) {
        throw new UnsupportedOperationException("Unimplemented method 'insertSampleEventRegistration'");
    }

    private static void insertSampleEvent(Statement stmt) {
        throw new UnsupportedOperationException("Unimplemented method 'insertSampleEvent'");
    }

    private static void insertSampleHealthRecord(Statement stmt) {
        throw new UnsupportedOperationException("Unimplemented method 'insertSampleHealthRecord'");
    }

    private static void insertSampleMembershipTier(Statement stmt) {
        throw new UnsupportedOperationException("Unimplemented method 'insertSampleMembershipTier'");
    }

    private static void insertSampleMenuItem(Statement stmt) {
        throw new UnsupportedOperationException("Unimplemented method 'insertSampleMenuItem'");
    }

    private static void insertSampleOrderItem(Statement stmt) {
        throw new UnsupportedOperationException("Unimplemented method 'insertSampleOrderItem'");
    }

    private static void insertSampleOrder(Statement stmt) {
        throw new UnsupportedOperationException("Unimplemented method 'insertSampleOrder'");
    }

    private static void insertSamplePet(Statement stmt) {
        throw new UnsupportedOperationException("Unimplemented method 'insertSamplePet'");
    }

    private static void insertSampleReservation(Statement stmt) {
        throw new UnsupportedOperationException("Unimplemented method 'insertSampleReservation'");
    }

    private static void insertSampleRoom(Statement stmt) {
        throw new UnsupportedOperationException("Unimplemented method 'insertSampleRoom'");
    }

    private static void insertSampleStaff(Statement stmt) {
        // empty implementation
    }




}