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
    private static void createData(String folderPath, String folderPathOut) {
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
        createDataStore(recordingFileNamesFreqency, folderPathOut);
    }

    private static void createDataStore(HashMap<String, CallFrequency> recordingFileNamesFreqency,
            String folderPathOut) {
        File folder = new File(folderPathOut);
        folder.mkdirs();
        for (String key : recordingFileNamesFreqency.keySet()) {
            String name = recordingFileNamesFreqency.get(key).name;
            for (int i = 0; i < recordingFileNamesFreqency.get(key).recordingMetadata.RecordingFile.size(); i++) {
                String recordingFile = recordingFileNamesFreqency.get(key).recordingMetadata.RecordingFile.toArray()[i]
                        .toString();
                String recordingDate = recordingFileNamesFreqency.get(key).recordingMetadata.RecordingDate.toArray()[i]
                        .toString();
                String recordingTime = recordingFileNamesFreqency.get(key).recordingMetadata.RecordingTime.toArray()[i]
                        .toString();
                String year = recordingDate.substring(0, 2);
                String month = recordingDate.substring(2, 4);
                String date = recordingDate.substring(4, 6);
                String hour = recordingTime.substring(0, 2);
                String newFolderPath = folderPathOut + "/" + name + "/" + year + "/" + month + "/"
                        + date + "/" + hour;
                File newFolder = new File(newFolderPath);
                newFolder.mkdirs();
                File file = new File(recordingFile);
                file.renameTo(new File(newFolderPath + "/" + file.getName()));
            }
        }
    }

    public static void main(String[] args) {
        // 2 arguments are expected. The first argument is the input folder path and the
        // second argument is the output folder path.
        if (args.length != 2) {
            System.out.println("Invalid number of arguments. Please provide the input and output folder paths.");
            return;
        }
        String folderPathIn = args[0];
        String folderPathOut = args[1];
        createData(folderPathIn, folderPathOut);
    }
}
