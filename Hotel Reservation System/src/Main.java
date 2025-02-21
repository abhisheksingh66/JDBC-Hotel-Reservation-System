import java.sql.*;
import java.util.Scanner;

class HotelReservationSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "@Anshulsingh66";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}

public class Main {
    public static void reserveRoom(Connection con, Scanner scanner) {
        try {
            System.out.println("Enter guest name:");
            scanner.nextLine(); // Consume newline
            String guestName = scanner.nextLine();
            System.out.println("Enter room number:");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter contact number:");
            scanner.nextLine();
            String contactNumber = scanner.nextLine();

            String sql = "INSERT INTO reservation (guest_name, room_number, contact_number, reservation_date) VALUES (?, ?, ?, NOW())";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, guestName);
                stmt.setInt(2, roomNumber);
                stmt.setString(3, contactNumber);

                int affectedRows = stmt.executeUpdate();
                System.out.println(affectedRows > 0 ? "Reservation successful" : "Reservation failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewReservations(Connection con) throws SQLException {
        String sql = "SELECT * FROM reservation";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("+----+--------------+------+--------------+---------------------+");
            System.out.println("| ID | Guest Name   | Room | Contact No.  | Reservation Date   |");
            System.out.println("+----+--------------+------+--------------+---------------------+");
            while (rs.next()) {
                System.out.printf("| %-2d | %-12s | %-4d | %-12s | %-19s |\n",
                        rs.getInt("reservation_id"),
                        rs.getString("guest_name"),
                        rs.getInt("room_number"),
                        rs.getString("contact_number"),
                        rs.getString("reservation_date"));
            }
            System.out.println("+----+--------------+------+--------------+---------------------+");
        }
    }

    public static void getRoomNumber(Connection con, Scanner scanner) {
        try {
            System.out.println("Enter reservation ID:");
            int reservationId = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Enter guest name:");
            String guestName = scanner.nextLine();

            String sql = "SELECT room_number FROM reservation WHERE reservation_id = ? AND guest_name = ?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setInt(1, reservationId);
                stmt.setString(2, guestName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int roomNumber = rs.getInt("room_number");
                    System.out.println("Room number for reservation ID " + reservationId + " is: " + roomNumber);
                } else {
                    System.out.println("No reservation found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateReservation(Connection con, Scanner scanner) {
        try {
            System.out.println("Enter reservation ID to update:");
            int reservationId = scanner.nextInt();
            scanner.nextLine();

            if (!reservationExists(con, reservationId)) {
                System.out.println("Reservation not found!");
                return;
            }

            System.out.println("Enter new guest name:");
            String newGuestName = scanner.nextLine();
            System.out.println("Enter new room number:");
            int newRoomNumber = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Enter new contact number:");
            String newContactNumber = scanner.nextLine();

            String sql = "UPDATE reservation SET guest_name = ?, room_number = ?, contact_number = ? WHERE reservation_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, newGuestName);
                stmt.setInt(2, newRoomNumber);
                stmt.setString(3, newContactNumber);
                stmt.setInt(4, reservationId);

                int rowsUpdated = stmt.executeUpdate();
                System.out.println(rowsUpdated > 0 ? "Reservation updated successfully." : "Update failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteReservation(Connection con, Scanner scanner) {
        try {
            System.out.println("Enter reservation ID to delete:");
            int reservationId = scanner.nextInt();

            if (!reservationExists(con, reservationId)) {
                System.out.println("Reservation not found!");
                return;
            }

            String sql = "DELETE FROM reservation WHERE reservation_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setInt(1, reservationId);
                int affectedRows = stmt.executeUpdate();
                System.out.println(affectedRows > 0 ? "Reservation deleted." : "Deletion failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean reservationExists(Connection con, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservation WHERE reservation_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setInt(1, reservationId);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException {
        System.out.println("Exiting system...");
        for (int i = 5; i > 0; i--) {
            System.out.print(".");
            Thread.sleep(450);
        }
        System.out.println("\nThank you for using the Hotel Reservation System!");
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = HotelReservationSystem.getConnection();
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nHotel Management System");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> reserveRoom(con, scanner);
                    case 2 -> viewReservations(con);
                    case 3 -> getRoomNumber(con, scanner);
                    case 4 -> updateReservation(con, scanner);
                    case 5 -> deleteReservation(con, scanner);
                    case 0 -> {
                        exit();
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
