import java.util.Scanner;

import application.Auth;
import cli.CLI;

public class App {

    public static Scanner scanner = new Scanner(System.in);
    public static CLI cli = new CLI(scanner);

    public static void main(String[] args) throws Exception {
        while (true) {
            cli.clear();
            System.out.println("----- Easy Daily Review :::: author: @cpt-westphalen -----");
            System.out.println("Press 'Enter' to start.");
            scanner.nextLine();

            try {
                while (true) {
                    if (Auth.isAuthorized()) {
                        cli.mainMenu();
                        System.out.println("Do you wish to exit Easy Daily Review? ('y' / 'n')");
                        if (scanner.nextLine().toLowerCase().startsWith("y"))
                            Auth.logout();
                    } else {
                        cli.authMenu();
                        if (Auth.isAuthorized() == false) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                scanner.nextLine();
                System.out.println("Ops, something went wrong. Let's try it again, shall we? ('Enter' to return)");
                scanner.nextLine();
            }
        }

        // TODO View week rates: Highest, lowest, average
        // TODO Compare answers from different reviews

    }
}
