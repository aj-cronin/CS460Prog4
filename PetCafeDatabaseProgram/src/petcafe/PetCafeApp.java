package petcafe;

import java.sql.*;
import java.util.Scanner;

public class PetCafeApp {

    private final Connection conn;
    private final Scanner in;

    public PetCafeApp(Connection conn) {
        this.conn = conn;
        this.in = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.print("Oracle username: ");
        String user = s.nextLine().trim();
        System.out.print("Oracle password: ");
        String pass = s.nextLine().trim();

        try (Connection conn = DBUtil.getConnection(user, pass)) {
            conn.setAutoCommit(true);
            PetCafeApp app = new PetCafeApp(conn);
            app.mainMenu();
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    private void mainMenu() {
        while (true) {
            System.out.println("\n==== Pet Cafe Management ====");
            System.out.println("1. Members");
            System.out.println("2. Reservations");
            System.out.println("3. Orders");
            System.out.println("4. Pets & Adoptions");
            System.out.println("5. Events");
            System.out.println("6. Reports (required queries)");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1": membersMenu(); break;
                case "2": reservationsMenu(); break;
                case "3": ordersMenu(); break;
                case "4": petsMenu(); break;
                case "5": eventsMenu(); break;
                case "6": reportsMenu(); break;
                case "0": System.out.println("Goodbye!"); return;
                default: System.out.println("Invalid choice."); break;
            }
        }
    }

    private void membersMenu() {
        while (true) {
            System.out.println("\n-- Members --");
            System.out.println("1. Add member");
            System.out.println("2. Update member");
            System.out.println("3. Delete member");
            System.out.println("4. List members");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            String c = in.nextLine().trim();
            try {
                switch (c) {
                    case "1": addMember(); break;
                    case "2": updateMember(); break;
                    case "3": deleteMember(); break;
                    case "4": listMembers(); break;
                    case "0": return;
                    default: System.out.println("Invalid choice."); break;
                }
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private int nextId(String table, String col) throws SQLException {
        String sql = "SELECT NVL(MAX(" + col + "),0) + 1 FROM " + table;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private void addMember() throws SQLException {
        int memberId = nextId("Member", "member_id");
        System.out.println("New member id: " + memberId);

        System.out.print("Name: ");
        String name = in.nextLine();
        System.out.print("Phone (digits only): ");
        String phoneStr = in.nextLine();
        Integer phone = phoneStr.isEmpty() ? null : Integer.parseInt(phoneStr);
        System.out.print("Email: ");
        String email = in.nextLine();
        System.out.print("Date of birth (YYYY-MM-DD) or blank: ");
        String dobStr = in.nextLine();
        java.sql.Date dob = dobStr.isEmpty() ? null : java.sql.Date.valueOf(dobStr);
        System.out.print("Emergency contact: ");
        String emergency = in.nextLine();
        System.out.print("Tier id (or blank): ");
        String tierStr = in.nextLine();
        Integer tierId = tierStr.isEmpty() ? null : Integer.parseInt(tierStr);

        String sql = "INSERT INTO Member (member_id, name, phone, email, date_of_birth, emergency_contact, tier_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setString(2, name);
            if (phone == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, phone);
            ps.setString(4, email);
            if (dob == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, dob);
            ps.setString(6, emergency);
            if (tierId == null) ps.setNull(7, Types.INTEGER);
            else ps.setInt(7, tierId);
            ps.executeUpdate();
        }
        System.out.println("Member inserted.");
    }

    private void updateMember() throws SQLException {
        System.out.print("Member id to update: ");
        int id = Integer.parseInt(in.nextLine());
        System.out.print("New phone (blank to keep): ");
        String phoneStr = in.nextLine();
        System.out.print("New email (blank to keep): ");
        String email = in.nextLine();
        System.out.print("New tier id (blank to keep): ");
        String tierStr = in.nextLine();

        String sql = "UPDATE Member SET phone = COALESCE(?, phone), email = COALESCE(?, email), tier_id = COALESCE(?, tier_id) WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (phoneStr.isEmpty()) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, Integer.parseInt(phoneStr));
            if (email.isEmpty()) ps.setNull(2, Types.VARCHAR);
            else ps.setString(2, email);
            if (tierStr.isEmpty()) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, Integer.parseInt(tierStr));
            ps.setInt(4, id);

            int rows = ps.executeUpdate();
            if (rows == 0) System.out.println("No such member.");
            else System.out.println("Member updated.");
        }
    }

    private void deleteMember() throws SQLException {
        System.out.print("Member id to delete: ");
        int memberId = Integer.parseInt(in.nextLine());

        String checkSql =
                "SELECT " +
                        "(SELECT COUNT(*) FROM Reservation WHERE member_id = ? AND status IN ('BOOKED','IN_PROGRESS')) AS active_res, " +
                        "(SELECT COUNT(*) FROM Adoption_Application WHERE member_id = ? AND status = 'PENDING') AS pending_app, " +
                        "(SELECT COUNT(*) FROM Customer_Order WHERE member_id = ? AND payment_status <> 'PAID') AS unpaid_orders " +
                        "FROM dual";

        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, memberId);
            ps.setInt(2, memberId);
            ps.setInt(3, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt("active_res") > 0 ||
                        rs.getInt("pending_app") > 0 ||
                        rs.getInt("unpaid_orders") > 0) {
                    System.out.println("Cannot delete member; outstanding activity exists.");
                    return;
                }
            }
        }

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Member WHERE member_id = ?")) {
            ps.setInt(1, memberId);
            int rows = ps.executeUpdate();
            if (rows == 0) System.out.println("No such member.");
            else System.out.println("Member deleted.");
        }
    }

    private void listMembers() throws SQLException {
        String sql = "SELECT member_id, name, email, tier_id FROM Member ORDER BY member_id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("%d | %s | %s | tier %s%n",
                        rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4));
            }
        }
    }

    private void reservationsMenu() {
        while (true) {
            System.out.println("\n-- Reservations --");
            System.out.println("1. Add reservation");
            System.out.println("2. Update status/check-out");
            System.out.println("3. Cancel reservation");
            System.out.println("4. List reservations");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            String c = in.nextLine().trim();
            try {
                switch (c) {
                    case "1": addReservation(); break;
                    case "2": updateReservationStatus(); break;
                    case "3": cancelReservation(); break;
                    case "4": listReservations(); break;
                    case "0": return;
                    default: System.out.println("Invalid choice."); break;
                }
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void addReservation() throws SQLException {
        int resId = nextId("Reservation", "reservation_id");
        System.out.println("New reservation id: " + resId);

        System.out.print("Member id: ");
        int memberId = Integer.parseInt(in.nextLine());
        System.out.print("Room id: ");
        int roomId = Integer.parseInt(in.nextLine());
        System.out.print("Reservation date (YYYY-MM-DD): ");
        java.sql.Date resDate = java.sql.Date.valueOf(in.nextLine());
        System.out.print("Start time (YYYY-MM-DD HH:MM:SS): ");
        Timestamp start = Timestamp.valueOf(in.nextLine());
        System.out.print("Duration minutes: ");
        int duration = Integer.parseInt(in.nextLine());
        System.out.print("Tier id at visit (or blank): ");
        String tierStr = in.nextLine();
        Integer tierId;
        if (tierStr.isEmpty()) {
            String sql = "SELECT tier_id FROM Member WHERE member_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, memberId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) tierId = (Integer) rs.getObject(1);
                    else {
                        System.out.println("No such member.");
                        return;
                    }
                }
            }
        } else tierId = Integer.parseInt(tierStr);

        int capacity;
        try (PreparedStatement ps = conn.prepareStatement("SELECT max_capacity FROM Room WHERE room_id = ?")) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Invalid room.");
                    return;
                }
                capacity = rs.getInt(1);
            }
        }

        Timestamp endTime = new Timestamp(start.getTime() + duration * 60000L);

        String countSql =
                "SELECT COUNT(*) FROM Reservation WHERE room_id = ? AND reservation_date = ? AND (start_time < ?) AND (start_time + NUMTODSINTERVAL(duration_minutes, 'MINUTE') > ?)";
        try (PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setInt(1, roomId);
            ps.setDate(2, resDate);
            ps.setTimestamp(3, endTime);
            ps.setTimestamp(4, start);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) >= capacity) {
                    System.out.println("Room capacity exceeded; cannot book.");
                    return;
                }
            }
        }

        String sql =
                "INSERT INTO Reservation (reservation_id, member_id, room_id, reservation_date, start_time, duration_minutes, status, check_in_time, check_out_time, tier_id) VALUES (?, ?, ?, ?, ?, ?, 'BOOKED', NULL, NULL, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resId);
            ps.setInt(2, memberId);
            ps.setInt(3, roomId);
            ps.setDate(4, resDate);
            ps.setTimestamp(5, start);
            ps.setInt(6, duration);
            if (tierId == null) ps.setNull(7, Types.INTEGER);
            else ps.setInt(7, tierId);
            ps.executeUpdate();
        }
        System.out.println("Reservation created.");
    }

    private void updateReservationStatus() throws SQLException {
        System.out.print("Reservation id: ");
        int id = Integer.parseInt(in.nextLine());
        System.out.print("New status: ");
        String status = in.nextLine();
        System.out.print("Set check-out time to now? (y/n): ");
        boolean setCheckout = in.nextLine().trim().equalsIgnoreCase("y");

        String sql =
                "UPDATE Reservation SET status = ?, check_out_time = CASE WHEN ? = 'Y' THEN SYSTIMESTAMP ELSE check_out_time END WHERE reservation_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, setCheckout ? "Y" : "N");
            ps.setInt(3, id);
            int rows = ps.executeUpdate();
            if (rows == 0) System.out.println("No such reservation.");
            else System.out.println("Reservation updated.");
        }
    }

    private void cancelReservation() throws SQLException {
        System.out.print("Reservation id to cancel: ");
        int id = Integer.parseInt(in.nextLine());

        String checkSql = "SELECT reservation_date, start_time FROM Reservation WHERE reservation_id = ?";
        java.sql.Date date;
        Timestamp start;
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No such reservation.");
                    return;
                }
                date = rs.getDate(1);
                start = rs.getTimestamp(2);
            }
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (!date.after(new java.sql.Date(now.getTime())) && !start.after(now)) {
            System.out.println("Cannot cancel past/ongoing reservation.");
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Customer_Order WHERE reservation_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    System.out.println("Cannot cancel; orders exist.");
                    return;
                }
            }
        }

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Reservation WHERE reservation_id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        System.out.println("Reservation canceled.");
    }

    private void listReservations() throws SQLException {
        String sql = "SELECT r.reservation_id, r.reservation_date, r.start_time, r.status, m.name, rm.room_name FROM Reservation r JOIN Member m ON r.member_id = m.member_id JOIN Room rm ON r.room_id = rm.room_id ORDER BY r.reservation_date, r.start_time";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("%d | %s | %s | %s | %s | %s%n",
                        rs.getInt(1), rs.getDate(2),
                        rs.getTimestamp(3), rs.getString(4),
                        rs.getString(5), rs.getString(6));
            }
        }
    }

    private void ordersMenu() {
        while (true) {
            System.out.println("\n-- Orders --");
            System.out.println("1. Create order");
            System.out.println("2. Mark order as paid");
            System.out.println("3. Delete order");
            System.out.println("4. List orders");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            String c = in.nextLine().trim();
            try {
                switch (c) {
                    case "1": createOrder(); break;
                    case "2": markOrderPaid(); break;
                    case "3": deleteOrder(); break;
                    case "4": listOrders(); break;
                    case "0": return;
                    default: System.out.println("Invalid choice."); break;
                }
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void createOrder() throws SQLException {
        int orderId = nextId("Customer_Order", "order_id");
        System.out.println("New order id: " + orderId);
        System.out.print("Member id (or blank): ");
        String memberStr = in.nextLine();
        Integer memberId = memberStr.isEmpty() ? null : Integer.parseInt(memberStr);
        System.out.print("Reservation id (or blank): ");
        String resStr = in.nextLine();
        Integer resId = resStr.isEmpty() ? null : Integer.parseInt(resStr);

        String insertOrder =
                "INSERT INTO Customer_Order (order_id, member_id, reservation_id, order_date, order_time, total_price, payment_status) VALUES (?, ?, ?, TRUNC(SYSDATE), SYSTIMESTAMP, 0, 'UNPAID')";
        try (PreparedStatement ps = conn.prepareStatement(insertOrder)) {
            ps.setInt(1, orderId);
            if (memberId == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, memberId);
            if (resId == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, resId);
            ps.executeUpdate();
        }

        while (true) {
            System.out.print("Add item? (y/n): ");
            if (!in.nextLine().trim().equalsIgnoreCase("y")) break;
            System.out.print("Menu item id: ");
            int itemId = Integer.parseInt(in.nextLine());
            System.out.print("Quantity: ");
            int qty = Integer.parseInt(in.nextLine());

            double price;
            try (PreparedStatement ps = conn.prepareStatement("SELECT base_price FROM Menu_Item WHERE item_id = ?")) {
                ps.setInt(1, itemId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No such menu item.");
                        continue;
                    }
                    price = rs.getDouble(1);
                }
            }

            String insItem = "INSERT INTO Order_Item (order_id, item_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insItem)) {
                ps.setInt(1, orderId);
                ps.setInt(2, itemId);
                ps.setInt(3, qty);
                ps.setDouble(4, price);
                ps.executeUpdate();
            }
        }

        finalizeOrderTotal(orderId);
        System.out.println("Order created.");
    }

    private void finalizeOrderTotal(int orderId) throws SQLException {
        conn.setAutoCommit(false);
        try {
            double baseTotal;
            try (PreparedStatement ps = conn.prepareStatement("SELECT SUM(quantity * unit_price) FROM Order_Item WHERE order_id = ?")) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    baseTotal = rs.getDouble(1);
                }
            }

            int discount = 0;
            String discSql =
                    "SELECT mt.discount_rate FROM Customer_Order co JOIN Member m ON co.member_id = m.member_id JOIN Membership_Tier mt ON m.tier_id = mt.tier_id WHERE co.order_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(discSql)) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) discount = rs.getInt(1);
                }
            }

            double finalTotal = baseTotal * (1.0 - discount / 100.0);

            try (PreparedStatement ps = conn.prepareStatement("UPDATE Customer_Order SET total_price = ?, payment_status = 'UNPAID' WHERE order_id = ?")) {
                ps.setDouble(1, finalTotal);
                ps.setInt(2, orderId);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private void markOrderPaid() throws SQLException {
        System.out.print("Order id: ");
        int id = Integer.parseInt(in.nextLine());
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Customer_Order SET payment_status = 'PAID' WHERE order_id = ?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) System.out.println("No such order.");
            else System.out.println("Marked as PAID.");
        }
    }

    private void deleteOrder() throws SQLException {
        System.out.print("Order id to delete: ");
        int id = Integer.parseInt(in.nextLine());

        try (PreparedStatement ps = conn.prepareStatement("SELECT payment_status FROM Customer_Order WHERE order_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No such order.");
                    return;
                }
                if (!"UNPAID".equalsIgnoreCase(rs.getString(1))) {
                    System.out.println("Can delete only unpaid orders.");
                    return;
                }
            }
        }

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Order_Item WHERE order_id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Customer_Order WHERE order_id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        System.out.println("Order deleted.");
    }

    private void listOrders() throws SQLException {
        String sql = "SELECT order_id, member_id, total_price, payment_status FROM Customer_Order ORDER BY order_id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("%d | member %s | %.2f | %s%n",
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getDouble(3),
                        rs.getString(4));
            }
        }
    }

    private void petsMenu() {
        while (true) {
            System.out.println("\n-- Pets & Adoptions --");
            System.out.println("1. Add pet");
            System.out.println("2. Update pet status/room");
            System.out.println("3. Delete pet");
            System.out.println("4. Add adoption application");
            System.out.println("5. Update application status");
            System.out.println("6. Record adoption");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            String c = in.nextLine().trim();
            try {
                switch (c) {
                    case "1": addPet(); break;
                    case "2": updatePet(); break;
                    case "3": deletePet(); break;
                    case "4": addAdoptionApplication(); break;
                    case "5": updateAdoptionApplication(); break;
                    case "6": addAdoption(); break;
                    case "0": return;
                    default: System.out.println("Invalid choice."); break;
                }
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void addPet() throws SQLException {
        int petId = nextId("Pet", "pet_id");
        System.out.println("New pet id: " + petId);
        System.out.print("Name: ");
        String name = in.nextLine();
        System.out.print("Species: ");
        String species = in.nextLine();
        System.out.print("Breed: ");
        String breed = in.nextLine();
        System.out.print("Age (int): ");
        int age = Integer.parseInt(in.nextLine());
        System.out.print("Current room id (or blank): ");
        String roomStr = in.nextLine();
        Integer roomId = roomStr.isEmpty() ? null : Integer.parseInt(roomStr);

        String sql =
                "INSERT INTO Pet (pet_id, name, species, breed, age, date_of_arrival, temperament, special_needs, status, current_room_id) VALUES (?, ?, ?, ?, ?, TRUNC(SYSDATE), NULL, NULL, 'IN_CARE', ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, petId);
            ps.setString(2, name);
            ps.setString(3, species);
            ps.setString(4, breed);
            ps.setInt(5, age);
            if (roomId == null) ps.setNull(6, Types.INTEGER);
            else ps.setInt(6, roomId);
            ps.executeUpdate();
        }
        System.out.println("Pet added.");
    }

    private void updatePet() throws SQLException {
        System.out.print("Pet id: ");
        int id = Integer.parseInt(in.nextLine());
        System.out.print("New status: ");
        String status = in.nextLine();
        System.out.print("New room id (blank to keep): ");
        String roomStr = in.nextLine();

        String sql =
                "UPDATE Pet SET status = ?, current_room_id = COALESCE(?, current_room_id) WHERE pet_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            if (roomStr.isEmpty()) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, Integer.parseInt(roomStr));
            ps.setInt(3, id);
            int rows = ps.executeUpdate();
            if (rows == 0) System.out.println("No such pet.");
            else System.out.println("Pet updated.");
        }
    }

    private void deletePet() throws SQLException {
        System.out.print("Pet id to delete: ");
        int id = Integer.parseInt(in.nextLine());

        String status;
        try (PreparedStatement ps = conn.prepareStatement("SELECT status FROM Pet WHERE pet_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No such pet.");
                    return;
                }
                status = rs.getString(1);
            }
        }
        if (!("ADOPTED".equalsIgnoreCase(status) || "DECEASED".equalsIgnoreCase(status))) {
            System.out.println("Pet can only be removed when adopted or deceased.");
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Adoption_Application WHERE pet_id = ? AND status = 'PENDING'")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    System.out.println("Pet has pending applications; cannot delete.");
                    return;
                }
            }
        }

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Pet WHERE pet_id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        System.out.println("Pet deleted.");
    }

    private void addAdoptionApplication() throws SQLException {
        int appId = nextId("Adoption_Application", "application_id");
        System.out.println("New application id: " + appId);
        System.out.print("Member id: ");
        int memberId = Integer.parseInt(in.nextLine());
        System.out.print("Pet id: ");
        int petId = Integer.parseInt(in.nextLine());
        System.out.print("Staff id: ");
        int staffId = Integer.parseInt(in.nextLine());

        String sql = "INSERT INTO Adoption_Application (application_id, member_id, pet_id, submitted_date, status, reviewed_by, review_date, notes) VALUES (?, ?, ?, TRUNC(SYSDATE), 'PENDING', ?, NULL, NULL)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appId);
            ps.setInt(2, memberId);
            ps.setInt(3, petId);
            ps.setInt(4, staffId);
            ps.executeUpdate();
        }
        System.out.println("Application created.");
    }

    private void updateAdoptionApplication() throws SQLException {
        System.out.print("Application id: ");
        int id = Integer.parseInt(in.nextLine());
        System.out.print("New status: ");
        String status = in.nextLine();
        System.out.print("Notes: ");
        String notes = in.nextLine();

        String sql =
                "UPDATE Adoption_Application SET status = ?, review_date = TRUNC(SYSDATE), notes = ? WHERE application_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, notes);
            ps.setInt(3, id);
            int rows = ps.executeUpdate();
            if (rows == 0) System.out.println("No such application.");
            else System.out.println("Application updated.");
        }
    }

    private void addAdoption() throws SQLException {
        int adoptId = nextId("Adoption", "adoption_id");
        System.out.println("New adoption id: " + adoptId);
        System.out.print("Application id: ");
        int appId = Integer.parseInt(in.nextLine());

        int petId, memberId;
        try (PreparedStatement ps = conn.prepareStatement("SELECT pet_id, member_id, status FROM Adoption_Application WHERE application_id = ?")) {
            ps.setInt(1, appId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No such application.");
                    return;
                }
                if (!"APPROVED".equalsIgnoreCase(rs.getString("status"))) {
                    System.out.println("Application is not approved.");
                    return;
                }
                petId = rs.getInt("pet_id");
                memberId = rs.getInt("member_id");
            }
        }

        System.out.print("Adoption fee: ");
        double fee = Double.parseDouble(in.nextLine());
        System.out.print("Follow-up date (YYYY-MM-DD) or blank: ");
        String followStr = in.nextLine();
        java.sql.Date follow = followStr.isEmpty() ? null : java.sql.Date.valueOf(followStr);

        String sql =
                "INSERT INTO Adoption (adoption_id, application_id, pet_id, member_id, adoption_date, adoption_fee, follow_up_schedule) VALUES (?, ?, ?, ?, TRUNC(SYSDATE), ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adoptId);
            ps.setInt(2, appId);
            ps.setInt(3, petId);
            ps.setInt(4, memberId);
            ps.setDouble(5, fee);
            if (follow == null) ps.setNull(6, Types.DATE);
            else ps.setDate(6, follow);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement("UPDATE Pet SET status = 'ADOPTED' WHERE pet_id = ?")) {
            ps.setInt(1, petId);
            ps.executeUpdate();
        }

        System.out.println("Adoption recorded.");
    }

    private void eventsMenu() {
        while (true) {
            System.out.println("\n-- Events --");
            System.out.println("1. Add event");
            System.out.println("2. Register member for event");
            System.out.println("3. Update registration status");
            System.out.println("4. List events");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            String c = in.nextLine().trim();
            try {
                switch (c) {
                    case "1": addEvent(); break;
                    case "2": registerForEvent(); break;
                    case "3": updateEventRegistration(); break;
                    case "4": listEvents(); break;
                    case "0": return;
                    default: System.out.println("Invalid choice."); break;
                }
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void addEvent() throws SQLException {
        int eventId = nextId("Event", "event_id");
        System.out.println("New event id: " + eventId);
        System.out.print("Title: ");
        String title = in.nextLine();
        System.out.print("Description: ");
        String desc = in.nextLine();
        System.out.print("Room id: ");
        int roomId = Integer.parseInt(in.nextLine());
        System.out.print("Event date (YYYY-MM-DD): ");
        java.sql.Date date = java.sql.Date.valueOf(in.nextLine());
        System.out.print("Start time (HH:MM): ");
        String startTime = in.nextLine();
        System.out.print("End time (HH:MM): ");
        String endTime = in.nextLine();
        System.out.print("Max attendees: ");
        int max = Integer.parseInt(in.nextLine());
        System.out.print("Event type: ");
        String type = in.nextLine();
        System.out.print("Staff id: ");
        int staffId = Integer.parseInt(in.nextLine());

        String sql =
                "INSERT INTO Event (event_id, title, description, room_id, event_date, start_time, end_time, max_attendees, event_type, staff_id) VALUES (?, ?, ?, ?, ?, TO_TIMESTAMP(?, 'HH24:MI'), TO_TIMESTAMP(?, 'HH24:MI'), ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setString(2, title);
            ps.setString(3, desc);
            ps.setInt(4, roomId);
            ps.setDate(5, date);
            ps.setString(6, startTime);
            ps.setString(7, endTime);
            ps.setInt(8, max);
            ps.setString(9, type);
            ps.setInt(10, staffId);
            ps.executeUpdate();
        }
        System.out.println("Event created.");
    }

    private void registerForEvent() throws SQLException {
        System.out.print("Event id: ");
        int eventId = Integer.parseInt(in.nextLine());
        System.out.print("Member id: ");
        int memberId = Integer.parseInt(in.nextLine());

        int max;
        try (PreparedStatement ps = conn.prepareStatement("SELECT max_attendees FROM Event WHERE event_id = ?")) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No such event.");
                    return;
                }
                max = rs.getInt(1);
            }
        }

        int count;
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Event_Registration WHERE event_id = ? AND attendance_status IN ('REGISTERED','ATTENDED')")) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                count = rs.getInt(1);
            }
        }
        if (count >= max) {
            System.out.println("Event is full.");
            return;
        }

        String sql =
                "INSERT INTO Event_Registration (member_id, event_id, registration_date, attendance_status) VALUES (?, ?, TRUNC(SYSDATE), 'REGISTERED')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setInt(2, eventId);
            ps.executeUpdate();
        }
        System.out.println("Member registered.");
    }

    private void updateEventRegistration() throws SQLException {
        System.out.print("Event id: ");
        int eventId = Integer.parseInt(in.nextLine());
        System.out.print("Member id: ");
        int memberId = Integer.parseInt(in.nextLine());
        System.out.print("New status: ");
        String status = in.nextLine();

        String sql =
                "UPDATE Event_Registration SET attendance_status = ? WHERE member_id = ? AND event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, memberId);
            ps.setInt(3, eventId);
            int rows = ps.executeUpdate();
            if (rows == 0) System.out.println("No such reg.");
            else System.out.println("Registration updated.");
        }
    }

    private void listEvents() throws SQLException {
        String sql =
                "SELECT e.event_id, e.title, e.event_date, e.max_attendees, COUNT(er.member_id) AS registered FROM Event e LEFT JOIN Event_Registration er ON e.event_id = er.event_id AND er.attendance_status IN ('REGISTERED','ATTENDED') GROUP BY e.event_id, e.title, e.event_date, e.max_attendees ORDER BY e.event_date";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("%d | %s | %s | %d/%d%n",
                        rs.getInt("event_id"),
                        rs.getString("title"),
                        rs.getDate("event_date"),
                        rs.getInt("registered"),
                        rs.getInt("max_attendees"));
            }
        }
    }

    private void reportsMenu() {
        while (true) {
            System.out.println("\n-- Reports / Queries --");
            System.out.println("1. Adoption applications for a given pet");
            System.out.println("2. Visit history for a customer");
            System.out.println("3. Upcoming events with capacity");
            System.out.println("4. Top N members by revenue");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            String c = in.nextLine().trim();
            try {
                switch (c) {
                    case "1": reportAdoptionApplicationsForPet(); break;
                    case "2": reportVisitHistory(); break;
                    case "3": reportUpcomingEventsWithCapacity(); break;
                    case "4": reportTopMembers(); break;
                    case "0": return;
                    default: System.out.println("Invalid choice."); break;
                }
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void reportAdoptionApplicationsForPet() throws SQLException {
        System.out.print("Pet id: ");
        int petId = Integer.parseInt(in.nextLine());
        String sql =
                "SELECT m.name, a.submitted_date, a.status, s.name FROM Adoption_Application a JOIN Member m ON a.member_id = m.member_id LEFT JOIN Staff s ON a.reviewed_by = s.staff_id WHERE a.pet_id = ? ORDER BY a.submitted_date";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, petId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("Applicant | Date | Status | Coordinator");
                while (rs.next()) {
                    System.out.printf("%s | %s | %s | %s%n",
                            rs.getString(1),
                            rs.getDate(2),
                            rs.getString(3),
                            rs.getString(4));
                }
            }
        }
    }

    private void reportVisitHistory() throws SQLException {
        System.out.print("Member id: ");
        int memberId = Integer.parseInt(in.nextLine());
        String sql =
                "SELECT r.reservation_id, r.reservation_date, r.start_time, rm.room_name, mt.tier_name, NVL(SUM(co.total_price),0) FROM Reservation r JOIN Room rm ON r.room_id = rm.room_id LEFT JOIN Membership_Tier mt ON r.tier_id = mt.tier_id LEFT JOIN Customer_Order co ON co.reservation_id = r.reservation_id WHERE r.member_id = ? GROUP BY r.reservation_id, r.reservation_date, r.start_time, rm.room_name, mt.tier_name ORDER BY r.reservation_date, r.start_time";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("ResID | Date | Start | Room | Tier | Spent");
                while (rs.next()) {
                    System.out.printf("%d | %s | %s | %s | %s | %.2f%n",
                            rs.getInt(1),
                            rs.getDate(2),
                            rs.getTimestamp(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getDouble(6));
                }
            }
        }
    }

    private void reportUpcomingEventsWithCapacity() throws SQLException {
        String sql =
                "SELECT e.event_id, e.title, e.event_date, e.start_time, r.room_name, e.max_attendees, COUNT(er.member_id), s.name FROM Event e JOIN Room r ON e.room_id = r.room_id JOIN Staff s ON e.staff_id = s.staff_id LEFT JOIN Event_Registration er ON e.event_id = er.event_id AND er.attendance_status IN ('REGISTERED','ATTENDED') WHERE e.event_date >= TRUNC(SYSDATE) GROUP BY e.event_id, e.title, e.event_date, e.start_time, r.room_name, e.max_attendees, s.name HAVING COUNT(er.member_id) < e.max_attendees ORDER BY e.event_date, e.start_time";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("ID | Title | Date | Room | Registered/Capacity | Coordinator");
            while (rs.next()) {
                System.out.printf("%d | %s | %s | %s | %d/%d | %s%n",
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getDate(3),
                        rs.getString(5),
                        rs.getInt(7),
                        rs.getInt(6),
                        rs.getString(8));
            }
        }
    }

    private void reportTopMembers() throws SQLException {
        System.out.print("Minimum total spend: ");
        double min = Double.parseDouble(in.nextLine());
        System.out.print("Top N: ");
        int topN = Integer.parseInt(in.nextLine());

        String sql =
                "SELECT * FROM (SELECT m.member_id, m.name, COUNT(DISTINCT r.reservation_id), NVL(SUM(co.total_price),0) FROM Member m LEFT JOIN Reservation r ON r.member_id = m.member_id LEFT JOIN Customer_Order co ON co.member_id = m.member_id GROUP BY m.member_id, m.name HAVING NVL(SUM(co.total_price),0) >= ? ORDER BY NVL(SUM(co.total_price),0) DESC) WHERE ROWNUM <= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, min);
            ps.setInt(2, topN);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("MemberID | Name | Visits | TotalSpent");
                while (rs.next()) {
                    System.out.printf("%d | %s | %d | %.2f%n",
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getInt(3),
                            rs.getDouble(4));
                }
            }
        }
    }
}
