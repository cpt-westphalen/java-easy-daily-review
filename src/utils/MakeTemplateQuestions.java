package utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import application.entities.TemplateQuestion;
import application.entities.TemplateQuestion.Type;

public class MakeTemplateQuestions {
    public static List<TemplateQuestion> fromScannerNextLine(Scanner fileScanner) {
        List<TemplateQuestion> templateQuestionsList = new LinkedList<TemplateQuestion>();
        while (fileScanner.hasNextLine()) {
            Type templateQuestionType;
            String templateQuestionText;
            String templateQuestionId;

            String line = fileScanner.nextLine();
            if (line.startsWith("i")) {
                templateQuestionId = line.substring(2);
                line = fileScanner.nextLine();
            } else {
                templateQuestionId = UUID.randomUUID().toString();
            }
            switch (line.charAt(0)) {
                case '&':
                    templateQuestionType = Type.TEXT;
                    break;
                case '!':
                    templateQuestionType = Type.BOOLEAN;
                    break;
                case '#':
                    templateQuestionType = Type.NUMBER;
                    break;
                default:
                    templateQuestionType = Type.TEXT;
            }
            templateQuestionText = line.substring(2);
            TemplateQuestion templateQuestion = new TemplateQuestion(templateQuestionId, templateQuestionType,
                    templateQuestionText);
            templateQuestionsList.add(templateQuestion);
        }
        fileScanner.close();
        return templateQuestionsList;
    }
}
