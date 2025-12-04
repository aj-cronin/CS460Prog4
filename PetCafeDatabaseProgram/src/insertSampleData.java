import java.io.*;
import java.sql.*;                 // For access to the SQL interaction methods
import java.io.BufferedReader;

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

            insertSampleAdoptionApplication(stmt);
            insertSampleAdoption(stmt);
            insertSampleEventRegistration(stmt);
            insertSampleEvent(stmt);
            insertSampleHealthRecord(stmt);
            insertSampleMembershipTier(stmt);
            insertSampleMenuItem(stmt);
            insertSampleOrderItem(stmt);
            insertSampleOrder(stmt);
            insertSamplePet(stmt);
            insertSampleReservation(stmt);
            insertSampleRoom(stmt);
            insertSampleStaff(stmt);

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

    private static void insertSampleStaff(Statement stmt) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'insertSampleStaff'");
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

    private static void insertSampleAdoptionApplication(Statement stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertSampleAdoptionApplication'");
    }

}