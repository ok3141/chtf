import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class P extends Problem {

    @Param
    private int adjust;
    @Param
    private float epsilon = 0.001f;
    @Param
    private long magic = 0x36523462;

    @Override
    protected String getFileExtension() {
        return "wav";
    }

    protected void onSolve() throws IOException, ProblemStoppedException {
        log.println(adjust);
        log.println(epsilon);
        log.println(magic);

        DataInputStream in = new DataInputStream(this.in);

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

        int d0 = (0x000000FF & in.readByte()) - 128;
        int d1 = (0x000000FF & in.readByte()) - 128;
        int d2 = (0x000000FF & in.readByte()) - 128;
        int beep = d0;

        Set<Long> freq = new HashSet<Long>();

        double sumSegm = 0.0;
        int countSegm = 0;

        for (int i = 3; i < count; ++i) {
            if (i % 100 == 0) {
                checkStopping();
            }

            if (d1 != beep || (d0 != 0 && d2 != 0)) {
                if (d1 == 0) {
                    d1 = 1;
                }

                int laplas = -(d0 - 2 * d1 + d2);
                double sqr = laplas / (double) d1;

                double f = Math.sqrt(sqr) * header.getSamplesPerSec() / (2 * Math.PI);
                if (!Double.isNaN(f)) {
                    sumSegm += f;
                    ++countSegm;
                }

            } else {
                if (countSegm > 0) {
                    long v = (((long) (sumSegm / countSegm)) - 1) / 100 + 1;
                    freq.add(v);
                    log.println(v + " > " + d0 + " " + d1 + " " + d2 + " : " + sumSegm + " " + countSegm);
                }

                sumSegm = 0.0;
                countSegm = 0;
            }

            d0 = d1;
            d1 = d2;
            d2 = (0x000000FF & in.readByte()) - 128;
        }

        if (count % 2 != 0) {
            in.skip(1);
        }

        in.close();

        log.resultln(freq.size());
        log.println(freq);
    }
}
