package com.pearson.test.qglobal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestJmeter {
    public TestJmeter() {
    }

    public static void main(String[] args) {
        String response = "{\"meta\":{\"total\":4},\"slas\":[{\"id\":22674564,\"measure\":\"AV\",\"grade\":\"K\",\"gradeVal\":0,\"date\":1510876800000,\"score\":25,\"natlValue\":93,\"natlColorCode\":\"rgb(0,150,201)\",\"hasMonitor\":false,\"isValid\":true,\"isDeletable\":false,\"measureGradeLevelId\":21,\"schoolYearId\":2017,\"assignmentId\":null,\"assignmentLoginName\":null,\"assignmentLoginTestCode\":null,\"assignmentFormSequence\":null,\"assignmentFormCode\":null,\"assignmentStatus\":null,\"assignmentIsLocked\":null,\"dateFormated\":\"11/17/2017\",\"goalStatus\":\"not_recommended\",\"goalStatusFillPercentage\":null,\"goalStatusPercentage\":null,\"baselineAssessmentId\":null},{\"id\":22674567,\"measure\":\"IS\",\"grade\":\"K\",\"gradeVal\":0,\"date\":1510876800000,\"score\":4,\"natlValue\":12,\"natlColorCode\":\"rgb(240,209,64)\",\"hasMonitor\":false,\"isValid\":true,\"isDeletable\":false,\"measureGradeLevelId\":23,\"schoolYearId\":2017,\"assignmentId\":null,\"assignmentLoginName\":null,\"assignmentLoginTestCode\":null,\"assignmentFormSequence\":null,\"assignmentFormCode\":null,\"assignmentStatus\":null,\"assignmentIsLocked\":null,\"dateFormated\":\"11/17/2017\",\"goalStatus\":\"not_recommended\",\"goalStatusFillPercentage\":null,\"goalStatusPercentage\":null,\"baselineAssessmentId\":null},{\"id\":22674565,\"measure\":\"LNF\",\"grade\":\"K\",\"gradeVal\":0,\"date\":1510876800000,\"score\":8,\"natlValue\":10,\"natlColorCode\":\"rgb(237,168,56)\",\"hasMonitor\":false,\"isValid\":true,\"isDeletable\":false,\"measureGradeLevelId\":20,\"schoolYearId\":2017,\"assignmentId\":null,\"assignmentLoginName\":null,\"assignmentLoginTestCode\":null,\"assignmentFormSequence\":null,\"assignmentFormCode\":null,\"assignmentStatus\":null,\"assignmentIsLocked\":null,\"dateFormated\":\"11/17/2017\",\"goalStatus\":\"not_recommended\",\"goalStatusFillPercentage\":null,\"goalStatusPercentage\":null,\"baselineAssessmentId\":null},{\"id\":22674566,\"measure\":\"LWSF\",\"grade\":\"K\",\"gradeVal\":0,\"date\":1510876800000,\"score\":68,\"natlValue\":99,\"natlColorCode\":\"rgb(0,150,201)\",\"hasMonitor\":false,\"isValid\":true,\"isDeletable\":false,\"measureGradeLevelId\":24,\"schoolYearId\":2017,\"assignmentId\":null,\"assignmentLoginName\":null,\"assignmentLoginTestCode\":null,\"assignmentFormSequence\":null,\"assignmentFormCode\":null,\"assignmentStatus\":null,\"assignmentIsLocked\":null,\"dateFormated\":\"11/17/2017\",\"goalStatus\":\"not_recommended\",\"goalStatusFillPercentage\":null,\"goalStatusPercentage\":null,\"baselineAssessmentId\":null}]}";
        System.out.println("Response: " + response);
        String totalSLA = retrive("\"total\":(\\d+)", response, 1);
        System.out.println("Total SLA: " + totalSLA);
        int tot = Integer.parseInt(totalSLA);
        String[] aMeasures = new String[tot];
        String[] aGrades = new String[tot];
        String[] expMeasures = new String[]{"AV", "IS", "LNF", "LWSF"};
        String[] var10000 = new String[]{"K", "K", "K", "K"};
        if (tot != 0) {
            int count = 0;

            for(int i = 0; i < tot; ++i) {
                aMeasures[i] = retrive("measure\":\"([a-zA-Z-]+)", response, i + 1);
                aGrades[i] = retrive("grade\":\"([a-zA-Z-]+)", response, i + 1);
                System.out.println(aMeasures[i]);

                for(int j = 0; j < expMeasures.length; ++j) {
                    if (aMeasures[i].equals(expMeasures[j])) {
                        System.out.println(aMeasures[i] + " SLA found");
                        ++count;
                        break;
                    }
                }
            }

            if (count != tot) {
                System.out.println("Not all SLA found");
            }
        } else {
            System.out.println("Error - NO SLA");
        }

    }

    public static String retrive(String regex, String response, int match) {
        Pattern regexPatt = Pattern.compile(regex);
        Matcher matcher = regexPatt.matcher(response);
        int count = 0;

        do {
            if (!matcher.find()) {
                if (count == 0) {
                    System.out.println("Regex not found " + regex);
                }

                return null;
            }

            ++count;
        } while(count != match);

        return matcher.group(1);
    }
}
