
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.Scanner;

import application.Auth;
import cli.CLI;

public class App {

    public static Scanner scanner = new Scanner(System.in);
    public static CLI cli = new CLI(scanner);

    public static void main(String[] args) throws Exception {
        cli.clear();
        System.out.println("//----- Easy Daily Review ----- author: @cpt-westphalen -----//");
        System.out.println();

        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(new File("cli-logs.txt")))) {
            logWriter.write("App initialized :: " + LocalDateTime.now());
            logWriter.newLine();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        while (!Auth.isAuthorized()) {
            cli.authMenu();
            while (Auth.isAuthorized()) {
                cli.mainMenu();
            }
        }

        // TODO Text file DB for written reviews.
        // TODO Review searching
        // TODO Create own template review

    }
}
