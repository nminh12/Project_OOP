import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== WELCOME TO LIBRARY ===");
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Enter your username: ");
            String username = sc.nextLine().trim();
            System.out.print("Enter your password: ");
            String password = sc.nextLine();

            try (Scanner fileScanner = new Scanner(new File("users.txt"))) {
                boolean found = false;
                while (fileScanner.hasNextLine()) {
                    String[] parts = fileScanner.nextLine().split(",");
                    if (parts.length >= 3 && parts[0].equals(username) && parts[1].equals(password)) {
                        found = true;
                        User user;
                        switch (parts[2]) {
                            case "Admin":
                                user = new Admin(username, password);
                                break;
                            case "Librarian":
                                user = new Librarian(username, password);
                                break;
                            case "Reader":
                                user = new Reader(username, password);  
                                break;
                            default:
                                System.out.println("Unknown role.");
                                return;
                        }
                        user.showMenu(sc);
                        return; 
                    }
                }

                if (!found) {
                    System.out.println("Invalid credentials. Please try again.");
                    System.out.print("Do you want to try again? (y/n): ");
                    String choice = sc.nextLine().trim().toLowerCase();
                    if (!choice.equals("y")) {
                        System.out.println("Goodbye.");
                        break;
                    }
                }

            } catch (IOException e) {
                System.out.println("Login error: " + e.getMessage());
                break;
            }
        }

        sc.close();
    }
}
