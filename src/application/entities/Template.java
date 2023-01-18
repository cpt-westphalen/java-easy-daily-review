package application.entities;

public class Template {

    public static enum Period {
        DAILY,
        WEEKLY,
        QUARTERLY,
        YEARLY
    };

    public static String lastQuestionText = "How would you rate your day as a whole? (integer, 0-100)";

}
