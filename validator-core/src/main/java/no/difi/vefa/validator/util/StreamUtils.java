package no.difi.vefa.validator.util;

import com.google.common.io.ByteStreams;
import no.difi.vefa.validator.api.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class StreamUtils {

    /**
     * Will read all from stream and reset it.
     * @param inputStream Input stream to read from (must support mark)
     * @return Read data
     * @throws IOException
     */
    public static byte[] readAllAndReset(InputStream inputStream) throws IOException {
        byte[] bytes = ByteStreams.toByteArray(inputStream);
        inputStream.reset();

        return bytes;
    }

    /**
     * Will read parts from stream and reset it.
     * @param inputStream A markable stream
     * @param length Read bytes
     * @return
     * @throws IOException
     */
    public static byte[] readAndReset(InputStream inputStream, int length) throws IOException {

        byte[] bytes = new byte[length];

        inputStream.mark(length);
        int numberOfReadBytes = inputStream.read(bytes);
        inputStream.reset();

        if (numberOfReadBytes == -1)
            throw new IOException("Empty file");

        bytes = Arrays.copyOfRange(bytes, 0, numberOfReadBytes);
        return bytes;
    }
}
