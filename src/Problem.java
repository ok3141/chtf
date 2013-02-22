import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Problem {

    protected FileInputStream in;
    protected PrintStream out;
    protected LogOut log;

    private boolean stopping;

    protected abstract void onSolve() throws IOException, ProblemStoppedException;

    protected char getPrefix() {
        return getClass().getSimpleName().charAt(0);
    }

    protected String getFileExtension() {
        return "in";
    }

    public String getInputFilename(int inputId) {
        return inputId + "." + getFileExtension();
    }

    public void solve(int inputId) throws IOException, ProblemStoppedException {
        String inputFilename = getInputFilename(inputId);
        String outputFilename = getPrefix() + Challenge.buildOutFilename(inputFilename);
        in = new FileInputStream(inputFilename);
        out = new PrintStream(outputFilename);
        log = new LogOut(System.out, out);

        try {
            stopping = false;
            onSolve();
        } finally {
            Utils.close(in);
            Utils.close(out);
            in = null;
            out = null;
        }
    }

    public void stop() {
        stopping = true;
    }

    protected final void checkStopping() throws ProblemStoppedException {
        if (stopping) {
            throw new ProblemStoppedException();
        }
    }

    private boolean isParam(Field param) {
        if (param == null) return false;
        return param.getAnnotation(Param.class) != null;
    }

    public final List<String> getParams() {
        ArrayList<String> result = new ArrayList<String>();

        Field[] params = getClass().getDeclaredFields();
        for (Field param : params) {
            if (isParam(param)) {
                result.add(param.getName());
            }
        }

        return Collections.unmodifiableList(result);
    }

    public final String getParam(String key) {
        try {
            Field param = getClass().getDeclaredField(key);
            if (!isParam(param)) {
                return null;
            }

            param.setAccessible(true);
            return String.valueOf(param.get(this));
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public final void setParam(String key, String value) {
        try {
            Field param = getClass().getDeclaredField(key);
            if (isParam(param)) {
                Object targetValue = null;

                Class<?> type = param.getType();

                if (type.isPrimitive()) {
                    type = ReflectUtils.getWrapperType(type);
                }

                if (type == Character.class) {
                    if (value != null && value.length() > 0) {
                        targetValue = Character.valueOf(value.charAt(0));
                    }
                } else if (type == String.class) {
                    targetValue = value;
                } else {
                    Method valueOf = type.getDeclaredMethod("valueOf", new Class[] { String.class });
                    if (valueOf != null) {
                        targetValue = valueOf.invoke(null, value);
                    }
                }

                param.setAccessible(true);
                param.set(this, targetValue);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
