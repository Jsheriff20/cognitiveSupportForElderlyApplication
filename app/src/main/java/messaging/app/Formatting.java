package messaging.app;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Formatting {

    public String removeEndingSpaceFromString(String string) {
        int len = string.length();
        boolean whiteSpaceAtEnd = true;

        while (whiteSpaceAtEnd) {
            String lastChar = string.substring(len - 1);

            //check if the last string is a space
            if (lastChar.equals(" ")) {
                //if so remove the space
                string = string.substring(0, len - 1);
            } else {
                whiteSpaceAtEnd = false;
            }
        }

        return string;
    }


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
}
