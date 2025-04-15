import java.util.*;
import java.io.*;


abstract class User { 
    private String username; 
    private String password; 
    private String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String getRole() {
        return this.role;
    }
    
    public abstract void showMenu(Scanner sc);
}

class Admin extends User {
    public Admin(String username, String password) {
        super(username, password, "Admin");
    }

    @Override
    public void showMenu(Scanner sc) {
        int choice;
        do {
            System.out.println("=======[Admin Menu]=======");
            System.out.println("1. Add User");
            System.out.println("2. Remove User");
            System.out.println("3. List Users");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            switch (choice) {
                case 1:
                    addUser(sc);
                    break;
                case 2:
                    removeUser(sc);
                    break;
                case 3:
                    listUsers();
                    break;
                case 4:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
        while (choice != 4);
    }

    private boolean isUsernameExists(String username) {
        try (Scanner fileScanner = new Scanner(new File("users.txt"))) {
            while (fileScanner.hasNextLine()) {
                String[] parts = fileScanner.nextLine().split(",");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            return false;
        }
        return false;
    }

    private void addUser(Scanner sc) {
        sc.nextLine(); // Clear buffer if needed
    
        String username;
        while (true) {
            System.out.print("Enter username: ");
            username = sc.nextLine().trim();
    
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty.");
            } else if (isUsernameExists(username)) {
                System.out.println("Username already exists. Please choose another.");
            } else {
                break;
            }
        }
    
        String password;
        while (true) {
            System.out.print("Enter password: ");
            password = sc.nextLine().trim();
    
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty.");
            } else {
                break;
            }
        }
    
        String role;
        while (true) {
            System.out.print("Enter role (Admin/Librarian/Reader): ");
            role = sc.nextLine().trim();
    
            if (role.isEmpty()) {
                System.out.println("Role cannot be empty.");
            } else if (!role.equals("Admin") && !role.equals("Librarian") && !role.equals("Reader")) {
                System.out.println("Invalid role. Must be Admin, Librarian, or Reader.");
            } else {
                break;
            }
        }
    
        try {
            FileWriter fw = new FileWriter("users.txt", true);
            fw.write(username + "," + password + "," + role + "\n");
            fw.close();
            System.out.println("New user [" + username + "] has been added.");
        } catch (IOException e) {
            System.out.println("Failed to add user: " + e.getMessage());
        }
    }

    private void removeUser(Scanner sc) {
        sc.nextLine(); // Consume newline left-over
        System.out.print("Enter username to remove: ");
        String username = sc.nextLine().trim();
    
        File userFile = new File("users.txt");
        ArrayList<String> updatedFile = new ArrayList<>();
        boolean found = false;
    
        try {
            Scanner rk = new Scanner(userFile);
            while (rk.hasNextLine()) {
                String line = rk.nextLine();
                String[] data = line.split(",");
                if (data.length >= 1 && data[0].equals(username)) {
                    found = true; 
                    continue;
                }
                updatedFile.add(line);
            }
            rk.close();
    
            if (found) {
                FileWriter wr = new FileWriter(userFile, false);
                for (String line : updatedFile) {
                    wr.write(line + "\n");
                }
                wr.close();
                System.out.println("User [" + username + "] removed successfully.");
            } else {
                System.out.println("Cannot find user [" + username + "] in the file.");
            }
    
        } catch (IOException e) {
            System.out.println("Error occurred while processing file.");
        }
    }

    private void listUsers() {
        try {
            File userFile = new File("users.txt");
            Scanner sc = new Scanner(userFile);
            System.out.println("=======[User List]=======");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] data = line.split(",");
                System.out.println("Username: " + data[0] + ", Role: " + data[2]);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error occurred while reading file.");
        }
    }
}

class LibraryUtils {
    public static List<Book> readBooksFromFile(String filename) {
        List<Book> books = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 7) continue;

                String type = parts[0];
                String title = parts[1];
                String author = parts[2];
                String genre = parts[3];
                String isbn = parts[4];
                boolean isAvailable = Boolean.parseBoolean(parts[5]);
                String dueDate = parts[6];

                if (type.equalsIgnoreCase("Printed") && parts.length == 8) {
                    String pages = parts[7];
                    books.add(new PrintedBook(title, author, genre, isbn, isAvailable, dueDate, pages));
                } else if (type.equalsIgnoreCase("Ebook") && parts.length == 8) {
                    String fileFormat = parts[7];
                    books.add(new Ebook(title, author, genre, isbn, fileFormat));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }

        return books;
    }

    public static void writeBooksToFile(String filename, List<Book> books) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Book book : books) {
                writer.write(book.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void addBook(List<Book> books, Book newBook) {
        books.add(newBook);
    }

    public static void removeBookByTitle(List<Book> books, String title) {
        books.removeIf(book -> book.getTitle().equalsIgnoreCase(title));
    }

    public static void listAvailableBooks(List<Book> books) {
        for (Book book : books) {
            if (book.isAvailable()) {
                System.out.println(book.getType() + ": " + book.getTitle() + " by " + book.author);
            }
        }
    }
}

class Librarian extends User {
    private List<Book> books;
    private final String filename = "books.txt";

    public Librarian(String username, String password) {
        super(username, password, "Librarian");
        this.books = LibraryUtils.readBooksFromFile(filename);
    }

    @Override
    public void showMenu(Scanner sc) {
        int choice;
        do {
            System.out.println("=======[Librarian Menu]=======");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. List Available Books");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addBook(sc);
                    break;
                case 2:
                    removeBook(sc);
                    break;
                case 3:
                    listBooks();
                    break;
                case 4:
                    LibraryUtils.writeBooksToFile(filename, books);
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 4);
    }

    private void addBook(Scanner sc) {
        System.out.println("Enter type of book (1. Printed Book, 2. EBook): ");
        String choice = sc.nextLine();
    
        if (!choice.equals("1") && !choice.equals("2")) {
            System.out.println("Invalid book type selection. Cancelling add operation.");
            return;
        }
    
        System.out.print("Title: ");
        String title = sc.nextLine();
        System.out.print("Author: ");
        String author = sc.nextLine();
        System.out.print("Genre: ");
        String genre = sc.nextLine();
        System.out.print("ISBN: ");
        String isbn = sc.nextLine();
    
        Book newBook = null;
    
        if (choice.equals("1")) {
            System.out.print("Pages: ");
            String pages = sc.nextLine();
            newBook = new PrintedBook(title, author, genre, isbn, true, "N/A", pages);
        } else {
            System.out.print("File format: ");
            String fileFormat = sc.nextLine();
            newBook = new Ebook(title, author, genre, isbn, fileFormat);
        }
    
        LibraryUtils.addBook(books, newBook);
        LibraryUtils.writeBooksToFile(filename, books);
    
        System.out.println("New book [" + title + "] has been added! [Type: " + newBook.getType() + "]");
    }

    private void removeBook(Scanner sc) {
        System.out.print("Enter title of the book to remove: ");
        String title = sc.nextLine();
    
        boolean isRemoved = false;
        Iterator<Book> iterator = books.iterator();
        while (iterator.hasNext()) {
            Book book = iterator.next();
            if (book.getTitle().equalsIgnoreCase(title)) {
                iterator.remove();
                isRemoved = true;
                break; 
            }
        }
    
        if (isRemoved) {
            LibraryUtils.writeBooksToFile(filename, books);
            System.out.println("Book '" + title + "' has been removed.");
        } else {
            System.out.println("Cannot find book '" + title + "' in the files.");
        }
    }

    private void listBooks() {
        for (Book book : books) {
            if (book.isAvailable()) {
                String bookInfo = "Title: " + book.getTitle() +
                                  ", Author: " + book.author +
                                  ", Genre: " + book.genre +
                                  ", ISBN: " + book.getIsbn() +
                                  ", Availability: " + (book.isAvailable() ? "Available" : "Not Available") +
                                  ", Due Date: " + book.dueDate;
    
                if (book instanceof PrintedBook) {
                    PrintedBook printedBook = (PrintedBook) book;
                    bookInfo += ", Pages: " + printedBook.getPages();
                }
                else if (book instanceof Ebook) {
                    Ebook ebook = (Ebook) book;
                    bookInfo += ", File Format: " + ebook.getFileFormat();
                }
    
                System.out.println(bookInfo);
            }
        }
    }
}

class Reader extends User {
    public Reader(String username, String password) {
        super(username, password, "Reader");
    }

    @Override
    public void showMenu(Scanner sc) {
        int choice;
        do {
            System.out.println("=======[Reader Menu]=======");
            System.out.println("1. View Books");
            System.out.println("2. Borrow Book");
            System.out.println("3. Return Book");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            switch (choice) {
                case 1:
                    viewBooks();
                    break;
                case 2:
                    borrowBook(sc);
                    break;
                case 3:
                    returnBook(sc);
                    break;
                case 4:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 4);
    }

    public void viewBooks() {
        try {
            File bookFile = new File("books.txt");
            Scanner sc = new Scanner(bookFile);
            System.out.println("=======[Available Books]=======");
    
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] data = line.split(",");
    
                if (data[0].equalsIgnoreCase("Printed") && data[5].equals("true")) {
                    System.out.println("Title: " + data[1] + ", Author: " + data[2] + ", Genre: " + data[3] +
                                       ", ISBN: " + data[4] + ", Pages: " + data[7] + ", Availability: Available, Type: Printed");
    
                } else if (data[0].equalsIgnoreCase("Ebook") && data[5].equals("true")) {
                    System.out.println("Title: " + data[1] + ", Author: " + data[2] + ", Genre: " + data[3] +
                                       ", ISBN: " + data[4] + ", Format: " + data[7] + ", Availability: Available, Type: Ebook");
                }
            }
    
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error occurred while reading file.");
        }
    }
    
    public void borrowBook(Scanner sc) {
        System.out.print("Enter title of book to borrow: ");
        sc.nextLine(); // Consume leftover newline
        String title = sc.nextLine().trim();
    
        File bookFile = new File("books.txt");
        ArrayList<String> updatedFile = new ArrayList<>();
        boolean found = false;
    
        try {
            Scanner rk = new Scanner(bookFile);
            while (rk.hasNextLine()) {
                String line = rk.nextLine();
                String[] data = line.split(",");
    
                if (data[1].equalsIgnoreCase(title)) {
                    found = true;
    
                    if (data[0].equals("EBook")) {
                        System.out.println("You can't borrow EBook! Please choose another book.");
                    } 
                    else if (data[5].equals("true")) {
                        data[5] = "false"; // Mark as borrowed
                        System.out.println("You have borrowed the book [" + title + "] successfully.");
    
                        try (FileWriter tfw = new FileWriter("transactions.txt", true)) {
                            String transaction = getUsername() + " | BORROW | Title: " + data[1] +
                                                 " | ISBN: " + data[4] +
                                                 " | Date: " + java.time.LocalDate.now();
                            tfw.write(transaction + "\n");
                        } catch (IOException e) {
                            System.out.println("Error writing to transactions file.");
                        }
                    } else {
                        System.out.println("Book [" + title + "] is not available for borrowing.");
                    }
                }
    
                updatedFile.add(String.join(",", data));
            }
            rk.close();
    
            if (found) {
                FileWriter wr = new FileWriter(bookFile, false);
                for (String line : updatedFile) {
                    wr.write(line + "\n");
                }
                wr.close();
            } else {
                System.out.println("Cannot find book [" + title + "] in the file.");
            }
    
        } catch (IOException e) {
            System.out.println("Error occurred while processing file.");
        }
    }
    
    public void returnBook(Scanner sc) {
        System.out.print("Enter title of book to return: ");
        sc.nextLine(); // Consume leftover newline
        String title = sc.nextLine().trim();
    
        File bookFile = new File("books.txt");
        File transactionFile = new File("transactions.txt");
        ArrayList<String> updatedBookFile = new ArrayList<>();
        boolean found = false;
        boolean canReturn = false;
        String isbnToReturn = "";
    
        try {
            List<String> borrowLog = new ArrayList<>();
            List<String> returnLog = new ArrayList<>();
    
            if (transactionFile.exists()) {
                Scanner txReader = new Scanner(transactionFile);
                while (txReader.hasNextLine()) {
                    String line = txReader.nextLine();
                    if (line.contains("BORROW") && line.contains(title)) {
                        borrowLog.add(line);
                    } else if (line.contains("RETURN") && line.contains(title)) {
                        returnLog.add(line);
                    }
                }
                txReader.close();
            }
    
            if (borrowLog.size() > returnLog.size()) {
                canReturn = true;
            }
    
            Scanner rk = new Scanner(bookFile);
            while (rk.hasNextLine()) {
                String line = rk.nextLine();
                String[] data = line.split(",");
    
                if (data[1].equalsIgnoreCase(title)) {
                    found = true;
    
                    if (data[0].equals("EBook")) {
                        System.out.println("You can't return EBook! Please choose another book.");
                    } else if (!canReturn) {
                        System.out.println("You have not borrowed this book or already returned it.");
                    } else if (data[5].equalsIgnoreCase("false")) {
                        data[5] = "true"; // Mark as returned
                        isbnToReturn = data[4];
                        System.out.println("You have returned the book [" + title + "] successfully.");
    
                        // Ghi log RETURN vào transactions.txt
                        try (FileWriter tfw = new FileWriter(transactionFile, true)) {
                            String transaction = getUsername() + " | RETURN | Title: " + data[1] +
                                                 " | ISBN: " + isbnToReturn +
                                                 " | Date: " + java.time.LocalDate.now();
                            tfw.write(transaction + "\n");
                        } catch (IOException e) {
                            System.out.println("Error writing to transactions file.");
                        }
                    } else {
                        System.out.println("Book [" + title + "] is not currently borrowed.");
                    }
                }
    
                updatedBookFile.add(String.join(",", data));
            }
            rk.close();
    
            if (found) {
                FileWriter wr = new FileWriter(bookFile, false);
                for (String line : updatedBookFile) {
                    wr.write(line + "\n");
                }
                wr.close();
            } else {
                System.out.println("Cannot find book [" + title + "] in the file.");
            }
    
        } catch (IOException e) {
            System.out.println("Error occurred while processing file.");
        }
    }
}



    
    