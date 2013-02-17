import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Challenge {

    private static final Pattern IN_FILE = Pattern.compile("(\\d+)\\.\\w+$");

    public static String buildOutFilename(String inFilename) {
        Matcher matcher = IN_FILE.matcher(inFilename);
        if (matcher.find()) {
            return matcher.group(1) + ".out";
        }

        throw new IllegalArgumentException(inFilename);
    }

}
