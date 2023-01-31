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

import application.useCases.GetReviews;
import application.useCases.GetTemplateQuestions;
import application.useCases.ListTemplateReviews;
import application.useCases.LoginUser;
import application.useCases.RegisterNewUser;
import application.useCases.RemoveQuestionFromTemplateReview;
import application.useCases.UpdateTemplateReview;
import application.useCases.AddQuestionToTemplateReview;
import application.useCases.CreateNewReview;
import application.useCases.CreateTemplateQuestion;
import application.useCases.CreateTemplateReview;

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
        while (!Auth.isAuthorized()) {
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
            LoginUser loginUser = new LoginUser(CliModule.userRepository);
            try {
                loginUser.exec(name, pin);
                System.out.println();
                System.out.println("* Login was successfull! *");
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println();
                System.out.println("* Error: " + e.getMessage() + " *");
                System.out.println("(Press 'Enter' to try again; type '0' to go back)");
                if (scan.nextLine().startsWith("0")) {
                    return;
                }

            }
        }
    }

    private void registerUser() {
        String login, pin;
        RegisterNewUser registerNewUser = new RegisterNewUser(CliModule.userRepository);

        while (true) {
            clear();
            System.out.println("----- Register -----");
            System.out.println("Name: ");
            login = scan.nextLine();
            System.out.println();
            System.out.println("Pin (numbers only): ");
            pin = scan.nextLine();
            try {
                registerNewUser.exec(login, pin);
                System.out.println();
                System.out.println("* User created! Please login *");
                Thread.sleep(1500);
                return;
            } catch (Exception e) {
                System.out.println();
                System.out.println("* Error: " + e.getMessage() + " *");
                System.out.println("(Press 'Enter' to try again; or type '0' to return)");
                if (scan.nextLine().startsWith("0")) {
                    return;
                }
            }
        }

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
        System.out.println("----- New Review :: " + LocalDate.now() + " -----");
        System.out.println();

        List<Question> reviewQuestions = new LinkedList<Question>();

        // custom questions
        for (TemplateQuestion templateQuestion : template.getTemplateQuestions()) {
            Question answeredQuestion = makeQuestionFromTemplateWithUserInputAnswer(templateQuestion);
            reviewQuestions.add(answeredQuestion);
        }
        // default questions
        List<TemplateQuestion> defaultQuestions = TemplateReview.getDefaultTemplateQuestions();
        for (TemplateQuestion templateQuestion : defaultQuestions) {
            Question answeredQuestion = makeQuestionFromTemplateWithUserInputAnswer(templateQuestion);
            reviewQuestions.add(answeredQuestion);
        }

        CreateNewReview createNewReview = new CreateNewReview(CliModule.reviewRepository);
        Review review = createNewReview.exec(Auth.getLoggedUser().getId(), UUID.randomUUID().toString(),
                LocalDate.now(), reviewQuestions);
        System.out.println("Completed! Would you like to see your answers? ('y' or 'n')");
        if (scan.nextLine().toLowerCase().startsWith("n")) {
            return;
        }
        printReview(review);

    }

    private void previousReviewsMenu() {
        while (true) {
            clear();
            System.out.println("----- User Reviews -----");
            Integer option = Menu.showOptions(scan,
                    new String[] { "Display recent reviews", "Search review by date" });
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
    }

    private void displayRecentReviews() {
        GetReviews getReviews = new GetReviews(CliModule.reviewRepository);
        List<Review> userReviews;
        while (true) {
            clear();
            System.out.println("----- User Reviews -----");

            try {
                userReviews = getReviews.listRecentFromLoggedUser();

                if (userReviews.size() == 0) {
                    System.out.println("* No Reviews Yet! *");
                    System.out.println("(Press 'Enter' to return)");
                    scan.nextLine();
                    return;
                }

                String[] displayOptions = new String[userReviews.size() <= 10 ? userReviews.size() : 10];

                for (int i = 0; i < displayOptions.length; i++) {
                    Review review = userReviews.get(userReviews.size() - i - 1);
                    displayOptions[i] = review.getDate() + " - Day rating: "
                            + review.getDayRate();
                }
                Integer selectedOption = Menu.showOptions(scan, displayOptions);
                scan.nextLine();
                if (selectedOption == null) {
                    return;
                }

                Review selectedReview = userReviews.get(userReviews.size() - selectedOption - 1);
                printReview(selectedReview);
            } catch (Exception e) {
                System.out.println("You need to login first.");
                return;
            }

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
        String[] options = { "Customize Review Template", "Create new Review Template" };
        Integer selectedOption = Menu.showOptions(scan, options);
        if (selectedOption == null) {
            scan.nextLine();
            return;
        }
        switch (selectedOption) {
            case 0:
                // Customize Review Template
                scan.nextLine();
                TemplateReview selectedTemplateReview = selectDailyReviewTemplate();
                if (selectedTemplateReview == null) {
                    break;
                }
                customizeReviewTemplate(selectedTemplateReview);
                break;

            case 1:
                // Create Template Review
                scan.nextLine();
                createNewTemplateReview();
            default:
                break;
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
            String[] options = { "View details", "Add question", "Remove question", "Edit template name" };
            Integer selected = Menu.showOptions(scan, options);
            if (selected == null) {
                scan.nextLine();
                return;
            }
            UpdateTemplateReview updateTemplateReview = new UpdateTemplateReview(CliModule.templateReviewRepository);
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
                    removeQuestionFromTemplateReviewMenu(template);
                    break;
                case 3:
                    scan.nextLine();
                    System.out.println("Type a new title / name for the template: ");
                    String newName = scan.nextLine();
                    template.setDisplayName(newName);
                    updateTemplateReview.exec(template);
                    break;
                default:
                    break;
            }

        }
    }

    private void displayTemplateReviewDetails(TemplateReview templateReview) {
        clear();
        System.out.println("----- Template Details :: " + templateReview.getId() + " -----");
        System.out.println("Name: " + templateReview.getDisplayName());
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
        AddQuestionToTemplateReview addQuestionToTemplateReview = new AddQuestionToTemplateReview(
                CliModule.templateReviewRepository);
        while (true) {
            clear();
            System.out.println("----- Add Question :: " + template.getDisplayName() + " -----");
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

        AddQuestionToTemplateReview addQuestionToTemplateReview = new AddQuestionToTemplateReview(
                CliModule.templateReviewRepository);

        while (true) {
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

            TemplateReview updatedTemplateReview = addQuestionToTemplateReview.exec(template,
                    selectedTemplateQuestion);

            if (updatedTemplateReview == null) {
                System.out.println("* This question is already on the Template *");
            }

            System.out.println();
            System.out.println("* Template Review updated! *");
            System.out.println("(Press 'Enter' to return)");
            scan.nextLine();
        }
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
        clear();
        while (true) {
            System.out.println("----- Select Template Question to Add -----");
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

    private void removeQuestionFromTemplateReviewMenu(TemplateReview templateReview) {
        while (true) {
            clear();
            System.out.println("----- Remove Question From Template -----");

            List<TemplateQuestion> questionsFromTemplateReview = templateReview.getTemplateQuestions();

            String[] options = new String[questionsFromTemplateReview.size()];
            for (int i = 0; i < options.length; i++) {
                TemplateQuestion question = questionsFromTemplateReview.get(i);
                options[i] = question.getDisplayName() + " (" + question.getType() + ")";
            }

            Integer selectedOption = Menu.showOptions(scan, options);
            if (selectedOption == null) {
                return;
            }
            TemplateQuestion selectedTemplateQuestion = questionsFromTemplateReview.get(selectedOption);
            RemoveQuestionFromTemplateReview removeQuestionFromTemplateReview = new RemoveQuestionFromTemplateReview(
                    CliModule.templateReviewRepository);
            try {
                removeQuestionFromTemplateReview.exec(selectedTemplateQuestion.getId(), templateReview);
            } catch (Exception e) {
                System.out.println("* Error: " + e.getMessage() + " *");
                System.out.println("(Press 'Enter' to try again)");
                scan.nextLine();
            }
        }
    }

    private void createNewTemplateReview() {
        TemplateReview newTemplateReview = null;
        String name = null;
        List<TemplateQuestion> selectedTemplateQuestions = new LinkedList<>();
        // GetTemplateQuestions getTemplateQuestions = new
        // GetTemplateQuestions(CliModule.templateQuestionRepository);
        // List<TemplateQuestion> allTemplateQuestions = getTemplateQuestions.exec();
        while (true) {
            clear();
            System.out.println("----- New Review Template -----");
            if (name == null) {
                System.out.println("Type a display name for the template: ");
                System.out.println("(Press 'Enter' to send)");
                name = scan.nextLine().trim();
                System.out.println();
            }

            if (newTemplateReview == null) {
                CreateTemplateReview createTemplateReview = new CreateTemplateReview(
                        CliModule.templateReviewRepository);
                try {
                    newTemplateReview = createTemplateReview.exec(name, selectedTemplateQuestions);
                } catch (Exception e) {
                    System.out.println("* Error: " + e.getMessage() + " *");
                    System.out.println("(Press 'Enter' to return)");
                    scan.nextLine();
                    return;
                }
            }

            addQuestionToTemplateReviewMenu(newTemplateReview);

            if (!selectedTemplateQuestions.isEmpty()) {
                System.out.println("* Template Created Sucessfully! *");
                System.out.println("(Press 'Enter' to return)");
                scan.nextLine();
                return;
            }

        }
    };

    private void printReview(Review review) {
        clear();
        System.out.println("------ Review :: " + review.getDate() + " ------");
        System.out.println();
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
        System.out.println();
        System.out.println("(Press 'Enter' to return)");
        scan.nextLine();
    }

    private Question makeQuestionFromTemplateWithUserInputAnswer(TemplateQuestion templateQuestion) {
        System.out.println(templateQuestion.getText());
        Type questionType = templateQuestion.getType();
        if (questionType.equals(Type.BOOLEAN)) {
            System.out.println("(Type 'y' or 'n', 'Enter' to submit)");
        }
        if (questionType.equals(Type.NUMBER)) {
            System.out.println("(Type an integer between 0 and 100, 'Enter' to submit)");
        }
        if (questionType.equals(Type.TEXT)) {
            System.out.println("(Type any text or leave it blank, 'Enter' to submit)");
        }
        String answerText = scan.nextLine();
        Question question = new Question(templateQuestion, null, null);
        Answer answer = question.getAnswer();
        if (!answerText.isEmpty()) {
            answer.setValue(answerText);
            question.setAnswer(answer);
        }
        System.out.println();
        System.out.println("--");
        return question;
    }
}
