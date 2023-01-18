package cli;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import application.Auth;
import application.entities.Answer;
import application.entities.Question;
import application.entities.Review;
import application.entities.User;
import application.useCases.RegisterNewReview;

public class CLI {

    public Scanner scan;

    public CLI(Scanner scanner) {
        this.scan = scanner;
    }

    public void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void userLogin() throws Exception {
        String name;
        String pin;
        System.out.println("Please, login.");
        System.out.println("Name: ");
        name = this.scan.nextLine();
        System.out.println("Pin: ");
        pin = this.scan.nextLine();
        this.clear();
        User user = CliModule.userRepository.findByName(name);
        if (user == null) {
            throw new Exception("User not found.");
        }
        if (!Auth.login(user, Integer.valueOf(pin))) {
            throw new Exception("Incorrect Pin.");
        }

        System.out.println("Login was successfull!");
    }

    public void registerUser() throws Exception {
        System.out.println("Please, sign up: ");
        String login, pin;

        System.out.println("Name: ");
        login = this.scan.nextLine();
        System.out.println("Pin (numbers only): ");
        pin = this.scan.nextLine();

        this.clear();

        String id = UUID.randomUUID().toString();
        User user = new User(id, login, Integer.valueOf(pin));
        CliModule.userRepository.add(user);

        List<User> users = CliModule.userRepository.getAll();

        System.out.println("User list: ");
        for (User u : users) {
            System.out.println("- " + u.getName());
        }
    }

    public void mainMenu() {
        String[] options = new String[] { "New daily review", "Check previous reviews", "Settings" };
        Integer selectedOption = Menu.main(scan, options);
        switch (selectedOption) {
            case 0:
                // new daily review use-case
                scan.nextLine();
                registerNewReview();
                // check selected template
                // make new review object
                // display options and input answers
                // save review to repository
                // display success message
                break;

            default:
                break;
        }

    }

    public void registerNewReview() {
        RegisterNewReview registerNewReview = new RegisterNewReview(CliModule.reviewRepository);
        Review template = registerNewReview.getTemplateDailyReview();
        System.out.println("You are using the default daily review template.");
        List<Question> questions = new LinkedList<Question>();
        for (Question templateQuestion : template.getQuestions()) {
            Question question = new Question(templateQuestion.getId(), templateQuestion.getType(),
                    templateQuestion.getText());
            Answer answer = question.getAnswer();
            System.out.println(question.getText());
            String answerText = scan.nextLine();
            if (!answerText.isEmpty()) {
                answer.setValue(answerText);
                question.setAnswer(answer);
            }
            questions.add(question);
        }
        // create the new review from the questions and answers
        Review review = new Review(UUID.randomUUID().toString(), template.getPeriod(), LocalDateTime.now(), questions);

        // set overall review rate, which should always be the last question.
        Integer rate = questions.get((questions.size() - 1)).getAnswer().getValueAsInteger();
        review.setRate(rate);

        registerNewReview.saveToRepository(review);
        System.out.println("Completed! Would you like to see your answers? ('y' or 'n')");
        if (scan.nextLine().charAt(0) == 'n') {
            return;
        }
        printReview(review);

    }

    public void printReview(Review review) {
        System.out.println("------ Review :: " + review.getDate() + " ------");
        System.out.println("Periodicity: " + review.getPeriod().name());
        System.out.println("Overall rating: " + review.getRate());
        System.out.println("\n");
        System.out.println("-- Answers --");
        for (Question question : review.getQuestions()) {
            System.out.println(question.getText());
            System.out.println("R: " + question.getAnswer().getValue());
        }
        System.out.println("\n");
        System.out.println("------------");
    }
}
