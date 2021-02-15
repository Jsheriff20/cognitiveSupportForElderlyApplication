package messaging.app;

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
}
