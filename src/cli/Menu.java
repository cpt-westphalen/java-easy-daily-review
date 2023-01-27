package cli;

import java.util.Scanner;

public class Menu {
    public static Integer showOptions(Scanner scan, String[] options) {
        int attempts = 0;
        while (true) {
            try {
                System.out.println("Choose an option (type one number):");
                for (int i = 0; i < options.length; i++) {
                    System.out.println("(" + (i + 1) + ") " + options[i]);
                }
                System.out.println("(" + options.length + 1 + ") Go back");
                Integer option = scan.nextInt();
                if (option == options.length + 1)
                    return null;
                if (option >= 1 && option <= options.length) {
                    return option - 1;
                }
            } catch (Exception e) {
                if (++attempts == 3)
                    return null;
                System.out.println("* Enter a valid option *");
            }
        }
    }

}
