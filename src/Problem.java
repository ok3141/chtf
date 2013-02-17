import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

public abstract class Problem {

    protected FileInputStream in;
    protected PrintStream out;
    protected LogOut log;

    protected abstract void onSolve() throws IOException;

    protected char getPrefix() {
        return getClass().getSimpleName().charAt(0);
    }

    public void solve(String inputFilename) throws IOException {
        String outputFilename = getPrefix() + Challenge.buildOutFilename(inputFilename);
        in = new FileInputStream(inputFilename);
        out = new PrintStream(outputFilename);
        log = new LogOut(System.out, out);

        try {
            onSolve();
        } finally {
            Utils.close(in);
            Utils.close(out);
            in = null;
            out = null;
        }
    }

}
