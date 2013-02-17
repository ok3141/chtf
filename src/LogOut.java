import java.io.PrintStream;

public final class LogOut {

    private final PrintStream log;
    private final PrintStream out;

    public LogOut(PrintStream log, PrintStream out) {
        this.log = log;
        this.out = out;
    }

    public void print(boolean b) {
        log.print(b);
    }

    public void print(char c) {
        log.print(c);
    }

    public void print(char[] s) {
        log.print(s);
    }

    public void print(double d) {
        log.print(d);
    }

    public void print(float f) {
        log.print(f);
    }

    public void print(int i) {
        log.print(i);
    }

    public void print(long l) {
        log.print(l);
    }

    public void print(Object obj) {
        log.print(obj);
    }

    public void print(String s) {
        log.print(s);
    }

    public void println(boolean b) {
        log.println(b);
    }

    public void println(char c) {
        log.println(c);
    }

    public void println(char[] s) {
        log.println(s);
    }

    public void println(double d) {
        log.println(d);
    }

    public void println(float f) {
        log.println(f);
    }

    public void println(int i) {
        log.println(i);
    }

    public void println(long l) {
        log.println(l);
    }

    public void println(Object obj) {
        log.println(obj);
    }

    public void println(String s) {
        log.println(s);
    }

    public void result(boolean b) {
        print(b);
        out.print(b);
    }

    public void result(char c) {
        print(c);
        out.print(c);
    }

    public void result(char[] s) {
        print(s);
        out.print(s);
    }

    public void result(double d) {
        print(d);
        out.print(d);
    }

    public void result(float f) {
        print(f);
        out.print(f);
    }

    public void result(int i) {
        print(i);
        out.print(i);
    }

    public void result(long l) {
        print(l);
        out.print(l);
    }

    public void result(Object obj) {
        print(obj);
        out.print(obj);
    }

    public void result(String s) {
        print(s);
        out.print(s);
    }

    public void resultln(boolean b) {
        println(b);
        out.println(b);
    }

    public void resultln(char c) {
        println(c);
        out.println(c);
    }

    public void resultln(char[] s) {
        println(s);
        out.println(s);
    }

    public void resultln(double d) {
        println(d);
        out.println(d);
    }

    public void resultln(float f) {
        println(f);
        out.println(f);
    }

    public void resultln(int i) {
        println(i);
        out.println(i);
    }

    public void resultln(long l) {
        println(l);
        out.println(l);
    }

    public void resultln(Object obj) {
        println(obj);
        out.println(obj);
    }

    public void resultln(String s) {
        println(s);
        out.println(s);
    }
}
