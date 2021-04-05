package messaging.app;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class Formatting {


    public String howLongAgo(String messageSentUNIXTime){
        Date now = new Date();
        long currentUnixTime = now.getTime() / 1000L;


        long messageSentUnixTime = Long.parseLong(messageSentUNIXTime);
        long unixTimeAgo = currentUnixTime - messageSentUnixTime;
        String formattedTimeAgo;

        //if time is less than 1 minute
        if(unixTimeAgo < 60)
        {
            formattedTimeAgo = unixTimeAgo + " Seconds";
        }
        //if time is more than 1 minute and less than 1 hour
        else if(unixTimeAgo >= 60 && unixTimeAgo < 3600)
        {
            formattedTimeAgo = Math.round(unixTimeAgo / 60) + " Minutes";
        }
        //if time is more than 1 hour and less than 1 day
        else if(unixTimeAgo >= 3600 && unixTimeAgo < 86400)
        {
            formattedTimeAgo = Math.round(unixTimeAgo / 3600) + " Hours";
        }
        //if time is more than 1 day and less than 1 week
        else if(unixTimeAgo >= 86400 && unixTimeAgo < 604800)
        {
            formattedTimeAgo = Math.round(unixTimeAgo / 86400) + " Days";
        }
        //if time is more than 1 week and less than 1 Month
        else if(unixTimeAgo >= 604800 && unixTimeAgo < 2629743)
        {
            formattedTimeAgo = Math.round(unixTimeAgo / 604800) + " Weeks";
        }
        //if time is more than 1 Month
        else
        {
            formattedTimeAgo = Math.round(unixTimeAgo / 604800) + " Months";
        }

        return formattedTimeAgo;
    }

    public String getFileNameFromUrl(String Url){

        String editedUrl;

        String[] editedUrlArray = Url.split("&");
        editedUrlArray = editedUrlArray[0].split("%2F");
        editedUrl = editedUrlArray[1];

        return editedUrl;
    }

    public String removeProfanity(String checkingString){
        //a select few have been chosen
        //TODO
        //Add more profanity words before release
        List<String> profanityWords = Arrays.asList("fuck", "shit", "bastard", "bitch", "fucker", "fucking", "shitting"); // suppose these words are offensive

        for (String word : profanityWords) {
            Pattern rx = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE);
            checkingString = rx.matcher(checkingString).replaceAll(new String(new char[word.length()]).replace('\0', '*'));
        }

        return checkingString;
    }


    public List<HashMap<String, String>> orderReceivedMediaDetails(List<HashMap<String, String>> receivedMediaDetails) {

        HashMap<Long, HashMap<String, String>> unorderedMap = new HashMap<>();
        List<HashMap<String, String>> sortedList = new ArrayList<>();

        for (Map<String, String> kvPair : receivedMediaDetails) {
            Long timestamp = Long.valueOf(kvPair.get("lastMessageTimeStamp"));
            unorderedMap.put(timestamp, (HashMap<String, String>) kvPair);
        }

        // TreeMap to store values of HashMap
        TreeMap<Long, Map<String, String>> sorted = new TreeMap<>();

        // Copy all data from hashMap into TreeMap
        sorted.putAll(unorderedMap);

        // Display the TreeMap which is naturally sorted
        for (Map.Entry<Long, Map<String, String>> entry : sorted.entrySet()) {
            sortedList.add(0, (HashMap<String, String>) entry.getValue());
        }

        return sortedList;
    }


    public double getPercentageChange(long startNumber, long finalNumber ){
        double percentageChange = 100 * ((finalNumber - startNumber) / (double)startNumber);
        return percentageChange;
    }


    public double getPercentageChangeOfHighScores(HashMap<String, List<Long>> highScores, String gameMode){

        List<Long> gridPast5HighScores = highScores.get(gameMode);
        long startHighScore = gridPast5HighScores.get(0);
        long totalScores = 0;
        for(long highScore : gridPast5HighScores){
            totalScores += highScore;
        }

        long averageHighScore = totalScores / gridPast5HighScores.size();

        Log.d("test", "highScorePercentageChange: " + getPercentageChange(startHighScore, averageHighScore));
        return getPercentageChange(startHighScore, averageHighScore);
    }


}
