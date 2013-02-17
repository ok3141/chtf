import java.io.Closeable;
import java.io.IOException;

public class Utils {
    public static void close(Closeable in) {
        try {
            in.close();
        } catch (IOException ex) {
            // silent
        }
    }
}
