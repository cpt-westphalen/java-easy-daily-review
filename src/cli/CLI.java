package cli;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

import application.Auth;
import application.entities.Answer;
import application.entities.Question;
import application.entities.Review;
import application.entities.TemplateQuestion;
import application.entities.TemplateReview;
import application.entities.User;
import application.entities.TemplateQuestion.Type;
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
        Integer option = Menu.showOptions(this.scan, new String[] { "Register new user", "Login" });
        switch (option) {
            case 0:
                this.scan.nextLine();
                this.registerUser();
                break;
            case 1:
                this.scan.nextLine();
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
                System.out.println();
                this.authMenu();
                return;
            }
            if (!Auth.login(user, Integer.valueOf(pin))) {
                System.out.println("*Error: Incorrect Pin*");
                System.out.println();
                this.authMenu();
                return;
            }
        }
        System.out.println("Login was successfull!");
    }

    private void registerUser() {
        this.clear();
        System.out.println("Please, sign up: ");
        System.out.println("--");
        System.out.println();
        String login, pin;

        System.out.println("Name: ");
        login = this.scan.nextLine();
        System.out.println();
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
        System.out.println();
    }

    public void mainMenu() {
        String[] options = new String[] { "New daily review", "Check previous reviews", "Settings" };
        while (true) {
            this.clear();
            LocalDate today = LocalDate.now();
            String name = Auth.getLoggedUser().getName();
            System.out.println("------------- " + today + " :: " + name);

            Integer selectedOption = Menu.showOptions(this.scan, options);

            switch (selectedOption) {
                case 0:
                    // new daily review use-case
                    this.scan.nextLine();
                    this.clear();
                    this.registerNewReview();
                    break;

                case 1:
                    // check previous reviews
                    this.scan.nextLine();
                    this.clear();
                    this.previousReviewsMenu();
                    break;

                default:
                    break;
            }
            System.out.println("Do you wish to exit Easy Daily Review? ('y' or 'n')");

            if (this.scan.nextLine().startsWith("y")) {
                break;
            }
        }
    }

    private void registerNewReview() {
        this.clear();
        RegisterNewReview registerNewReview = new RegisterNewReview(CliModule.reviewRepository);
        TemplateReview template = registerNewReview.getTemplateReviewFrom("src/templates", "daily-review-template.txt");
        System.out.println("You are using the default daily review template.");
        System.out.println("----- " + LocalDate.now() + " -----");
        System.out.println();
        List<Question> questions = new LinkedList<Question>();
        for (TemplateQuestion templateQuestion : template.getTemplateQuestions()) {
            Question question = new Question(templateQuestion, null, null);
            Answer answer = question.getAnswer();
            System.out.println(question.getText());
            if (question.getType().equals(Type.BOOLEAN)) {
                System.out.println("(Type 'y' or 'n', 'Enter' to submit)");
            }
            if (question.getType().equals(Type.NUMBER)) {
                System.out.println("(Type an integer between 0 and 100, 'Enter' to submit)");
            }
            if (question.getType().equals(Type.TEXT)) {
                System.out.println("(Type any text or leave it blank, 'Enter' to submit)");
            }
            String answerText = this.scan.nextLine();
            System.out.println();
            System.out.println("--");
            if (!answerText.isEmpty()) {
                answer.setValue(answerText);
                question.setAnswer(answer);
            }
            questions.add(question);
        }
        // create the new review from the questions and answers
        Review review = new Review(Auth.getLoggedUser().getId(), UUID.randomUUID().toString(), template.getPeriod(),
                LocalDate.now(), questions);

        // set default rates by querying the question id
        Integer dayRate = review.getQuestionById(
                "36276627-b507-41ff-b9f0-8bc7c9709986")
                .getAnswer().getValueAsInteger();
        Integer wellbeingRate = review
                .getQuestionById(
                        "1236d288-9b69-458e-8474-c58fcd35ad08")
                .getAnswer().getValueAsInteger();
        Integer productivityRate = review
                .getQuestionById(
                        "86f8f91a-17cb-4058-9dc2-5d439b3daa58")
                .getAnswer().getValueAsInteger();

        review.setDayRate(dayRate);
        review.setWellbeingRate(wellbeingRate);
        review.setProductivityRate(productivityRate);

        registerNewReview.saveToRepository(review);
        System.out.println("Completed! Would you like to see your answers? ('y' or 'n')");
        if (this.scan.nextLine().charAt(0) == 'n') {
            return;
        }
        this.printReview(review);

    }

    public void previousReviewsMenu() {
        this.clear();
        System.out.println("----- User Reviews -----");
        Integer option = Menu.showOptions(this.scan,
                new String[] { "Display recent reviews", "Search review by date", "Go back" });
        switch (option) {
            case 0:
                scan.nextLine();
                this.displayRecentReviews();
                break;
            case 1:
                scan.nextLine();
                this.searchReviewByDate();
                break;

            case 2:
                return;

            default:
                break;
        }
    }

    private void displayRecentReviews() {
        GetReviews getReviews = new GetReviews(CliModule.reviewRepository);
        List<Review> userReviews;
        this.clear();
        System.out.println("----- User Reviews -----");

        try {
            userReviews = getReviews.listAllFromLoggedUser();

            if (userReviews.size() == 0) {
                System.out.println("* No Reviews Yet! *");
                System.out.println("(Press 'Enter' to return)");
                this.scan.nextLine();
                return;
            }
            int displayNumber = 0;
            if (userReviews.size() < 10) {
                for (int i = userReviews.size() - 1; i >= 0; i--) {
                    displayNumber++;
                    Review review = userReviews.get(i);
                    System.out.println(
                            "(" + (displayNumber) + ") " + review.getDate() + " - Day rating: "
                                    + review.getDayRate());
                }
            } else {
                for (int i = userReviews.size() - 1; i >= userReviews.size() - 11; i--) {
                    displayNumber++;
                    Review review = userReviews.get(i);
                    System.out.println(
                            "(" + (displayNumber) + ") " + review.getDate() + " - Day rating: "
                                    + review.getDayRate());
                }
            }

            while (true) {
                System.out.println("Enter the number to display the review, or 'q' to return");
                try {
                    Integer option = this.scan.nextInt();
                    this.scan.nextLine();
                    if (option > 0 && option <= displayNumber) {
                        Review selectedReview = userReviews.get(userReviews.size() - option);
                        this.printReview(selectedReview);
                        return;
                    }
                    System.out.println("* Enter a valid option *");
                } catch (Exception e) {
                    return;
                }
            }

        } catch (Exception e) {
            System.out.println("You need to login first.");
            return;
        }
    }

    private void searchReviewByDate() {
        GetReviews getReviews = new GetReviews(CliModule.reviewRepository);

        while (true) {

            List<Review> userReviews;
            this.clear();
            System.out.println("----- Search Review -----");
            try {
                userReviews = getReviews.listAllFromLoggedUser();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }
            if (userReviews.size() == 0) {
                System.out.println("* No Reviews Yet! *");
                System.out.println("(Press 'Enter' to return)");
                this.scan.nextLine();
                return;
            }
            System.out.println("Type a date (Format: yyyy-mm-dd):");
            String dateString = this.scan.nextLine().trim();
            LocalDate date;
            try {
                date = LocalDate.parse(dateString);
            } catch (Exception e) {
                System.out.println("* Invalid Date, press 'Enter' to return *");
                scan.nextLine();
                return;
            }
            List<Review> reviewsWithDate = userReviews.stream().filter(review -> review.getDate().equals(date))
                    .collect(Collectors.toList());
            if (reviewsWithDate.size() == 0) {
                System.out.println("* No Reviews Found! *");
                System.out.println("(Press 'Enter' to return)");
                scan.nextLine();
                return;
            } else {
                List<String> menu = new ArrayList<String>();
                for (Review review : reviewsWithDate) {
                    menu.add(review.getDate() + " - Day rate: " + review.getDayRate());
                }
                Integer option = Menu.showOptions(scan, menu.toArray(new String[menu.size()]));
                Review selectedReview = reviewsWithDate.get(option);
                printReview(selectedReview);
                scan.nextLine();
                System.out.println("Would you like to search again?");
                System.out.println("(Type 'y' or 'n')");
                if (scan.nextLine().equals("n")) {
                    return;
                }

            }
        }
    }

    public void printReview(Review review) {
        this.clear();
        System.out.println("------ Review :: " + review.getDate() + " ------");
        System.out.println();
        System.out.println("Periodicity: " + review.getPeriod().name());
        System.out.println("Well-being rate: " + review.getWellbeingRate());
        System.out.println("Productivity rate: " + review.getProductivityRate());
        System.out.println("Overall day rate: " + review.getDayRate());
        System.out.println("\n-- Answers --\n");
        List<Question> questions = review.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            System.out.println();
            System.out.println((i + 1) + ") " + questions.get(i).getText());
            String answer = questions.get(i).getAnswer().getValue();
            System.out.println(answer != null ? ("\tR: " + answer) : "\t* No answer provided *");
        }
        System.out.println("\n------------\n");
    }
}
