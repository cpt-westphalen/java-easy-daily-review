package cli;

import java.time.LocalDate;
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
import application.entities.TemplateReview.Period;
import application.useCases.GetReviews;
import application.useCases.GetTemplateQuestions;
import application.useCases.ListTemplateReviews;
import application.useCases.AddQuestionToTemplateReview;
import application.useCases.CreateNewReview;
import application.useCases.CreateTemplateQuestion;

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
        clear();
        System.out.println("----- Authentication -----");
        Integer option = Menu.showOptions(scan, new String[] { "Register new user", "Login" });
        if (option == null) {
            scan.nextLine();
            return;
        }
        switch (option) {
            case 0:
                scan.nextLine();
                registerUser();
                break;
            case 1:
                scan.nextLine();
                loginUser();
                break;
        }
    }

    private void loginUser() {
        while (!Auth.isAuthorized()) {
            clear();
            System.out.println("----- Login -----");
            String name, pin;
            System.out.println("Name: ");
            name = scan.nextLine();
            System.out.println("Pin: ");
            pin = scan.nextLine();
            clear();
            User user = CliModule.userRepository.findByName(name);
            if (user == null) {
                System.out.println("*Error: User not found*");
                System.out.println();
                authMenu();
                return;
            }
            if (!Auth.login(user, Integer.valueOf(pin))) {
                System.out.println("*Error: Incorrect Pin*");
                System.out.println();
                authMenu();
                return;
            }
        }
        System.out.println("Login was successfull!");
    }

    private void registerUser() {
        clear();
        System.out.println("----- Register -----");
        String login;
        Integer pin;

        System.out.println("Name: ");
        login = scan.nextLine();
        do {
            System.out.println();
            System.out.println("Pin (numbers only): ");
            try {
                pin = scan.nextInt();
                break;
            } catch (Exception e) {
                System.out.println("* Enter only numbers *");
            }

        } while (true);

        clear();

        String id = UUID.randomUUID().toString();
        User user = new User(id, login, pin);
        CliModule.userRepository.add(user);

        List<User> users = CliModule.userRepository.getAll();

        System.out.println("User list: ");
        for (User u : users) {
            System.out.println("- " + u.getName());
        }
        System.out.println();
    }

    public void mainMenu() {
        GetReviews getReviews = new GetReviews(CliModule.reviewRepository);
        while (true) {
            String[] options = new String[] {
                    getReviews.hasReviewedToday() ? "Overwrite today's review" : "Write today's review",
                    "Check previous reviews", "Settings" };
            clear();
            LocalDate today = LocalDate.now();
            String name = Auth.getLoggedUser().getName();
            System.out.println("----- " + name + " :: " + today + " -----");

            Integer selectedOption = Menu.showOptions(scan, options);

            if (selectedOption == null) {
                scan.nextLine();
                return;
            }

            switch (selectedOption) {
                case 0:
                    // write new review
                    scan.nextLine();
                    selectTemplateReview();
                    break;

                case 1:
                    // check previous reviews
                    scan.nextLine();
                    previousReviewsMenu();
                    break;

                case 2:
                    // show settings
                    scan.nextLine();
                    clear();
                    settingsMenu();
                    break;

                default:
                    break;
            }
        }
    }

    private void selectTemplateReview() {
        clear();
        ListTemplateReviews listTemplateReviews = new ListTemplateReviews(CliModule.templateReviewRepository);
        List<TemplateReview> templateReviewsList = listTemplateReviews.exec();
        String[] templateOptions = new String[templateReviewsList.size()];
        for (int i = 0; i < templateReviewsList.size(); i++) {
            TemplateReview tr = templateReviewsList.get(i);
            templateOptions[i] = tr.getDisplayName() + " - Questions: " + tr.getTemplateQuestions().size();
        }
        Integer selectedOption = Menu.showOptions(scan, templateOptions);
        if (selectedOption == null) {
            scan.nextLine();
            return;
        }
        TemplateReview template = templateReviewsList.get(selectedOption);
        scan.nextLine();
        registerNewReview(template);
    }

    private void registerNewReview(TemplateReview template) {
        clear();
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
            String answerText = scan.nextLine();
            System.out.println();
            System.out.println("--");
            if (!answerText.isEmpty()) {
                answer.setValue(answerText);
                question.setAnswer(answer);
            }
            questions.add(question);
        }
        CreateNewReview createNewReview = new CreateNewReview(CliModule.reviewRepository);
        Review review = createNewReview.exec(Auth.getLoggedUser().getId(), UUID.randomUUID().toString(),
                template.getPeriod(),
                LocalDate.now(), questions);
        System.out.println("Completed! Would you like to see your answers? ('y' or 'n')");
        if (scan.nextLine().charAt(0) == 'n') {
            return;
        }
        printReview(review);

    }

    private void previousReviewsMenu() {
        clear();
        System.out.println("----- User Reviews -----");
        Integer option = Menu.showOptions(scan,
                new String[] { "Display recent reviews", "Search review by date", "Go back" });
        if (option == null) {
            scan.nextLine();
            return;
        }
        switch (option) {
            case 0:
                scan.nextLine();
                displayRecentReviews();
                break;
            case 1:
                scan.nextLine();
                searchReviewByDate();
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
        clear();
        System.out.println("----- User Reviews -----");

        try {
            userReviews = getReviews.listAllFromLoggedUser();

            if (userReviews.size() == 0) {
                System.out.println("* No Reviews Yet! *");
                System.out.println("(Press 'Enter' to return)");
                scan.nextLine();
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
                    Integer option = scan.nextInt();
                    scan.nextLine();
                    if (option > 0 && option <= displayNumber) {
                        Review selectedReview = userReviews.get(userReviews.size() - option);
                        printReview(selectedReview);
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
            clear();
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
                scan.nextLine();
                return;
            }
            System.out.println("Type a date (Format: yyyy-mm-dd):");
            String dateString = scan.nextLine().trim();
            LocalDate date;
            try {
                date = LocalDate.parse(dateString);
            } catch (Exception e) {
                System.out.println("* Invalid Date, press 'Enter' to return *");
                scan.nextLine();
                return;
            }
            List<Review> reviewsOnDate = userReviews.stream().filter(review -> review.getDate().equals(date))
                    .collect(Collectors.toList());
            if (reviewsOnDate.size() == 0) {
                System.out.println("* No Reviews Found! *");
                System.out.println("(Press 'Enter' to return)");
                scan.nextLine();
                return;
            } else {
                String[] options = new String[reviewsOnDate.size()];
                for (int i = 0; i < options.length; i++) {
                    Review review = reviewsOnDate.get(i);
                    options[i] = review.getDate() + " - Day rate: " + review.getDayRate();
                }
                Integer option = Menu.showOptions(scan, options);
                scan.nextLine();
                if (option == null) {
                    return;
                }
                Review selectedReview = reviewsOnDate.get(option);
                printReview(selectedReview);

                System.out.println("Would you like to search again?");
                System.out.println("(Type 'y' or 'n')");
                if (scan.nextLine().equals("n")) {
                    return;
                }

            }
        }
    }

    private void settingsMenu() {
        clear();
        System.out.println("----- Settings -----");
        String[] options = { "Customize Review template" };
        Integer selectedOption = Menu.showOptions(scan, options);
        if (selectedOption == null) {
            scan.nextLine();
            return;
        }
        switch (selectedOption) {
            case 0:
                // Customize Review Template
                scan.nextLine();
                customizeReviewTemplatePeriodMenu();
                break;

            default:
                break;
        }
    }

    private void customizeReviewTemplatePeriodMenu() {
        while (true) {
            clear();
            System.out.println("----- Customize Review Template -----");
            String[] options = { "Daily Review Templates", "Weekly Review Templates" };
            Integer selectedOption = Menu.showOptions(scan, options);
            if (selectedOption == null) {
                scan.nextLine();
                return;
            }
            switch (selectedOption) {
                case 0:
                    TemplateReview selectedTemplateReview = selectDailyReviewTemplate();
                    scan.nextLine();
                    customizeReviewTemplate(selectedTemplateReview);
                    break;
                case 1:
                    // TODO List weekly review templates for selection
                    break;

                default:
                    break;
            }
        }
    }

    private TemplateReview selectDailyReviewTemplate() {
        ListTemplateReviews listTemplateReviews = new ListTemplateReviews(CliModule.templateReviewRepository);
        List<TemplateReview> templateReviews = listTemplateReviews.exec();
        while (true) {

            clear();
            System.out.println("----- Daily Review Templates -----");

            if (templateReviews == null || templateReviews.isEmpty()) {
                System.out.println("* No Templates Found! *");
                System.out.println("(Press 'Enter' to return)");
                scan.nextLine();
                return null;
            }
            String[] templateReviewDisplayNames = new String[templateReviews.size()];
            for (int i = 0; i < templateReviews.size(); i++) {
                TemplateReview tr = templateReviews.get(i);
                templateReviewDisplayNames[i] = tr.getDisplayName() + " - Questions: "
                        + tr.getTemplateQuestions().size();
            }
            Integer selectedOption = Menu.showOptions(scan, templateReviewDisplayNames);
            if (selectedOption == null) {
                scan.nextLine();
                return null;
            }
            return templateReviews.get(selectedOption);
        }
    }

    private void customizeReviewTemplate(TemplateReview template) {
        while (true) {
            clear();
            System.out.println("----- Customize Template :: " + template.getDisplayName() + " -----");
            String[] options = { "View details", "Add question", "Remove question", "Edit template name",
                    template.getPeriod().equals(Period.DAILY) ? "Change periodicity to WEEKLY"
                            : "Change periodicity to DAILY" };
            Integer selected = Menu.showOptions(scan, options);
            if (selected == null) {
                scan.nextLine();
                return;
            }
            switch (selected) {
                case 0:
                    scan.nextLine();
                    displayTemplateReviewDetails(template);
                    break;
                case 1:
                    scan.nextLine();
                    addQuestionToTemplateReviewMenu(template);
                    return;
                case 2:
                    scan.nextLine();
                    // TODO remove question from selected template
                    break;
                case 3:
                    scan.nextLine();
                    System.out.println("Type a new title / name for the template: ");
                    String newName = scan.nextLine();
                    template.setDisplayName(newName);
                    break;
                case 4:
                    scan.nextLine();
                    if (template.getPeriod().equals(Period.WEEKLY)) {
                        template.setPeriod(Period.DAILY);
                        break;
                    }
                    template.setPeriod(Period.WEEKLY);
                    break;

                default:
                    break;
            }
            // TODO update template review use-case 'updateTemplateReview(TemplateReview t)'
        }
    }

    private void displayTemplateReviewDetails(TemplateReview templateReview) {
        clear();
        System.out.println("----- Template Details :: " + templateReview.getId() + " -----");
        System.out.println("Name: " + templateReview.getDisplayName());
        System.out.println("Periodicity: " + templateReview.getPeriod());
        System.out.println("----- Questions -----");
        List<TemplateQuestion> templateQuestions = templateReview.getTemplateQuestions();
        for (int i = 0; i < templateQuestions.size(); i++) {
            TemplateQuestion tq = templateQuestions.get(i);
            System.out.println(i + ") " + tq.getDisplayName());
            System.out.println("Text: " + tq.getText());
            System.out.println("Type: " + tq.getType());
            System.out.println("-----");
        }
        System.out.println("(Press 'Enter' to return)");
        scan.nextLine();
        return;
    }

    private void addQuestionToTemplateReviewMenu(TemplateReview template) {
        while (true) {
            clear();
            System.out.println("----- Add Question :: " + template.getDisplayName() + "-----");
            Integer selected = Menu.showOptions(scan,
                    new String[] { "Existing Template Questions", "Create New Template Question" });
            if (selected == null) {
                scan.nextLine();
                return;
            }
            switch (selected) {
                case 0:
                    scan.nextLine();
                    addExistingQuestionToTemplateReview(template);
                    break;
                case 1:
                    scan.nextLine();
                    TemplateQuestion createdTemplateQuestion = createNewTemplateQuestion();
                    if (createdTemplateQuestion == null)
                        break;
                    AddQuestionToTemplateReview addQuestionToTemplateReview = new AddQuestionToTemplateReview(
                            CliModule.templateReviewRepository);
                    addQuestionToTemplateReview.exec(template, createdTemplateQuestion);
                    break;

                default:
                    break;
            }
        }
    }

    private void addExistingQuestionToTemplateReview(TemplateReview template) {

        GetTemplateQuestions getTemplateQuestions = new GetTemplateQuestions(
                CliModule.templateQuestionRepository);
        List<TemplateQuestion> allTemplateQuestions = getTemplateQuestions.exec();

        List<TemplateQuestion> templateQuestionsOnTemplateReview = template.getTemplateQuestions();

        List<TemplateQuestion> filteredTemplateQuestions = allTemplateQuestions.stream().filter(tq -> {
            for (TemplateQuestion existingTemplateQuestion : templateQuestionsOnTemplateReview) {
                if (existingTemplateQuestion.getId().equals(tq.getId())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

        TemplateQuestion selectedTemplateQuestion = selectTemplateQuestionFromList(filteredTemplateQuestions);

        if (selectedTemplateQuestion == null)
            return;

        AddQuestionToTemplateReview addQuestionToTemplateReview = new AddQuestionToTemplateReview(
                CliModule.templateReviewRepository);
        TemplateReview updatedTemplateReview = addQuestionToTemplateReview.exec(template,
                selectedTemplateQuestion);

        if (updatedTemplateReview == null) {
            System.out.println("* This question is already on the Template *");
        }

        System.out.println("[ Template Review updated! ]");
        System.out.println("(Press 'Enter' to return)");
        scan.nextLine();
    }

    private TemplateQuestion createNewTemplateQuestion() {
        int attempts = 0;
        while (true) {
            try {
                clear();
                System.out.println("----- Create Template Question -----");
                System.out.println("Enter the question text:");
                String questionText = scan.nextLine();

                System.out.println();
                System.out.println("Now, select what kind of answer this question expects:");

                Integer selectedOption = Menu.showOptions(scan,
                        new String[] { "Descriptive, textual", "Yes or No", "A number from 0 to 100" });
                scan.nextLine();
                if (selectedOption == null)
                    return null;

                Type type = selectedOption == 0 ? Type.TEXT : selectedOption == 1 ? Type.BOOLEAN : Type.NUMBER;

                System.out.println();
                System.out.println("Give your question a display name / title:");
                String questionDisplayName = scan.nextLine();

                System.out.println();
                System.out.println("Is everything right? ('y' / 'n')");

                if (scan.nextLine().toLowerCase().startsWith("y")) {
                    CreateTemplateQuestion createTemplateQuestion = new CreateTemplateQuestion(
                            CliModule.templateQuestionRepository);

                    TemplateQuestion createdTemplateQuestion = createTemplateQuestion.exec(null, type, questionText,
                            questionDisplayName);

                    if (createdTemplateQuestion == null) {
                        throw new Exception("Template Question could not be created, please verify your inputs");
                    }

                    System.out.println(
                            "[ Success! Template Question created with id: " + createdTemplateQuestion.getId() + " ]");
                    return createdTemplateQuestion;
                }
            } catch (Exception e) {
                System.out.println("* Error: " + e.getMessage() + " *");
                if (++attempts == 3) {
                    System.out.println("(press 'enter' to return)");
                    return null;
                }
                System.out.println("(press 'enter' to try again)");
                scan.nextLine();
            }
        }
    }

    private TemplateQuestion selectTemplateQuestionFromList(List<TemplateQuestion> templateQuestions) {
        if (templateQuestions.isEmpty()) {
            System.out.println("* This template uses all available questions! *");
            System.out.println("(Press 'Enter' to return)");
            scan.nextLine();
            return null;
        }
        String[] options = new String[templateQuestions.size()];
        for (int i = 0; i < templateQuestions.size(); i++) {
            TemplateQuestion tq = templateQuestions.get(i);
            String type = tq.getType().name();
            String formattedType = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
            options[i] = tq.getDisplayName() + " (" + formattedType + ")";
        }
        while (true) {
            Integer selected = Menu.showOptions(scan, options);
            if (selected == null) {
                scan.nextLine();
                return null;
            }
            TemplateQuestion selectedTemplateQuestion = templateQuestions.get(selected);
            scan.nextLine();
            return selectedTemplateQuestion;
        }
    }

    private void printReview(Review review) {
        clear();
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
