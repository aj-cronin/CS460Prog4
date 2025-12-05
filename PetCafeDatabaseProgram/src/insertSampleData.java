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

            insertData("Adoption_Application", new int[] {3 , 6}, new int[] {}, stmt);
            insertData("Adoption", new int[] {4, 6}, new int[] {}, stmt);
            insertData("Customer_Order", new int[] {3}, new int[] {4}, stmt);
            insertData("Event_Registration", new int[] {2}, new int[] {}, stmt);
            insertData("Event", new int[] {4}, new int[] {5, 6}, stmt);
            insertData("Health_Record", new int[] {3}, new int[] {}, stmt);
            insertData("Member", new int[] {4}, new int[] {}, stmt);
            insertData("Membership_Tier", new int[] {}, new int[] {}, stmt);
            insertData("Menu_Item", new int[] {}, new int[] {}, stmt);
            insertData("Order_Item", new int[] {}, new int[] {}, stmt);
            insertData("Pet", new int[] {5}, new int[] {}, stmt);
            insertData("Reservation", new int[] {3}, new int[] {4, 7, 8}, stmt);
            insertData("Room", new int[] {}, new int[] {}, stmt);
            insertData("Staff", new int[] {4}, new int[] {}, stmt);

            stmt.close();
            dbconn.close();

            System.out.println("All Sample Data has been inserted Successfully...");

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

            System.out.println(String.format("Successfully executed %s.sql", fileName));
        } catch (IOException e){
            System.out.println(e);
            System.exit(-1);
        }

    }

    
    private static void insertData(String tableName, int[] dateColumnIndicies, int[] timeColumnIndices, Statement stmt) throws SQLException {
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

                    if(contains(dateColumnIndicies, i)) {
                        splitLine[i] = splitLine[i] = String.format("TO_DATE('%s', 'MM-DD-YYYY')", splitLine[i]);
                    } else if (contains(timeColumnIndices, i)) {
                        splitLine[i] = splitLine[i] = String.format("TO_TIMESTAMP('%s', 'HH:MI AM')", splitLine[i]);
                    } else if (!isNumeric(splitLine[i])) {
                        splitLine = flattenCommaString(splitLine, i);
                        splitLine[i] = String.format("'%s'", splitLine[i].replace("'", "''").replace("\"", ""));
                    }
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

    private static String[] flattenCommaString(String[] splitLine, int i) {
        String currString = splitLine[i];

        if (currString.charAt(0) != '"') return splitLine;

        while (splitLine[i + 1].charAt(splitLine[i+1].length()-1) != '"') {
            splitLine[i] += splitLine[i+1];

            splitLine = remove(splitLine, i + 1);
        }

        splitLine[i] += splitLine[i+1];
        splitLine = remove(splitLine, i + 1);

        return splitLine;
    }

    private static String[] remove(String[] arr, int i) {
        String[] newArr = new String[arr.length - 1];

        for (int j = 0; j < i; j++) {
            newArr[j] = arr[j];
        }

        for (int j = i; j < arr.length - 1; j++) {
            newArr[j] = arr[j+1];
        }

        return newArr;
    }
}