/*
 * THIS IS JUST A FUN PROJECT, I DONT RECORD PEOPLE WITHOUT THEIR CONSENT.
 * IF YOU FIND THIS USEFUL PLEASE VISIT YOUR LAWYER TO LEARN ABOUT PRIVACY LAWS IN YOUR COUNTRY.
 * ALSO VISIT YOUR DOCTOR IF YOU FIND THIS USEFUL, YOU MIGHT BE A PSYCHOPATH.
 */

import java.io.File;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;

final class RecordingMetadata {
    Set<String> RecordingFile = new LinkedHashSet<>();
    LinkedList<String> RecordingDate = new LinkedList<>();
    LinkedList<String> RecordingTime = new LinkedList<>();

    void addRecordingFile(String filePath) {
        this.RecordingFile.add(filePath);
    }

    void addRecordingDate(String date) {
        this.RecordingDate.add(date);
    }

    void addRecordingTime(String time) {
        this.RecordingTime.add(time);
    }
}

final class CallFrequency {
    String name;
    int frequency;
    RecordingMetadata recordingMetadata = new RecordingMetadata();

    CallFrequency(String name) {
        this.name = name;
        this.frequency = 1;
    }

    void incrementFrequency() {
        this.frequency++;
    }

    void addRecordingFile(String filePath) {
        this.recordingMetadata.addRecordingFile(filePath);
    }

    void addRecordingDate(String date) {
        this.recordingMetadata.addRecordingDate(date);
    }

    void addRecordingTime(String time) {
        this.recordingMetadata.addRecordingTime(time);
    }
}

public class RecordingSorter {

    private static String getMonthString(String month) {
        switch (month) {
            case "01":
                return "January";
            case "02":
                return "February";
            case "03":
                return "March";
            case "04":
                return "April";
            case "05":
                return "May";
            case "06":
                return "June";
            case "07":
                return "July";
            case "08":
                return "August";
            case "09":
                return "September";
            case "10":
                return "October";
            case "11":
                return "November";
            case "12":
                return "December";
            default:
                return "Invalid month";
        }
    }

    private static void createData(String folderPath, String outputFolder) {
        HashMap<String, CallFrequency> recordingFileNamesFreqency = new HashMap<String, CallFrequency>();
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        String pattern = "(.*)-([0-9]{6})([0-9]{4})(.*).mp3";
        Pattern regex = Pattern.compile(pattern);
        for (File file : listOfFiles) {
            if (file.isFile()) {
                Matcher matcher = regex.matcher(file.getName());
                if (matcher.matches()) {
                    String name = matcher.group(1);
                    // Populate the recording files array. Break before '-' is the name.
                    String callerName = name.split("-")[0];
                    // System.out.println("Caller name: " + callerName);
                    if (recordingFileNamesFreqency.containsKey(callerName)) {
                        recordingFileNamesFreqency.get(callerName).incrementFrequency();
                    } else {
                        recordingFileNamesFreqency.put(callerName, new CallFrequency(callerName));
                    }
                    // Add the recording file to the list of recording files.
                    recordingFileNamesFreqency.get(callerName).addRecordingFile(file.getAbsolutePath());
                    // Add the recording date to the list of recording dates.
                    recordingFileNamesFreqency.get(callerName).addRecordingDate(matcher.group(2));
                    // Add the recording time to the list of recording times.
                    recordingFileNamesFreqency.get(callerName).addRecordingTime(matcher.group(3));
                }
            }
        }

        if (recordingFileNamesFreqency.size() == 0) {
            System.out.println("No recording files found.");
            return;
        }

        // Call create_data function with the recordingFileNamesFreqency.
        createDataStore(recordingFileNamesFreqency, outputFolder);
    }

    private static void createDirectoryStructure(Queue<String> directoryOrder) {
        while (!directoryOrder.isEmpty()) {
            String directoryPath = directoryOrder.poll();
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }

    }

    private static void createDataStore(HashMap<String, CallFrequency> recordingFileNamesFreqency,
            String outputFolder) {
        Queue<String> directoryOrder = new LinkedList<>();
        File folder = new File(outputFolder);
        for (String key : recordingFileNamesFreqency.keySet()) {
            directoryOrder.add(folder.getAbsolutePath());
            File nameFolder = new File(outputFolder + "/" + recordingFileNamesFreqency.get(key).name);
            directoryOrder.add(nameFolder.getAbsolutePath());
            for (int i = 0; i < recordingFileNamesFreqency.get(key).recordingMetadata.RecordingDate.size(); i++) {
                String date = recordingFileNamesFreqency.get(key).recordingMetadata.RecordingDate.get(i);
                String time = recordingFileNamesFreqency.get(key).recordingMetadata.RecordingTime.get(i);
                // Date: yy -> yyyy
                // Date: mm -> January - December
                // Date: dd -> 01 - 31
                // Time: hh -> 12:00 AM - 11:59 PM
                String year = "20" + date.substring(0, 2);
                String month = getMonthString(date.substring(2, 4));
                String day = date.substring(4, 6);
                String hour = time.substring(0, 2);
                // convert hour to 12 hour format. just Substract 12 if it is greater than 12
                // and append PM else append AM
                if (Integer.parseInt(hour) > 12) {
                    hour = Integer.toString(Integer.parseInt(hour) - 12) + "-PM";
                } else {
                    hour = hour + "-AM";
                }
                String directoryPath = outputFolder + "/" + recordingFileNamesFreqency.get(key).name + "/" + year + "/"
                        + month + "/" + day + "/" + hour;
                File directory = new File(directoryPath);
                directoryOrder.add(directory.getAbsolutePath());
                createDirectoryStructure(directoryOrder);
                for (String recordingFile : recordingFileNamesFreqency.get(key).recordingMetadata.RecordingFile) {
                    File file = new File(recordingFile);
                    file.renameTo(new File(directoryPath + "/" + file.getName()));
                }
                directoryOrder.clear();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java RecordingSorter <input_folder> <output_folder>");
            return;
        }
        String inputFolder = args[0];
        String outputFolder = args[1];
        createData(inputFolder, outputFolder);
    }
}
