import java.sql.*;
import java.util.Scanner;

public class Prog4 {

	private static final String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java Prog4 <username> <password>");
			System.exit(-1);
		}

		String username = args[0];
		String password = args[1];

		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("*** ClassNotFoundException:  " + "Error loading Oracle JDBC driver.  \n"
					+ "\tPerhaps the driver is not on the Classpath?");
			e.printStackTrace();
			System.exit(-1);
		}

		try (Connection conn = DriverManager.getConnection(oracleURL, username, password);
				Scanner scanner = new Scanner(System.in)) {

			boolean done = false;
			while (!done) {
				System.out.println("Pet Cafe Main Menu");
				System.out.println("1 - Member operations");
				System.out.println("2 - Pet operations");
				System.out.println("3 - Food & beverage order operations");
				System.out.println("4 - Reservation operations");
				System.out.println("5 - Health record operations");
				System.out.println("6 - Adoption application operations");
				System.out.println("7 - Event booking operations");
				System.out.println("8 - Queries");
				System.out.println("0 - Exit");
				System.out.print("Choice: ");

				String line = scanner.nextLine().trim();
				int choice;
				try {
					choice = Integer.parseInt(line);
				} catch (NumberFormatException e) {
					System.out.println("Please enter a number.");
					continue;
				}

				try {
					switch (choice) {
					case 1:
						// AJ
						// handleMemberMenu(conn, scanner);
						System.out.println("[Member menu not implemented yet]");
						break;
					case 2:
						// AJ
						// handlePetMenu(conn, scanner);
						System.out.println("[Pet menu not implemented yet]");
						break;
					case 3:
						handleOrderMenu(conn, scanner);
						break;
					case 4:
						handleReservationMenu(conn, scanner);
						break;
					case 5:
						// Amirkhon
						// handleHealthRecordMenu(conn, scanner);
						System.out.println("[Health record menu not implemented yet]");
						break;
					case 6:
						// Amirkhon
						// handleAdoptionMenu(conn, scanner);
						System.out.println("[Adoption application menu not implemented yet]");
						break;
					case 7:
						// Amirkhon
						// handleEventMenu(conn, scanner);
						System.out.println("[Event booking menu not implemented yet]");
						break;
					case 8:
						handleQueryMenu(conn, scanner);
						break;
					case 0:
						done = true;
						break;
					default:
						System.out.println("Invalid choice.");
					}
				} catch (SQLException e) {
					System.err.println("SQL error: " + e.getMessage());
				}
			}

		} catch (SQLException e) {
			System.err.println("*** SQLException:  " + "Could not open JDBC connection.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}
	}

	// Task 3
	private static void handleOrderMenu(Connection conn, Scanner scanner) throws SQLException {
		boolean back = false;
		while (!back) {
			System.out.println("Food & Beverage Order Menu");
			System.out.println("1 - Add order");
			System.out.println("2 - Update order payment status");
			System.out.println("3 - Delete order");
			System.out.println("0 - Back to main menu");
			System.out.print("Choice: ");

			String line = scanner.nextLine().trim();
			int choice;
			try {
				choice = Integer.parseInt(line);
			} catch (NumberFormatException e) {
				System.out.println("Please enter a number.");
				continue;
			}

			switch (choice) {
			case 1:
				addOrder(conn, scanner);
				break;
			case 2:
				updateOrder(conn, scanner);
				break;
			case 3:
				deleteOrder(conn, scanner);
				break;
			case 0:
				back = true;
				break;
			default:
				System.out.println("Invalid choice.");
			}
		}
	}

	// Task 4
	private static void handleReservationMenu(Connection conn, Scanner scanner) throws SQLException {
		boolean back = false;
		while (!back) {
			System.out.println("Reservation Menu");
			System.out.println("1 - Add reservation");
			System.out.println("2 - Update reservation");
			System.out.println("3 - Delete reservation");
			System.out.println("0 - Back to main menu");
			System.out.print("Choice: ");

			String line = scanner.nextLine().trim();
			int choice;
			try {
				choice = Integer.parseInt(line);
			} catch (NumberFormatException e) {
				System.out.println("Please enter a number.");
				continue;
			}

			switch (choice) {
			case 1:
				addReservation(conn, scanner);
				break;
			case 2:
				updateReservation(conn, scanner);
				break;
			case 3:
				deleteReservation(conn, scanner);
				break;
			case 0:
				back = true;
				break;
			default:
				System.out.println("Invalid choice.");
			}
		}
	}

	// Task 8 (queries)

	private static void handleQueryMenu(Connection conn, Scanner scanner) throws SQLException {
		boolean back = false;
		while (!back) {
			System.out.println("Queries Menu");
			System.out.println("1 - Query 1");
			System.out.println("2 - Query 2");
			System.out.println("3 - Query 3");
			System.out.println("4 - Custom Query 4");
			System.out.println("0 - Back to main menu");
			System.out.print("Choice: ");

			String line = scanner.nextLine().trim();
			int choice;
			try {
				choice = Integer.parseInt(line);
			} catch (NumberFormatException e) {
				System.out.println("Please enter a number.");
				continue;
			}

			switch (choice) {
			case 1:
				// AJ implements Query 1
				System.out.println("[Query 1 not implemented yet]");
				break;
			case 2:
				showVisitHistory(conn, scanner);
				break;
			case 3:
				// Amirkhon implements Query 3
				System.out.println("[Query 3 not implemented yet]");
				break;
			case 4:
				// Amirkhon implements custom Query 4
				System.out.println("[Custom Query 4 not implemented yet]");
				break;
			case 0:
				back = true;
				break;
			default:
				System.out.println("Invalid choice.");
			}
		}
	}

	// Order methods
	private static void deleteOrder(Connection conn, Scanner scanner) {
		// TODO Auto-generated method stub

	}

	private static void updateOrder(Connection conn, Scanner scanner) {
		// TODO Auto-generated method stub

	}

	private static void addOrder(Connection conn, Scanner scanner) {
		// TODO Auto-generated method stub

	}

	// Reservation methods
	private static void addReservation(Connection conn, Scanner scanner) {
		// TODO Auto-generated method stub

	}

	private static void deleteReservation(Connection conn, Scanner scanner) {
		// TODO Auto-generated method stub

	}

	private static void updateReservation(Connection conn, Scanner scanner) {
		// TODO Auto-generated method stub

	}

	// Query 8.2
	private static void showVisitHistory(Connection conn, Scanner scanner) {
		// TODO Auto-generated method stub

	}

}
