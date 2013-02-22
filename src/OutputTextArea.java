import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.swing.JTextArea;

public class OutputTextArea extends JTextArea {
    private static final long serialVersionUID = 1L;

    private PipedOutputStream pipeOutput;
    private PipedInputStream pipeInput;

    public OutputTextArea() {
        try {
            pipeOutput = new PipedOutputStream();
            pipeInput = new PipedInputStream(pipeOutput);

            new Thread(new Runnable() {
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(pipeInput));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            appendLine(line);
                        }
                    } catch (IOException ex) {
                        onIOException(ex);
                    }
                }
            }).start();
        } catch (IOException ex) {
            onIOException(ex);
        }
    }

    public void appendLine(final String line) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                append(line);
                append(System.getProperty("line.separator"));
            }
        });
    }

    public void write(int b) throws IOException {
        PipedOutputStream o = pipeOutput;
        if (o != null) {
            o.write(b);
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        PipedOutputStream o = pipeOutput;
        if (o != null) {
            o.write(b, off, len);
        }
    }

    public void write(byte[] b) throws IOException {
        PipedOutputStream o = pipeOutput;
        if (o != null) {
            o.write(b);
        }
    }

    private void onIOException(IOException ex) {
        Utils.close(pipeOutput);
        Utils.close(pipeInput);
        pipeOutput = null;
        pipeInput = null;

        appendLine(ex.toString());
    }
}
