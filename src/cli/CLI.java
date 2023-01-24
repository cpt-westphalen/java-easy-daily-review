package cli;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import application.Auth;
import application.entities.Answer;
import application.entities.Question;
import application.entities.Review;
import application.entities.TemplateQuestion;
import application.entities.TemplateReview;
import application.entities.User;
import application.useCases.GetReviews;
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

    public void authMenu() {
        Integer option = Menu.showOptions(scan, new String[] { "Register new user", "Login" });
        switch (option) {
            case 0:
                scan.nextLine();
                this.registerUser();
                break;
            case 1:
                scan.nextLine();
                this.loginUser();
                break;
        }
    }

    private void loginUser() {
        while (!Auth.isAuthorized()) {
            this.clear();
            String name, pin;
            System.out.println("Please, login.");
            System.out.println("Name: ");
            name = this.scan.nextLine();
            System.out.println("Pin: ");
            pin = this.scan.nextLine();
            this.clear();
            User user = CliModule.userRepository.findByName(name);
            if (user == null) {
                System.out.println("*Error: User not found*");
                this.authMenu();
            }
            if (!Auth.login(user, Integer.valueOf(pin))) {
                System.out.println("*Error: Incorrect Pin*");
                this.authMenu();
            }
        }
        System.out.println("Login was successfull!");
    }

    private void registerUser() {
        this.clear();
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
        while (true) {
            this.clear();
            LocalDate today = LocalDate.now();
            String name = Auth.getLoggedUser().getName();
            System.out.println("------------- " + today + " :: " + name);

            Integer selectedOption = Menu.showOptions(scan, options);

            switch (selectedOption) {
                case 0:
                    // new daily review use-case
                    scan.nextLine();
                    clear();
                    registerNewReview();
                    break;

                case 1:
                    // check previous reviews
                    scan.nextLine();
                    clear();
                    getPreviousReviews();
                    break;

                default:
                    break;
            }
            System.out.println("Do you wish to exit Easy Daily Review? ('y' or 'n')");

            if (scan.nextLine().startsWith("y")) {
                break;
            }
        }
    }

    public void registerNewReview() {
        this.clear();
        RegisterNewReview registerNewReview = new RegisterNewReview(CliModule.reviewRepository);
        TemplateReview template = registerNewReview.getTemplateReviewFrom("src/templates", "daily-review-template.txt");
        System.out.println("You are using the default daily review template.");
        System.out.println("----- " + LocalDateTime.now().toLocalDate() + " -----");
        List<Question> questions = new LinkedList<Question>();
        for (TemplateQuestion templateQuestion : template.getTemplateQuestions()) {
            Question question = new Question(templateQuestion);
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
        Review review = new Review(Auth.getLoggedUser().getId(), UUID.randomUUID().toString(), template.getPeriod(),
                LocalDateTime.now(), questions);

        // set default rates by querying the question id
        Integer dayRate = review.getQuestionById(CliModule.templateQuestionRepository.getDayRateQuestion().getId())
                .getAnswer().getValueAsInteger();
        Integer wellbeingRate = review
                .getQuestionById(CliModule.templateQuestionRepository.getWellbeingRateQuestion().getId())
                .getAnswer().getValueAsInteger();
        Integer productivityRate = review
                .getQuestionById(CliModule.templateQuestionRepository.getProductivityRateQuestion().getId())
                .getAnswer().getValueAsInteger();

        review.setDayRate(dayRate);
        review.setWellbeingRate(wellbeingRate);
        review.setProductivityRate(productivityRate);

        registerNewReview.saveToRepository(review);
        System.out.println("Completed! Would you like to see your answers? ('y' or 'n')");
        if (scan.nextLine().charAt(0) == 'n') {
            return;
        }
        printReview(review);

    }

    public void getPreviousReviews() {
        this.clear();
        GetReviews getReviews = new GetReviews(CliModule.reviewRepository);
        List<Review> userReviews;
        System.out.println("----- User Reviews -----");

        try {
            userReviews = getReviews.listAllFromLoggedUser();

            if (userReviews.size() == 0) {
                System.out.println("* No Reviews Yet! *");
                System.out.println("(Press 'Enter' to return)");
                scan.nextLine();
                return;
            }
            for (int i = 0; i < userReviews.size(); i++) {
                Review review = userReviews.get(i);
                System.out.println(
                        "(" + (i + 1) + ") " + review.getDate().toLocalDate() + " - Day rating: "
                                + review.getDayRate());
            }

            System.out.println("Enter the number to display the review, or 'q' to return");
            Integer option = scan.nextInt();
            scan.nextLine();
            Review selectedReview = userReviews.get(option - 1);
            System.out.println("--- Selected review: " + selectedReview.getDate().toLocalDate() + " ---");
            printReview(selectedReview);

        } catch (Exception e) {
            System.out.println("You need to login first.");
            return;
        }
    }

    public void printReview(Review review) {
        System.out.println("------ Review :: " + review.getDate() + " ------");
        System.out.println("Periodicity: " + review.getPeriod().name());
        System.out.println("Well-being rate: " + review.getWellbeingRate());
        System.out.println("Productivity rate: " + review.getProductivityRate());
        System.out.println("Overall day rate: " + review.getDayRate());
        System.out.println("-- Answers --");
        List<Question> questions = review.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            System.out.println((i + 1) + ") " + questions.get(i).getText());
            System.out.println("R: " + questions.get(i).getAnswer().getValue());
        }
        System.out.println("------------");
    }
}
