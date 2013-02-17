public class WavHeader {
    public static final int WAVE_FORMAT_PCM = 0x0001;
    public static final int WAVE_FORMAT_IEEE_FLOAT = 0x0003;
    public static final int WAVE_FORMAT_ALAW = 0x0006;
    public static final int WAVE_FORMAT_MULAW = 0x0007;
    public static final int WAVE_FORMAT_EXTENSIBLE = 0xFFFE;

    private int wavSize;
    private int formatSize;
    private short formatTag;
    private short channels;
    private int samplesPerSec;
    private int avgBytesPerSec;
    private short blockAlign;
    private short bitsPerSample;

    public int getWavSize() {
        return wavSize;
    }

    public int getFormatSize() {
        return formatSize;
    }

    public short getFormatTag() {
        return formatTag;
    }

    public short getChannels() {
        return channels;
    }

    public int getSamplesPerSec() {
        return samplesPerSec;
    }

    public int getAvgBytesPerSec() {
        return avgBytesPerSec;
    }

    public short getBlockAlign() {
        return blockAlign;
    }

    public short getBitsPerSample() {
        return bitsPerSample;
    }

    public WavHeader(int wavSize, int formatSize, short formatTag, short channels, int samplesPerSec, int avgBytesPerSec, short blockAlign, short bitsPerSample) {
        this.wavSize = wavSize;
        this.formatSize = formatSize;
        this.formatTag = formatTag;
        this.channels = channels;
        this.samplesPerSec = samplesPerSec;
        this.avgBytesPerSec = avgBytesPerSec;
        this.blockAlign = blockAlign;
        this.bitsPerSample = bitsPerSample;
    }

    @Override
    public String toString() {
        return "WavHeader [wavSize=" + wavSize + ", formatSize=" + formatSize + ", formatTag=" + formatTag + ", channels=" + channels + ", samplesPerSec=" + samplesPerSec + ", avgBytesPerSec=" + avgBytesPerSec + ", blockAlign="
                + blockAlign + ", bitsPerSample=" + bitsPerSample + "]";
    }

    public boolean hasFactChunk() {
        return formatTag != WAVE_FORMAT_PCM;
    }

}
