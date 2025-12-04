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

            dropTables(stmt);
            createTables(stmt);

            insertSampleAdoptionApplication(stmt);
            // insertSampleAdoption(stmt);
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

    private static void dropTables(Statement stmt) throws SQLException {
         File fileContent = new File("initialSQLfiles/dropTables.sql");

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

            System.out.println("Successfully dropped prior tables");
        } catch (IOException e){
            System.out.println(e);
            System.exit(-1);
        }

    }

    private static void createTables(Statement stmt) throws SQLException {
         File fileContent = new File("initialSQLfiles/createTables.sql");

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

            System.out.println("Successfully created initial tables");
        } catch (IOException e){
            System.out.println(e);
            System.exit(-1);
        }

    }


    private static void insertSampleStaff(Statement stmt) {
            
        }

    private static void insertSampleRoom(Statement stmt) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'insertSampleRoom'");
        }

    private static void insertSampleReservation(Statement stmt) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'insertSampleReservation'");
        }

    private static void insertSamplePet(Statement stmt) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'insertSamplePet'");
        }

    private static void insertSampleOrder(Statement stmt) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'insertSampleOrder'");
        }

    private static void insertSampleOrderItem(Statement stmt) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'insertSampleOrderItem'");
        }

    private static void insertSampleMenuItem(Statement stmt) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'insertSampleMenuItem'");
        }

    private static void insertSampleMembershipTier(Statement stmt) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'insertSampleMembershipTier'");
        }

    private static void insertSampleHealthRecord(Statement stmt) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'insertSampleHealthRecord'");
        }

    private static void insertSampleEvent(Statement stmt) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'insertSampleEvent'");
        }

    private static void insertSampleEventRegistration(Statement stmt) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'insertSampleEventRegistration'");
        }

    private static void insertSampleAdoption(Statement stmt) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'insertSampleAdoption'");
        }

    private static void insertSampleAdoptionApplication(Statement stmt) throws SQLException{
        File fileContent = new File("SampleDataCSVs/Adoption_Application.csv");

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

                    if(i != 6 && i != 3) {
                        if (!isNumeric(splitLine[i])) splitLine[i] = String.format("'%s'", splitLine[i]);
                    } else splitLine[i] = splitLine[i] = String.format("TO_DATE('%s', 'MM-DD-YYYY')", splitLine[i]);


                }

                String valueList = String.join(",", splitLine);
                
                String currInsert = String.format("INSERT INTO ajcronin.Adoption_Application VALUES (%s)", valueList);

                stmt.executeQuery(currInsert);

                currLine = reader.readLine();
            }

            System.out.println("Successfully imported for Adoption_Application Data");
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

}