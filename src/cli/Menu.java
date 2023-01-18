package cli;

import java.time.LocalDate;
import java.util.Scanner;

public class Menu {
    public static Integer showOptions(Scanner scan, String[] options) {
        boolean loop = true;
        do {
            System.out.println("Choose an option (type one number):");
            for (int i = 0; i < options.length; i++) {
                System.out.println("(" + i + ") " + options[i]);
            }
            Integer option = scan.nextInt();
            if (option >= 0 && option < options.length) {
                return option;
            }
            System.out.println("Enter a valid option");
        } while (loop);
        return 0;
    }

    public static Integer main(Scanner scan, String[] options) {

        LocalDate today = LocalDate.now();
        System.out.println("------------- " + today);

        Integer selectedOption = showOptions(scan, options);

        return selectedOption;
    }

}
