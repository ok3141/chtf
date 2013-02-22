import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;

public class P1 {
    private static final PrintStream log = System.out;
    private static final PrintStream err = System.err;
    private static final int MUTE = 128;
    private static final boolean LOG_FILE = false;

    public static void main(String[] args) throws Throwable {
        final PrintStream logFile;
        if (LOG_FILE) {
            logFile = new PrintStream("log.p" + Challenge.buildOutFilename(args[0]));
        } else {
            logFile = new PrintStream(new OutputStream() {
                public void write(int arg0) throws IOException {
                    // silent
                }
            });
        }

        PrintStream out = new PrintStream("p" + Challenge.buildOutFilename(args[0]));
        DataInputStream in = new DataInputStream(new FileInputStream(args[0]));

        WavHeader header = WavFile.read(in);
        if (header.hasFactChunk()) {
            throw new IllegalStateException();
        }

        int sampleSize = header.getBlockAlign();
        if (sampleSize != 1) {
            throw new IllegalStateException();
        }

        int samplesSize = WavFile.readData(in);
        int count = samplesSize / sampleSize;

        if (count < 3) {
            throw new IllegalStateException();
        }

        int d0 = 0x000000FF & in.readByte();
        int d1 = 0x000000FF & in.readByte();
        int d2 = 0x000000FF & in.readByte();

        logFile.println(d0 + " " + d1 + " " + d2);

        Set<Long> freq = new TreeSet<Long>();

        int switchSegm = 0;
        int countSegm = 0;
        double rate = header.getSamplesPerSec();

        for (int i = 3; i < count; ++i) {
            if (d0 == MUTE && d0 == d1 && d1 == d2) {
                if (countSegm > 0) {
                    double f = (switchSegm / (countSegm / rate)) / 2.0;
                    long v = ((long) Math.ceil(f) - 1L) / 100L + 1L;
                    v *= 100;
                    freq.add(v);
                    log.println(v + " > " + /* d0 + " " + d1 + " " + d2 + " : " + */switchSegm + " " + countSegm + " | " + f);
                }

                switchSegm = 0;
                countSegm = 0;
            } else {
                if ((d0 - MUTE) * (d1 - MUTE) < 0) {
                    ++switchSegm;
                }

                ++countSegm;
            }

            d0 = d1;
            d1 = d2;
            d2 = 0x000000FF & in.readByte();

            logFile.println(d0 + " " + d1 + " " + d2);
        }

        if (count % 2 != 0) {
            in.skip(1);
        }

        in.close();

        out.println(freq.size());
        log.println(freq.size());
        log.println(freq);
    }
}
