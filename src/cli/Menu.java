package cli;

import java.util.Scanner;

public class Menu {
    public static Integer showOptions(Scanner scan, String[] options) {
        int attempts = 0;
        System.out.println("Choose an option (type one number):");
        for (int i = 0; i < options.length; i++) {
            System.out.println("(" + (i + 1) + ") " + options[i]);
        }
        System.out.println("(0) Go back");
        while (true) {
            try {
                Integer option = scan.nextInt();
                if (option == 0)
                    return null;
                if (option >= 1 && option <= options.length) {
                    return option - 1;
                }
            } catch (Exception e) {
                if (++attempts == 3) {
                    return null;
                }
                scan.nextLine();
                System.out.println("* Enter a valid option *");
            }
        }
    }

}
