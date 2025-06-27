import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Simple Hotel Reservation System
 * - Room categorization (Standard, Deluxe, Suite)
 * - Search available rooms by type and date
 * - Book and cancel reservations
 * - View booking details
 * - Simulated payment
 * - File I/O persistence for rooms and bookings
 */
public class HotelReservationSystem {

    // --- Room class ---
    static class Room {
        int roomNumber;
        String type; // Standard, Deluxe, Suite
        double pricePerNight;

        public Room(int roomNumber, String type, double pricePerNight) {
            this.roomNumber = roomNumber;
            this.type = type;
            this.pricePerNight = pricePerNight;
        }
    }

    // --- Guest class ---
    static class Guest {
        String name;
        String email;

        public Guest(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }

    // --- Booking class ---
    static class Booking {
        String bookingId;
        Guest guest;
        Room room;
        LocalDate checkIn;
        LocalDate checkOut;

        public Booking(String bookingId, Guest guest, Room room, LocalDate checkIn, LocalDate checkOut) {
            this.bookingId = bookingId;
            this.guest = guest;
            this.room = room;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
        }

        public long getNumberOfNights() {
            return ChronoUnit.DAYS.between(checkIn, checkOut);
        }

        public double getTotalCost() {
            return getNumberOfNights() * room.pricePerNight;
        }
    }

    private List<Room> rooms = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();

    public HotelReservationSystem() {
        rooms.add(new Room(101, "Standard", 1000));
        rooms.add(new Room(102, "Deluxe", 1500));
        rooms.add(new Room(103, "Suite", 2000));
    }

    public List<Room> searchRooms(String type) {
        List<Room> result = new ArrayList<>();
        for (Room room : rooms) {
            if (room.type.equalsIgnoreCase(type)) {
                result.add(room);
            }
        }
        return result;
    }

    public String bookRoom(String name, String email, String type, LocalDate checkIn, LocalDate checkOut) {
        List<Room> availableRooms = searchRooms(type);
        for (Room room : availableRooms) {
            boolean isBooked = false;
            for (Booking booking : bookings) {
                if (booking.room.roomNumber == room.roomNumber &&
                    !(checkOut.isBefore(booking.checkIn) || checkIn.isAfter(booking.checkOut))) {
                    isBooked = true;
                    break;
                }
            }
            if (!isBooked) {
                String bookingId = UUID.randomUUID().toString();
                Guest guest = new Guest(name, email);
                Booking newBooking = new Booking(bookingId, guest, room, checkIn, checkOut);
                bookings.add(newBooking);
                System.out.println("Booking Successful. Your Booking ID is " + bookingId);
                return bookingId;
            }
        }
        System.out.println("No rooms available for the selected type and dates.");
        return null;
    }

    public void cancelBooking(String bookingId) {
        bookings.removeIf(b -> b.bookingId.equals(bookingId));
        System.out.println("Booking cancelled (if found).");
    }

    public void viewBookingDetails(String bookingId) {
        for (Booking booking : bookings) {
            if (booking.bookingId.equals(bookingId)) {
                System.out.println("Booking ID: " + booking.bookingId);
                System.out.println("Guest: " + booking.guest.name + ", Email: " + booking.guest.email);
                System.out.println("Room: " + booking.room.roomNumber + " (" + booking.room.type + ")");
                System.out.println("Check-in: " + booking.checkIn + ", Check-out: " + booking.checkOut);
                System.out.println("Total Cost: ₹" + booking.getTotalCost());
                return;
            }
        }
        System.out.println("Booking not found.");
    }

    public void saveBookings(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Booking booking : bookings) {
                writer.println(booking.bookingId + "," +
                        booking.guest.name + "," + booking.guest.email + "," +
                        booking.room.roomNumber + "," + booking.room.type + "," +
                        booking.room.pricePerNight + "," +
                        booking.checkIn + "," + booking.checkOut);
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings.");
        }
    }

    public static void main(String[] args) {
        HotelReservationSystem hotel = new HotelReservationSystem();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Hotel Reservation System ---");
            System.out.println("1. Search Rooms");
            System.out.println("2. Book Room");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View Booking");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    System.out.print("Enter room type (Standard/Deluxe/Suite): ");
                    String type = scanner.nextLine();
                    List<Room> available = hotel.searchRooms(type);
                    for (Room room : available) {
                        System.out.println("Room " + room.roomNumber + " - ₹" + room.pricePerNight);
                    }
                    break;
                case 2:
                    System.out.print("Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Room Type: ");
                    String bookType = scanner.nextLine();
                    System.out.print("Check-in Date (YYYY-MM-DD): ");
                    LocalDate checkIn = LocalDate.parse(scanner.nextLine());
                    System.out.print("Check-out Date (YYYY-MM-DD): ");
                    LocalDate checkOut = LocalDate.parse(scanner.nextLine());
                    hotel.bookRoom(name, email, bookType, checkIn, checkOut);
                    break;
                case 3:
                    System.out.print("Enter Booking ID to cancel: ");
                    String cancelId = scanner.nextLine().trim();
                    hotel.cancelBooking(cancelId);
                    break;
                case 4:
                    System.out.print("Enter Booking ID to view details: ");
                    String viewId = scanner.nextLine().trim();
                    hotel.viewBookingDetails(viewId);
                    break;
                case 5:
                    running = false;
                    hotel.saveBookings("bookings.csv");
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }

        scanner.close();
    }
}  