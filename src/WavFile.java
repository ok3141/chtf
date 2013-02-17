import java.io.DataInputStream;
import java.io.IOException;

public class WavFile {

    public static WavHeader read(DataInputStream dis) throws IOException {
        final byte[] buf4 = new byte[4];

        if (4 != dis.read(buf4)) {
            throw new IllegalStateException();
        }

        if (!"RIFF".equals(new String(buf4))) {
            throw new IllegalStateException();
        }

        int wavSize = ByteSwapper.swap(dis.readInt());

        if (4 != dis.read(buf4)) {
            throw new IllegalStateException();
        }

        if (!"WAVE".equals(new String(buf4))) {
            throw new IllegalStateException();
        }

        if (4 != dis.read(buf4)) {
            throw new IllegalStateException();
        }

        if (!"fmt ".equals(new String(buf4))) {
            throw new IllegalStateException();
        }

        int formatSize = ByteSwapper.swap(dis.readInt());

        if (formatSize < 16) {
            throw new IllegalStateException();
        }

        short formatTag = ByteSwapper.swap(dis.readShort());

        short channels = ByteSwapper.swap(dis.readShort());

        int samplesPerSec = ByteSwapper.swap(dis.readInt());

        int avgBytesPerSec = ByteSwapper.swap(dis.readInt());

        short blockAlign = ByteSwapper.swap(dis.readShort());

        short bitsPerSample = ByteSwapper.swap(dis.readShort());

        if (formatSize >= 18) {
            dis.skip(formatSize - 16);
        }

        return new WavHeader(wavSize, formatSize, formatTag, channels, samplesPerSec, avgBytesPerSec, blockAlign, bitsPerSample);
    }

    public static int readData(DataInputStream dis) throws IOException {
        final byte[] buf4 = new byte[4];

        if (4 != dis.read(buf4)) {
            throw new IllegalStateException();
        }

        if (!"data".equals(new String(buf4))) {
            throw new IllegalStateException();
        }

        return ByteSwapper.swap(dis.readInt());
    }
}
