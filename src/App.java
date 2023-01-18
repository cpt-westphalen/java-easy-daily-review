
import java.util.Scanner;

import cli.CLI;

public class App {

    public static Scanner scanner = new Scanner(System.in);
    public static CLI cli = new CLI(scanner);

    public static void main(String[] args) throws Exception {
        System.out.println("Programa iniciado.");

        // // // registrar usuário
        // cli.registerUser();

        // // // login
        // cli.userLogin();

        // // menu principal
        cli.mainMenu();

        cli.registerNewReview();
    }
}
