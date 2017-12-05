package com.pearson.testng;

import com.pearson.common.Log;
import com.pearson.common.enums.CommonConstants;
import com.pearson.common.enums.Locale;
import com.pearson.common.enums.Node;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlTest;

public class HtmlGenerator extends TestResultPruner {
    public static String LOGS_HTML_OUTPUT_FILENAME = "index.html";
    protected String logDirectory;
    protected Map<String, ITestResult> results;

    public HtmlGenerator() {
        this.logDirectory = Log.FOLDER_PATH + CommonConstants.DIR_SEPARATOR.getValue();
        this.results = new TreeMap();
    }

    public static String getOutputFilename() {
        return LOGS_HTML_OUTPUT_FILENAME;
    }

    public static void setOutputFilename(String outputFilename) {
        LOGS_HTML_OUTPUT_FILENAME = outputFilename;
    }

    public String getLogDirectory() {
        return this.logDirectory;
    }

    public void setLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        Iterator iterator = testContext.getPassedTests().getAllResults().iterator();

        ITestResult result;
        while(iterator.hasNext()) {
            result = (ITestResult)iterator.next();
            if (result != null && result.getName() != null) {
                this.results.put(result.getName(), result);
            }
        }

        iterator = testContext.getFailedTests().getAllResults().iterator();

        while(iterator.hasNext()) {
            result = (ITestResult)iterator.next();
            if (result != null && result.getName() != null) {
                this.results.put(result.getName(), result);
            }
        }

        iterator = testContext.getSkippedTests().getAllResults().iterator();

        while(iterator.hasNext()) {
            result = (ITestResult)iterator.next();
            if (result != null && result.getName() != null) {
                this.results.put(result.getName(), result);
            }
        }

        Date stTest = testContext.getStartDate();
        Date endTest = testContext.getEndDate();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat formatterCalendar = new SimpleDateFormat("MM/dd/yyyy");
        String sumStartTime = formatter.format(stTest);
        String sumEndTime = formatter.format(endTest);
        String sumCalendarDate = formatterCalendar.format(stTest);
        long timeElapsed = endTest.getTime() - stTest.getTime();
        String sumTimeDetails = String.format("%d hr, %d min, %d sec", TimeUnit.MILLISECONDS.toHours(timeElapsed), TimeUnit.MILLISECONDS.toMinutes(timeElapsed), TimeUnit.MILLISECONDS.toSeconds(timeElapsed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed)));
        String sumHost = testContext.getHost();
        String sumInputHost = System.getProperty(CommonConstants.SELENIUM_RC_HOST_PROPERTY.getValue(), "NONE");
        String sumBrowser = System.getProperty(CommonConstants.SELENIUM_BROWSER_TYPE_PROPERTY.getValue(), "FIREFOX");
        String sumEnvironment = CommonConstants.TEST_ENVIRONMENT.getValue();
        XmlTest xmlSuite = testContext.getCurrentXmlTest();
        String sumTestName = xmlSuite.getName();
        if (sumTestName.equals("Default test")) {
            sumTestName = testContext.getName();
        }

        String sumComputerName = "";
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME")) {
            sumComputerName = (String)env.get("COMPUTERNAME");
        } else if (env.containsKey("HOSTNAME")) {
            sumComputerName = (String)env.get("HOSTNAME");
        } else {
            sumComputerName = "Unknown Computer";
        }

        String formatTotalMemory = humanReadableByteCount(Runtime.getRuntime().maxMemory(), true);
        String formatFreeMemory = humanReadableByteCount(Runtime.getRuntime().freeMemory(), true);
        String summaryInfoHTML = "<div class='title'><h3>Test Suite Information</h3><table class='simple-gray'><thead><tr><td style='width:9%'>Environment</td><td style='width:10%'>Suite</td><td style='width:5%'>Locale</td><td style='width:5%'>Node</td><td style='width:6%'>Browser</td><td style='width:8%'>Date</td><td style='width:8%'>Start Time</td><td style='width:8%'>End Time</td><td style='width:10%'>Time Taken</td></tr></thead>\n<tbody><tr><td>" + sumEnvironment + "</td><td>" + sumTestName + "</td><td>" + Locale.getLocaleName() + "</td><td>" + Node.getNodeNumber() + "</td><td>" + sumBrowser + "</td><td>" + sumCalendarDate + "</td><td>" + sumStartTime + "</td><td>" + sumEndTime + "</td><td>" + sumTimeDetails + "</td></tr></tbody></table></div>" + "<div class='title'><h3>System Information</h3><table class='simple-gray'><thead><tr><td style='width:15%'>Host</td><td style='width:15%'>OS</td><td style='width:20%'>OS Arch</td><td style='width:10%'>Cores</td><td style='width:15%'>Max Memory</td><td style='width:15%'>Free Memory</td></tr></thead>\n" + "<tbody><tr><td>" + sumComputerName + "</td><td>" + CommonConstants.OS_NAME.getValue() + "</td><td>" + CommonConstants.ARCH_TYPE.getValue() + "</td><td>" + Runtime.getRuntime().availableProcessors() + "</td><td>" + formatTotalMemory + "</td><td>" + formatFreeMemory + "</td></tr></tbody></table></div></div>\n\n";
        summaryInfoHTML = summaryInfoHTML.toUpperCase();
        if (!this.execute(testContext, summaryInfoHTML, sumTestName)) {
            Log.getDefaultLogger().warning("Failed to generate logs.html");
        }

    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < (long)unit) {
            return bytes + " B";
        } else {
            int exp = (int)(Math.log((double)bytes) / Math.log((double)unit));
            String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
            return String.format("%.1f %sB", (double)bytes / Math.pow((double)unit, (double)exp), pre);
        }
    }

    public void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();

            for(int i = 0; i < children.length; ++i) {
                this.copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
            }
        } else {
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            byte[] buf = new byte[1024];

            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
        }

    }

    public boolean execute(ITestContext testContext, String summaryInfoHTML, String sumTestName) {
        boolean result = false;
        File node = new File(this.logDirectory);
        if (!node.isDirectory()) {
            Log.getDefaultLogger().warning("Invalid directory: " + this.logDirectory);
            return result;
        } else {
            boolean hasFailures = false;
            Iterator var8 = this.results.values().iterator();

            while(var8.hasNext()) {
                ITestResult r = (ITestResult)var8.next();
                if (!r.isSuccess()) {
                    hasFailures = true;
                    break;
                }
            }

            String itsWorkingSnippet = "";

            try {
                File src = new File(CommonConstants.CURRENT_DIR.getValue() + CommonConstants.DIR_SEPARATOR.getValue() + "img");
                File dst = new File(Log.FOLDER_PATH + CommonConstants.DIR_SEPARATOR.getValue() + "style");
                if (!dst.exists()) {
                    dst.mkdir();
                }

                this.copyDirectory(src, dst);
            } catch (IOException var30) {
                var30.printStackTrace();
            }

            if (!hasFailures) {
                try {
                    itsWorkingSnippet = "    $('.itsWorking').click(function() {\n            $('#itsWorking').html(\"<img class=\\\"centered\\\" id=\\\"itsWorkingImg\\\" src=\\\"style/minions.gif\\\" />\");\n            var adjustedWidth = 535;\n            var adjustedHeight = 280;\n            $('#itsWorking').dialog({\n                autoOpen: true,\n                modal: true,\n                resizable: true,\n                height: adjustedHeight,\n                width: adjustedWidth,\n                title: 'All Tests Passed!'\n            });\n            $('.ui-widget-overlay').css({\n                background:\"rgb(0, 0, 0)\",\n                opacity: \".50 !important\",\n                filter: \"Alpha(Opacity=50)\",\n            });            return false;\n    })\n    $('.itsWorking').trigger('click');\n";
                } catch (Exception var29) {
                    var29.printStackTrace();
                }
            }

            List<File> fileList = new ArrayList();
            List<File> miscFileList = new ArrayList();
            HtmlGenerator.TestFiles miscFiles = new HtmlGenerator.TestFiles("Miscellaneous");
            Map<String, HtmlGenerator.TestFiles> sortedList = new TreeMap();
            File[] list = node.listFiles();

            String htmlFile;
            for(int i = 0; list != null && i < list.length; ++i) {
                File file = list[i];
                if (file.isFile() && !file.isHidden() && file.canRead() && file.length() >= 1L && file.exists()) {
                    fileList.add(file);
                    miscFileList.add(file);
                    htmlFile = file.getName();
                    if (htmlFile.startsWith("log_") && htmlFile.endsWith(".log")) {
                        int start = htmlFile.indexOf("log_") + "log_".length();
                        int end = htmlFile.lastIndexOf(".log");
                        String key = htmlFile.substring(start, end);
                        sortedList.put(key, new HtmlGenerator.TestFiles(key, this.results));
                    }
                }
            }

            Iterator var37 = sortedList.values().iterator();

            while(var37.hasNext()) {
                HtmlGenerator.TestFiles testFiles = (HtmlGenerator.TestFiles)var37.next();
                Iterator var43 = fileList.iterator();

                while(var43.hasNext()) {
                    File file = (File)var43.next();
                    if (file.getName().contains(testFiles.getTestName())) {
                        testFiles.add(file);
                        miscFileList.remove(file);
                    }
                }
            }

            var37 = miscFileList.iterator();

            while(var37.hasNext()) {
                File file = (File)var37.next();
                miscFiles.add(file);
            }

            if (sortedList.isEmpty()) {
                Log.getDefaultLogger().warning("No log files found");
                return result;
            } else {
                StringBuilder buffer = new StringBuilder();
                buffer.append("<html xmlns='http://www.w3.org/1999/xhtml'>\n<head>\n <meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>\n <title></title>\n <meta name='keywords' content=''/> <meta name='description' content=''/>");
                if (Boolean.getBoolean(CommonConstants.SELENIUM_GRID_PROPERTY.getValue())) {
                    buffer.append("<link href='//fonts.googleapis.com/css?family=Source+Sans+Pro:200,300,400,600,700,900' rel='stylesheet'/>\n<link href='//fonts.googleapis.com/css?family=Open+Sans:300,400,600' rel='stylesheet' type='text/css' />\n<link href='//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css' rel='stylesheet' />\n<link href='style/default.css' rel='stylesheet' type='text/css' media='all'/>\n<link href='style/fonts.css' rel='stylesheet' type='text/css' media='all'/>\n<link href='style/extent.css' rel='stylesheet' type='text/css' media='all'/>\n<link rel='stylesheet' type='text/css' href='style/sweet-alert.css'/>\n<link rel='stylesheet' type='text/css' href='style/lightbox.css'/>\n<script type='text/javascript' src='//code.jquery.com/jquery-1.10.1.min.js'></script>\n<script type='text/javascript' src='//www.google.com/jsapi'></script>\n<script src='//cdn.rawgit.com/noelboss/featherlight/1.0.4/release/featherlight.min.js' type='text/javascript' charset='utf-8'></script>\n<script type='text/javascript' src='style/sweet-alert.min.js'></script>\n<script type='text/javascript' src='style/lightbox.min.js'></script>\n");
                } else {
                    buffer.append("<link href='http://fonts.googleapis.com/css?family=Source+Sans+Pro:200,300,400,600,700,900' rel='stylesheet'/>\n<link href='http://fonts.googleapis.com/css?family=Open+Sans:300,400,600' rel='stylesheet' type='text/css' />\n<link href='http://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css' rel='stylesheet' />\n<link href='style/default.css' rel='stylesheet' type='text/css' media='all'/>\n<link href='style/fonts.css' rel='stylesheet' type='text/css' media='all'/>\n<link href='style/extent.css' rel='stylesheet' type='text/css' media='all'/>\n<link rel='stylesheet' type='text/css' href='style/sweet-alert.css'/>\n<link rel='stylesheet' type='text/css' href='style/lightbox.css'/>\n<link rel='stylesheet' href='https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css'>\n<script type='text/javascript' src='http://code.jquery.com/jquery-1.12.4.min.js'></script>\n<script src='https://code.jquery.com/jquery-1.12.4.js'></script>\n<script src='https://code.jquery.com/ui/1.12.1/jquery-ui.js'></script>\n<script type='text/javascript' src='https://www.google.com/jsapi'></script>\n<script src='http://cdn.rawgit.com/noelboss/featherlight/1.0.4/release/featherlight.min.js' type='text/javascript' charset='utf-8'></script>\n<script type='text/javascript' src='style/sweet-alert.min.js'></script>\n<script type='text/javascript' src='style/lightbox.min.js'></script>\n");
                }

                buffer.append("\n<script type=\"text/javascript\">\n$(document).ready(function() {\n\n//extend JQuery to do case insensitive search\n$.extend($.expr[\":\"], {\n\"containsIN\": function(elem, i, match, array) {\nreturn (elem.textContent || elem.innerText || \"\").toLowerCase().indexOf((match[3] || \"\").toLowerCase()) >= 0;\n}\n});\n\n//expand the testcase to show the details\n$('table.testcase').click(function() {\n$(this).next().toggle();\n});\n\n//show the test comment with Sweet Alert\n$('td.stepcomment').click(function() {\nswal('',$(this).text().trim().split(\"${NEWLINE}\").join('\\n'));\n});\n\n//show tests by filter\n$('div.cs-btn').click(function() {\nvar id = $(this).attr('id').trim();\n\n$('div.testcase').hide();\n\nif(/pass/i.test(id))\n$('td.testpass').closest('div').show();\nelse if(/fail/i.test(id))\n$('td.testfail').closest('div').show();\nelse if(/warn/i.test(id))\n$('td.testwarn').closest('div').show();\nelse if(/error/i.test(id))\n$('td.testerror').closest('div').show();\nelse if(/norun/i.test(id))\n$('td.testnorun').closest('div').show();\nelse\n$('div.testcase').show();\n\nif( $('div.testcase:visible').length == 0 )\n$('div.notest').show();\nelse\n$('div.notest').hide();\n});\n\n//show testset details\n$('li#testdetailspage').click(function() {\n\n$('li#summarypage').removeClass(\"current_page_item\");\n$(this).addClass('current_page_item');\n\n$('div.testsummary').hide();\n$('div.testset').show();\n});\n\n//show summary details\n$('li#summarypage').click(function() {\n\n$('li#testdetailspage').removeClass(\"current_page_item\");\n$(this).addClass('current_page_item');\n\n$('div.testsummary').show();\n$('div.testset').hide();\n});\n\n//show tests by keyword\n$( 'input.search' ).change(function() {\n$('div.testcase').hide();\n$('div.testcase:containsIN('+$(this).val().trim() +')').show();\n\nif( $('div.testcase:visible').length == 0 )\n$('div.notest').show();\nelse\n$('div.notest').hide();\n});\n\n// testcase execution summary\nif($('div.testcase').length > 0 ) {\n$('#totaltccount').text($('div.testcase').length);\n$('#passtccount').text($('td.testpass').length);\n$('#warntccount').text($('td.testwarn').length);\n$('#othertccount').text($('td.testnorun').length);\n$('#failtccount').text($('td.testerror').length + $('td.testfail').length);\n$('#passpercent').text( (($('td.testpass').length) / $('div.testcase').length * 100).toFixed(2) + ' %');\n$('#failpercent').text( (($('td.testerror').length + $('td.testfail').length) / $('div.testcase').length * 100).toFixed(2) + ' %');\n}\n\n" + itsWorkingSnippet + "\n\n" + "    $('.previewDialog').click(function() {\n" + "        var file = $(this).data('file');\n" + "        if(file.indexOf(\".log\") > -1 && file.indexOf(\"error\") == 0) {\n" + "            var adjustedWidth = $(window).width() * 0.7;\n" + "            var adjustedHeight = $(window).height() * 0.6;\n" + "            $('#errorLogDialog').html(\"<iframe id=\\\"errorLogDialogIframe\\\" frameborder=\\\"0\\\" src=\\\"\" + file + \"\\\"></iframe>\");\n" + "            $('#errorLogDialog').dialog({\n" + "                autoOpen: true,\n" + "                modal: false,\n" + "                resizable: true,\n" + "                height: adjustedHeight,\n" + "                width: adjustedWidth,\n" + "                title: 'Error Log: ' + file,\n" + "                resize : function() {\n" + "                    var dialogWidth = $(this).width(); \n" + "                    var dialogHeight = $(this).height(); \n" + "                    $('#errorLogDialogIframe').width(dialogWidth); \n" + "                    $('#errorLogDialogIframe').height(dialogHeight); \n" + "                    $(this).css('overflow', 'hidden');\n" + "                }\n" + "            });\n" + "            var dialogWidth = $('#errorLogDialog').width(); \n" + "            var dialogHeight = $('#errorLogDialog').height(); \n" + "            $('#errorLogDialogIframe').width(dialogWidth); \n" + "            $('#errorLogDialogIframe').height(dialogHeight);\n" + "            $('#errorLogDialog').css('overflow', 'hidden');\n" + "        } else if(file.indexOf(\".log\") > -1) {\n" + "            var adjustedWidth = $(window).width() * 0.7;\n" + "            var adjustedHeight = $(window).height() * 0.7;\n" + "            $('#logDialog').html(\"<iframe id=\\\"logDialogIframe\\\" frameborder=\\\"0\\\" src=\\\"\" + file + \"\\\"></iframe>\");\n" + "            $('#logDialog').dialog({\n" + "                autoOpen: true,\n" + "                modal: false,\n" + "                resizable: true,\n" + "                height: adjustedHeight,\n" + "                width: adjustedWidth,\n" + "                title: 'Log: ' + file,\n" + "                resize : function() {\n" + "                    var dialogWidth = $(this).width();\n" + "                    var dialogHeight = $(this).height();\n" + "                    $('#logDialogIframe').width(dialogWidth);\n" + "                    $('#logDialogIframe').height(dialogHeight);\n" + "                    $(this).css('overflow', 'hidden');\n" + "                }\n" + "            });\n" + "            var dialogWidth = $('#logDialog').width(); \n" + "            var dialogHeight = $('#logDialog').height(); \n" + "            $('#logDialogIframe').width(dialogWidth); \n" + "            $('#logDialogIframe').height(dialogHeight);\n" + "            $('#logDialog').css('overflow', 'hidden');\n" + "        } else if(file.indexOf(\".png.txt\") > -1) {\n" + "            $('#pngTxtDialog').html(\"<iframe id=\\\"pngTxtDialogIframe\\\" frameborder=\\\"0\\\" src=\\\"\" + file + \"\\\"></iframe>\");\n" + "            var adjustedWidth = $(window).width() * 0.6;\n" + "            var adjustedHeight = $(window).height() * 0.7;\n" + "            $('#pngTxtDialog').dialog({\n" + "                autoOpen: true,\n" + "                modal: false,\n" + "                resizable: true,\n" + "                height: adjustedHeight,\n" + "                width: adjustedWidth,\n" + "                title: 'Page Source: ' + file,\n" + "                resize : function() {\n" + "                    var dialogWidth = $(this).width(); \n" + "                    var dialogHeight = $(this).height(); \n" + "                    $('#pngTxtDialogIframe').width(dialogWidth);\n" + "                    $('#pngTxtDialogIframe').height(dialogHeight);\n" + "                    $(this).css('overflow', 'hidden');\n" + "                }\n" + "            });\n" + "            var dialogWidth = $('#pngTxtDialog').width(); \n" + "            var dialogHeight = $('#pngTxtDialog').height(); \n" + "            $('#pngTxtDialogIframe').width(dialogWidth); \n" + "            $('#pngTxtDialogIframe').height(dialogHeight);\n" + "            $('#pngTxtDialog').css('overflow', 'hidden');\n" + "        } else if(file.indexOf(\".png\") > -1) {\n" + "            $('#pngDialog').html(\"<img id=\\\"pngDialogImg\\\" src=\\\"\" + file + \"\\\" />\");\n" + "            var adjustedWidth = $(window).width() * 0.95;\n" + "            var adjustedHeight = $(window).height() * 0.9;\n" + "            $('#pngDialog').dialog({\n" + "                autoOpen: true,\n" + "                modal: false,\n" + "                resizable: true,\n" + "                height: adjustedHeight,\n" + "                width: adjustedWidth,\n" + "                title: 'Image: ' + file\n" + "            });\n" + "        } else if(file.indexOf(\".json\") > -1) {\n" + "            var adjustedWidth = $(window).width() * 0.4;\n" + "            var adjustedHeight = $(window).height() * 0.7;\n" + "            $('#logDialog').html(\"<iframe id=\\\"logDialogIframe\\\" frameborder=\\\"0\\\" src=\\\"\" + file + \"\\\"></iframe>\");\n" + "            $('#logDialog').dialog({\n" + "                autoOpen: true,\n" + "                modal: false,\n" + "                resizable: true,\n" + "                height: adjustedHeight,\n" + "                width: adjustedWidth,\n" + "                title: 'Test Data: ' + file,\n" + "                resize : function() {\n" + "                    var dialogWidth = $(this).width();\n" + "                    var dialogHeight = $(this).height();\n" + "                    $('#logDialogIframe').width(dialogWidth);\n" + "                    $('#logDialogIframe').height(dialogHeight);\n" + "                    $(this).css('overflow', 'hidden');\n" + "                }\n" + "            });\n" + "            var dialogWidth = $('#logDialog').width(); \n" + "            var dialogHeight = $('#logDialog').height(); \n" + "            $('#logDialogIframe').width(dialogWidth); \n" + "            $('#logDialogIframe').height(dialogHeight);\n" + "            $('#logDialog').css('overflow', 'hidden');\n" + "        } else {\n" + "            $('#errorDialog').html(\"<strong>File not supported:</strong><br />\" + file);\n" + "            $('#errorDialog').dialog({\n" + "                autoOpen: true,\n" + "                modal: false,\n" + "                resizable: true,\n" + "                height: 125,\n" + "                width: 400,\n" + "                title: 'ERROR'\n" + "            });\n" + "        }\n" + "        return false;\n" + "    });\n" + "});\n" + "</script>\n\n" + "<script type=\"text/javascript\">\n" + "google.load('visualization', '1', {packages:['corechart']});\n" + "google.setOnLoadCallback(testSetChart);\n" + "google.setOnLoadCallback(teststepChart);\n\n" + "function testSetChart() {\n" + "var data = google.visualization.arrayToDataTable([\n" + "['Test Status', 'Count'],\n" + "['Pass',     $('td.testpass').length],\n" + "['Fail',     $('td.testfail').length],\n" + "['Warning',     $('td.testwarn').length],\n" + "['Error',     $('td.testerror').length],\n" + "['No-Run',     $('td.testnorun').length],\n" + "]);\n\n" + "var options = {\n" + "backgroundColor: { fill:'transparent' },\n" + "colors: ['green', 'tomato', 'orange', 'red', 'gray'],\n" + "height: 275,\n" + "pieHole: 0.4,\n" + "pieSliceText: 'value',\n" + "title: 'Testcase Summary',\n" + "width: 400\n" + "};\n\n" + "var chart = new google.visualization.PieChart(document.getElementById('test-status-dashboard'));\n" + "chart.draw(data, options);\n" + "}\n\n" + "function teststepChart() {\n" + "var data = google.visualization.arrayToDataTable([\n" + "['Test Status', 'Count'],\n" + "['Pass',     $('td.pass').length],\n" + "['Fail',      $('td.fail').length],\n" + "['Fatal',     $('td.fatal').length],\n" + "['Error',      $('td.error').length],\n" + "['Warning',    $('td.warn').length],\n" + "['Info',    $('td.info').length]\n" + "]);\n\n" + "var options = {\n" + "backgroundColor: { fill:'transparent' },\n" + "colors: ['green', 'tomato', 'darkred', 'red', 'orange', 'dodgerblue'],\n" + "height: 275,\n" + "pieHole: 0.4,\n" + "pieSliceText: 'value',\n" + "title: 'Step Summary',\n" + "width: 400\n" + "};\n\n" + "var chart = new google.visualization.PieChart(document.getElementById('step-status-dashboard'));\n" + "chart.draw(data, options);\n" + "}\n" + "</script>\n\n");
                buffer.append("<style type=\"text/css\">\nimg.centered { display: block; margin-left: auto; margin-right: auto; }\n</style>\n");
                buffer.append("</head>\n<body>\n<div id=\"logDialog\"></div>\n<div id=\"errorLogDialog\"></div>\n<div id=\"pngDialog\"></div>\n<div id=\"pngTxtDialog\"></div>\n<div id=\"errorDialog\"></div>\n<div id=\"itsWorking\"></div>\n");
                buffer.append("<div id=\"page\" class=\"container\">\n<div id='header'>\n<div id='logo'>\n\t<img src=\"style/qg.png\" alt=\"\"/>\n\t<h1><a href=\"#\">QGlobal</a></h1>\n\t<span></span></div>\n<div id=\"menu\"><ul><li class=\"current_page_item\" id=\"summarypage\"><a href=\"#\" accesskey=\"1\" title=\"Summary\">Summary</a></li>\n<li id=\"testdetailspage\"><a href=\"#\" accesskey=\"2\" title=\"Testcase Details\">Testcase Details</a></li></ul>\n</div>\n</div>\n<div id='main'><div class='testsummary'><div class='title'><h3>Testcase Execution Summary");
                if (!hasFailures) {
                    buffer.append("<span class='itsWorking'> - All Tests PASSED!</span>");
                }

                buffer.append("</h3><table class='simple-gray'><colgroup><col span='1'/><col style='background-color:#99ff99'/>\n<col style='background-color:#FFFFD1'/><col style='background-color:#FF704D'/><col span='1'/><col style='background-color:#CCCCFF'/><col style='background-color:#FFAD99'/></colgroup>\n<thead><tr><td style='width:14%'>Total</td><td style='width:14%'>Pass</td><td style='width:14%'>Warn</td><td style='width:14%'>Fail/Error</td><td style='width:14%'>Others</td><td style='width:14%'>Pass %</td><td style='width:14%'>Fail %</td></tr></thead><tbody><tr><td id='totaltccount'>0</td><td id='passtccount'>0</td><td id='warntccount'>0</td><td id='failtccount'>0</td><td id='othertccount'>0</td><td id='passpercent'>0</td><td id='failpercent'>0</td></tr></tbody></table></div><div class='graphs'><div id='test-status-dashboard'></div><div id='step-status-dashboard'></div></div>" + summaryInfoHTML + "<div class='testset' style='display:none;'>\n" + "<div id='test-filters'><span class='filter1'><b>Filter By Status: </b></span><span class='filter2'>\n" + "<div id='filter-all' class='cs-btn cs-btn-blue btn-toggle'><i class='fa fa-list'></i>ALL</div>\n" + "<div id='filter-pass' class='cs-btn cs-btn-green btn-toggle'><i class='fa fa-check'></i>PASS</div>\n" + "<div id='filter-fail' class='cs-btn cs-btn-red btn-toggle'><i class='fa fa-times'></i>FAIL</div>\n" + "<div id='filter-error' class='cs-btn cs-btn-lightred btn-toggle'><i class='fa fa-exclamation-circle'></i>ERROR</div>\n" + "<div id='filter-warning' class='cs-btn cs-btn-orange btn-toggle'><i class='fa fa-warning'></i>WARNING</div>\n" + "<div id='filter-norun' class='cs-btn cs-btn-gray btn-toggle'><i class='fa fa-question'></i>NORUN</div></span>\n" + "<span class='filter2'>\n" + "<input type='search' class='search' placeholder='Enter Keyword' size='25' maxlength='20'/>\n" + "</span>\n" + "</div>\n");
                Iterator var42 = sortedList.values().iterator();

                while(var42.hasNext()) {
                    HtmlGenerator.TestFiles testFiles = (HtmlGenerator.TestFiles)var42.next();
                    testFiles.setSuiteName(sumTestName);
                    testFiles.toHtmlNew(buffer);
                }

                buffer.append("<div class='notest' style='display:none;'>\n<h2>No test found</h2></div>\n</div></div></div>\n");
                buffer.append("</body></html>");
                BufferedWriter out = null;

                try {
                    htmlFile = this.logDirectory + LOGS_HTML_OUTPUT_FILENAME;
                    FileWriter fileWriter = new FileWriter(htmlFile);
                    out = new BufferedWriter(fileWriter);
                    out.write(buffer.toString());
                    result = true;
                } catch (Exception var27) {
                    var27.printStackTrace();
                    result = false;
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (Exception var26) {
                        ;
                    }

                }

                return result;
            }
        }
    }

    public static class LogFile {
        private HtmlGenerator.LogFile.Type type;
        private File file;
        private long timestamp;
        private long elapsedTime;
        private Throwable throwable;

        public LogFile(File file) {
            this.type = HtmlGenerator.LogFile.Type.UNKNOWN;
            this.timestamp = -1L;
            this.elapsedTime = -1L;
            this.throwable = null;
            this.setFile(file);
        }

        public final String toString() {
            return this.getClass().getSimpleName() + " {" + this.type.toString() + " " + this.file.toString() + "}";
        }

        public final Throwable getThrowable() {
            return this.throwable;
        }

        public final void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }

        public final HtmlGenerator.LogFile.Type getType() {
            return this.type;
        }

        public final void setType(HtmlGenerator.LogFile.Type type) {
            this.type = type;
        }

        public final File getFile() {
            return this.file;
        }

        public final long getTimestamp() {
            return this.timestamp;
        }

        public final void setElapsedTime(long elapsedTime) {
            this.elapsedTime = elapsedTime;
        }

        public final long getElapsedTime() {
            return this.elapsedTime;
        }

        public final void setFile(File file) {
            this.file = file;
            String filename = this.file.getName();
            if (filename.endsWith(".log")) {
                if (filename.startsWith("log_")) {
                    this.type = HtmlGenerator.LogFile.Type.LOG_FILE;
                } else if (filename.startsWith("error_log_")) {
                    this.type = HtmlGenerator.LogFile.Type.LOG_ERROR;
                }
            } else if (filename.endsWith(".png")) {
                this.type = HtmlGenerator.LogFile.Type.PNG_IMAGE;
                if (filename.endsWith("xception.png")) {
                    this.type = HtmlGenerator.LogFile.Type.PNG_EXCEPTION;
                } else if (filename.endsWith("Success.png")) {
                    this.type = HtmlGenerator.LogFile.Type.PNG_SUCCESS;
                }

                String fname = filename.substring(0, filename.lastIndexOf("."));
                if (fname.contains("_")) {
                    int last = fname.lastIndexOf("_");
                    int first = fname.lastIndexOf("_", last - 1);
                    if (first > 0 && last > 0) {
                        fname = fname.substring(first + 1, last);
                    }
                }

                this.timestamp = -1L;

                try {
                    this.timestamp = Long.parseLong(fname);
                } catch (Exception var6) {
                    ;
                }
            } else if (filename.endsWith(".png.txt")) {
                this.type = HtmlGenerator.LogFile.Type.PNG_SOURCE_TXT;
            }

        }

        public final StringBuilder toHtml2(StringBuilder buffer, String testName, int id) {
            if (this.getType().equals(HtmlGenerator.LogFile.Type.UNKNOWN)) {
                if (!this.getFile().getName().equals(HtmlGenerator.LOGS_HTML_OUTPUT_FILENAME)) {
                    buffer.append("\n<div class=\"").append(this.getClass().getSimpleName().toLowerCase()).append(" ").append(this.getType().toString().toLowerCase()).append("\" >").append("<a href=\"").append(this.file.getName()).append("\" >").append(this.file.getName()).append("</a>").append("</div>\n");
                }
            } else {
                String pngId = id < 0 ? "" : " id=\"" + testName + id + "\" data-test=\"" + testName + "\" data-value=\"" + id + "\" ";
                String throwableStr = this.throwable != null ? StringEscapeUtils.escapeHtml4(this.throwable.getMessage()) : "";
                buffer.append("\n<tr class=\"").append(this.getClass().getSimpleName().toLowerCase()).append(" ").append(this.getType().toString().toLowerCase()).append("\" >");
                if (this.getType().toString().toLowerCase().equals("log_file")) {
                    String jName = CommonConstants.JSON_PATH.getValue().substring(CommonConstants.JSON_PATH.getValue().lastIndexOf("\\") + 1);
                    buffer.append("<td style='text-align:left'>").append("<a ").append("class=\"previewDialog ").append("json_file").append("\" ").append("data-img=\"img").append(testName).append("\" ").append("data-file=\"").append(".." + CommonConstants.DIR_SEPARATOR.getValue() + CommonConstants.JSON_PATH.getValue()).append("\" >").append(jName).append("</a>").append("</td>");
                } else {
                    buffer.append("<td style='text-align:left'></td>");
                }

                buffer.append("<td style='text-align:right'>").append("<a target='_blank' href=\"").append(this.file.getName()).append("\" >raw</a>&nbsp;|&nbsp;").append("<a ").append(pngId).append("data-desc=\"" + throwableStr + "\" ").append("class=\"previewDialog ").append(this.getType().toString().toLowerCase()).append("\" ").append("data-img=\"img").append(testName).append("\" ").append("data-file=\"").append(this.file.getName()).append("\" >").append(this.file.getName()).append("</a>").append("</td>").append("</tr>\n");
            }

            return buffer;
        }

        public static enum Type {
            UNKNOWN,
            LOG_FILE,
            LOG_ERROR,
            PNG_IMAGE,
            PNG_EXCEPTION,
            PNG_SOURCE_TXT,
            PNG_SUCCESS;

            private Type() {
            }
        }
    }

    public static class TestFiles {
        private String testName = "";
        private List<HtmlGenerator.LogFile> files = new ArrayList();
        private Map<String, ITestResult> results = null;
        private boolean miscellaneousFiles = false;
        private String suiteName;

        public TestFiles(String testName) {
            this.testName = testName;
            this.miscellaneousFiles = true;
        }

        public void setSuiteName(String name) {
            this.suiteName = name;
        }

        public TestFiles(String testName, Map<String, ITestResult> results) {
            this.testName = testName;
            this.results = results;
        }

        public boolean isMiscellaneousFiles() {
            return this.miscellaneousFiles;
        }

        public void setMiscellaneousFiles(boolean miscellaneousFiles) {
            this.miscellaneousFiles = miscellaneousFiles;
        }

        public String getTestName() {
            return this.testName;
        }

        public List<HtmlGenerator.LogFile> getFiles() {
            return this.files;
        }

        public Map<HtmlGenerator.LogFile.Type, TreeMap<String, HtmlGenerator.LogFile>> getSortedMap() {
            Map<HtmlGenerator.LogFile.Type, TreeMap<String, HtmlGenerator.LogFile>> map = new TreeMap();
            HtmlGenerator.LogFile.Type[] var5;
            int var4 = (var5 = HtmlGenerator.LogFile.Type.values()).length;

            for(int var3 = 0; var3 < var4; ++var3) {
                HtmlGenerator.LogFile.Type type = var5[var3];
                map.put(type, new TreeMap());
            }

            Iterator var7 = this.files.iterator();

            while(var7.hasNext()) {
                HtmlGenerator.LogFile file = (HtmlGenerator.LogFile)var7.next();
                switch($SWITCH_TABLE$com$pearson$testng$HtmlGenerator$LogFile$Type()[file.getType().ordinal()]) {
                case 2:
                    ((TreeMap)map.get(HtmlGenerator.LogFile.Type.LOG_FILE)).put(file.getFile().getName(), file);
                    break;
                case 3:
                    ((TreeMap)map.get(HtmlGenerator.LogFile.Type.LOG_ERROR)).put(file.getFile().getName(), file);
                    break;
                case 4:
                default:
                    ((TreeMap)map.get(HtmlGenerator.LogFile.Type.PNG_IMAGE)).put(file.getFile().getName(), file);
                }
            }

            return map;
        }

        public void add(File file) {
            HtmlGenerator.LogFile logFile = new HtmlGenerator.LogFile(file);
            if (!logFile.getType().equals(HtmlGenerator.LogFile.Type.UNKNOWN)) {
                this.files.add(logFile);
            } else if (this.miscellaneousFiles) {
                this.files.add(logFile);
            }

        }

        public StringBuilder toHtmlNew(StringBuilder buffer) {
            Map<HtmlGenerator.LogFile.Type, TreeMap<String, HtmlGenerator.LogFile>> map = this.getSortedMap();
            boolean hasFiles = false;
            HtmlGenerator.LogFile.Type[] var7;
            int var6 = (var7 = HtmlGenerator.LogFile.Type.values()).length;

            for(int var5 = 0; var5 < var6; ++var5) {
                HtmlGenerator.LogFile.Type type = var7[var5];
                if (!((TreeMap)map.get(type)).isEmpty()) {
                    hasFiles = true;
                    break;
                }
            }

            if (!hasFiles) {
                return buffer;
            } else {
                StringBuffer stringBuffer = null;
                String timeDetails = "";
                String testTime = "";
                String testNameLc = "";
                String resultStatus = "";
                String description = "";
                int index;
                if (this.results != null && !this.results.isEmpty()) {
                    label266: {
                        testNameLc = this.getTestName().toLowerCase();
                        index = testNameLc.indexOf("_t");
                        if (index > 0) {
                            testNameLc = testNameLc.substring(0, index);
                        }

                        int resCount = 0;
                        Iterator var13 = this.results.values().iterator();

                        ITestResult result;
                        String resultName;
                        do {
                            if (!var13.hasNext()) {
                                break label266;
                            }

                            result = (ITestResult)var13.next();
                            ++resCount;
                            resultName = result.getName().toLowerCase();
                            description = result.getMethod().getDescription();
                            String passFail = "";
                            String failPass = "";
                            if (result.getTestContext() != null && result.getTestContext().getFailedTests() != null && result.getTestContext().getPassedTests() != null) {
                                int failed = result.getTestContext().getFailedTests().size();
                                int passed = result.getTestContext().getPassedTests().size();
                                passFail = String.valueOf(passed) + "/" + (failed + passed) + " tests passed";
                                failPass = String.valueOf(failed) + "/" + (failed + passed) + " tests failed";
                            }
                        } while(!testNameLc.startsWith(resultName) && !resultName.startsWith(testNameLc));

                        resultStatus = "";
                        if (!result.isSuccess() && !resultName.contains("status=success")) {
                            if (resultName.contains("status=fail")) {
                                resultStatus = "FAIL";
                            } else if (result.getStatus() > 0) {
                                switch(result.getStatus()) {
                                case 1:
                                case 4:
                                    resultStatus = "PASS";
                                    break;
                                case 2:
                                case 3:
                                default:
                                    resultStatus = "FAIL";
                                }
                            }
                        } else {
                            resultStatus = "PASS";
                        }

                        Date date = new Date(result.getStartMillis());
                        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy - HH:mm:ss");
                        testTime = formatter.format(date);
                        double timeElapsed = (double)(result.getEndMillis() - result.getStartMillis()) / 1000.0D;
                        timeDetails = String.valueOf(timeElapsed) + " secs";
                        this.createJSONResults(this.suiteName.toUpperCase(), testNameLc.toUpperCase(), description, result.getStartMillis(), resultStatus);
                    }
                }

                buffer.append("<div class='testcase' >\n<table class='testcase'>\n<tr><td class='testno'>" + testNameLc + "</td>\n" + "<td class='testdescription'>\n" + description + "\n" + "</td>\n" + "<td title='" + resultStatus + "' class='test" + resultStatus + "'></td>" + "</tr>\n" + "</table>\n" + "<div class='exec-info' style='display: none;'>\n" + "<table class='simple-gray'><thead><tr><td style='text-align:left'>Script Info</td><td style='text-align:right'>" + testTime + " (" + timeDetails + ")</td></tr></thead><tbody><tr id='tcheader" + testNameLc + "'>\n" + "<td style='text-align:left'>Test Data</td>\n" + "<td style='text-align:right'>Log File</td>\n" + "</tr>");
                index = 0;
                HtmlGenerator.LogFile.Type[] var38;
                int var37 = (var38 = HtmlGenerator.LogFile.Type.values()).length;

                label214:
                for(int var36 = 0; var36 < var37; ++var36) {
                    HtmlGenerator.LogFile.Type type = var38[var36];
                    if (!((TreeMap)map.get(type)).isEmpty()) {
                        List<Long> timestamps = new ArrayList();
                        Iterator var43 = ((TreeMap)map.get(type)).values().iterator();

                        while(true) {
                            HtmlGenerator.LogFile file;
                            do {
                                String line;
                                if (!var43.hasNext()) {
                                    int i = false;
                                    Iterator var48 = ((TreeMap)map.get(type)).descendingMap().values().iterator();

                                    HtmlGenerator.LogFile file;
                                    while(var48.hasNext()) {
                                        file = (HtmlGenerator.LogFile)var48.next();
                                        if (!file.getType().equals(HtmlGenerator.LogFile.Type.PNG_EXCEPTION) && !file.getType().equals(HtmlGenerator.LogFile.Type.PNG_IMAGE) && !file.getType().equals(HtmlGenerator.LogFile.Type.PNG_SUCCESS)) {
                                            file.toHtml2(buffer, this.getTestName(), -1);
                                        }
                                    }

                                    var48 = ((TreeMap)map.get(type)).descendingMap().values().iterator();

                                    while(true) {
                                        do {
                                            do {
                                                do {
                                                    do {
                                                        do {
                                                            if (!var48.hasNext()) {
                                                                continue label214;
                                                            }

                                                            file = (HtmlGenerator.LogFile)var48.next();
                                                        } while(file.getType().equals(HtmlGenerator.LogFile.Type.PNG_EXCEPTION));
                                                    } while(file.getType().equals(HtmlGenerator.LogFile.Type.PNG_IMAGE));
                                                } while(file.getType().equals(HtmlGenerator.LogFile.Type.PNG_SUCCESS));
                                            } while(file.getFile().getName().contains("error"));
                                        } while(file.getFile().getName().endsWith("png.txt"));

                                        try {
                                            FileReader fileReader = new FileReader(file.getFile());
                                            BufferedReader bufferedReader = new BufferedReader(fileReader);
                                            stringBuffer = new StringBuffer();
                                            boolean startFound = false;
                                            int stepCount = 0;

                                            while((line = bufferedReader.readLine()) != null) {
                                                if (line.contains("[com.pearson.common.Log initialize]")) {
                                                    startFound = false;
                                                    stepCount = 0;
                                                }

                                                if (startFound) {
                                                    boolean screenShotFound = false;
                                                    if (line.contains("[com.pearson.common.Utils captureScreenshot]")) {
                                                        screenShotFound = true;
                                                    }

                                                    String timeStep = "";
                                                    String stepText = "";
                                                    boolean extraHelper = false;
                                                    timeStep = line.substring(6, 18);
                                                    if (timeStep.matches("^[\\d].*")) {
                                                        stepText = line.substring(line.indexOf("]") + 2);
                                                    } else {
                                                        timeStep = "";
                                                        stepText = line.substring(line.indexOf("]") + 1);
                                                        extraHelper = true;
                                                    }

                                                    stringBuffer.append("<tr><td>" + stepCount + "</td><td>" + timeStep + "</td>");
                                                    if (stepText.startsWith("WARNING")) {
                                                        stringBuffer.append("<td alt='warn' title='warn' class='status warn'>");
                                                        stringBuffer.append("<i class='fa fa-warning'></i></td>\n");
                                                    } else if (stepText.startsWith("SEVERE")) {
                                                        stringBuffer.append("<td alt='fail' title='fail' class='status fail'>");
                                                        stringBuffer.append("<i class='fa fa-times'></i></td>\n");
                                                    } else {
                                                        stringBuffer.append("<td alt='pass' title='pass' class='status pass'>");
                                                        if (!extraHelper) {
                                                            stringBuffer.append("<i class='fa fa-check'></i>");
                                                        }

                                                        stringBuffer.append("</td>\n");
                                                    }

                                                    stringBuffer.append("<td class='stepdescription'>" + stepText + "</td>\n");
                                                    stringBuffer.append("<td></td>\n");
                                                    if (screenShotFound) {
                                                        String fileName = line.substring(line.lastIndexOf("\\") + 1);
                                                        String testNameFull = line.substring(line.indexOf("File:") + 6).split(" - ")[0];
                                                        stringBuffer.append("<td><a id='" + testNameFull + "' class='previewDialog png_image' data-file='" + fileName + "' data-img='img" + testNameFull + "' data-desc='' data-value='0' data-test='" + testNameFull + "'>" + "<img class='example-image' src='" + fileName + "' alt='image'></a></td>");
                                                    } else {
                                                        stringBuffer.append("<td></td>\n");
                                                    }

                                                    stringBuffer.append("</tr>\n");
                                                    ++stepCount;
                                                }

                                                if (line.contains("[Web Driver:")) {
                                                    startFound = true;
                                                    ++stepCount;
                                                }
                                            }

                                            fileReader.close();
                                        } catch (IOException var30) {
                                            var30.printStackTrace();
                                        }
                                    }
                                }

                                file = (HtmlGenerator.LogFile)var43.next();
                                if (file.getType().equals(HtmlGenerator.LogFile.Type.PNG_EXCEPTION)) {
                                    Iterator var49 = this.results.values().iterator();

                                    while(var49.hasNext()) {
                                        ITestResult result = (ITestResult)var49.next();
                                        Throwable throwable = result.getThrowable();
                                        if (throwable != null) {
                                            line = result.getName();
                                            if (line != null && !line.isEmpty()) {
                                                int index = line.indexOf(" on ");
                                                line = index > 0 ? line.substring(0, index) : line;
                                                index = line.indexOf(" ");
                                                line = index > 0 ? line.substring(0, index) : line;
                                                File f = file.getFile();
                                                if (f != null && f.getName().contains(line) && f.getName().contains(throwable.getClass().getSimpleName())) {
                                                    file.setThrowable(throwable);
                                                }
                                            }
                                        }
                                    }
                                }
                            } while(!file.getType().equals(HtmlGenerator.LogFile.Type.PNG_EXCEPTION) && !file.getType().equals(HtmlGenerator.LogFile.Type.PNG_IMAGE) && !file.getType().equals(HtmlGenerator.LogFile.Type.PNG_SUCCESS));

                            if (file.getTimestamp() > -1L) {
                                if (!timestamps.isEmpty()) {
                                    long prevTimestamp = ((Long)timestamps.get(timestamps.size() - 1)).longValue();
                                    file.setElapsedTime(file.getTimestamp() - prevTimestamp);
                                }

                                timestamps.add(file.getTimestamp());
                            }

                            ++index;
                        }
                    }
                }

                buffer.append("</tbody></table>\n\n");
                buffer.append("<table class='simple-gray'><thead><tr><td style='width:5%'>No</td><td style='width:12%'>Time</td><td style='width:4%'></td><td style='width:59%'>Step</td><td style='width:10%'>Comments</td><td style='width:8%'>Image</td></tr></thead><tbody>\n\n");
                buffer.append(stringBuffer);
                buffer.append("</tbody></table></div></div>\n");
                return buffer;
            }
        }

        private void createJSONResults(String suiteName, String tcid, String description, long sTime, String status) {
            Date dsTime = new Date(sTime);
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat formatTime = new SimpleDateFormat("HHmmss");
            JSONObject json = new JSONObject();

            try {
                json.put("PROJECT", CommonConstants.PROJECT.getValue());
                json.put("SUITE", suiteName);
                json.put("ENVIRONMENT", CommonConstants.TEST_ENVIRONMENT.getValue().toUpperCase());
                json.put("TCID", tcid);
                json.put("DESCRIPTION", description);
                json.put("EDATE", formatDate.format(dsTime));
                json.put("ETIME", formatTime.format(dsTime));
                json.put("STATUS", status);
            } catch (JSONException var24) {
                var24.printStackTrace();
            }

            String outputJson = Log.FOLDER_PATH + CommonConstants.DIR_SEPARATOR.getValue() + "json";
            File outputJsonFile = new File(outputJson);
            if (!outputJsonFile.exists()) {
                outputJsonFile.mkdir();
            }

            try {
                Throwable var13 = null;
                Object var14 = null;

                try {
                    FileWriter file = new FileWriter(outputJson + CommonConstants.DIR_SEPARATOR.getValue() + tcid + ".json");

                    try {
                        file.write(json.toString());
                        file.flush();
                    } finally {
                        if (file != null) {
                            file.close();
                        }

                    }
                } catch (Throwable var26) {
                    if (var13 == null) {
                        var13 = var26;
                    } else if (var13 != var26) {
                        var13.addSuppressed(var26);
                    }

                    throw var13;
                }
            } catch (IOException var27) {
                var27.printStackTrace();
            }

        }
    }
}
