import java.util.Scanner;

import application.Auth;
import cli.CLI;

public class App {

    public static Scanner scanner = new Scanner(System.in);
    public static CLI cli = new CLI(scanner);

    public static void main(String[] args) throws Exception {
        cli.clear();
        System.out.println("----- Easy Daily Review :::: author: @cpt-westphalen -----");
        System.out.println("Press 'Enter' to start.");
        scanner.nextLine();

        while (true) {
            try {
                if (Auth.isAuthorized()) {
                    cli.mainMenu();
                } else {
                    cli.authMenu();
                }
            } catch (Exception e) {
                scanner.nextLine();
                System.out.println("Ops, something went wrong. Let's try it again, shall we? ('Enter' to return)");
                scanner.nextLine();
            }
        }

        // TODO Create own template review
        // // create template question
        // // add or remove question from review template

    }
}
