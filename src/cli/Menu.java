package cli;

import java.util.Scanner;

public class Menu {
    public static Integer showOptions(Scanner scan, String[] options) {
        boolean loop = true;
        do {
            System.out.println("Choose an option (type one number):");
            for (int i = 0; i < options.length; i++) {
                System.out.println("(" + (i + 1) + ") " + options[i]);
            }
            Integer option = scan.nextInt();
            if (option >= 1 && option <= options.length) {
                return option - 1;
            }
            System.out.println("Enter a valid option");
        } while (loop);
        return 0;
    }

}
