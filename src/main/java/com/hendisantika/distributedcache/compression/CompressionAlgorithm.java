package com.hendisantika.distributedcache.compression;

import io.vavr.CheckedFunction1;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.xerial.snappy.Snappy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-distributed-cache
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 12/31/23
 * Time: 06:46
 * To change this template use File | Settings | File Templates.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CompressionAlgorithm {
    GZIP(
            streamCompressor(GzipCompressorOutputStream::new),
            streamDecompressor(GzipCompressorInputStream::new)),
    SNAPPY(Snappy::compress, Snappy::uncompress);
    private final CheckedFunction1<byte[], byte[]> compressor;
    private final CheckedFunction1<byte[], byte[]> decompressor;

    public byte[] compress(byte[] data) {
        try {
            return compressor.apply(data);
        } catch (Throwable e) {
            throw new RuntimeException("Couldn't compress using " + name(), e);
        }
    }

    public byte[] decompress(byte[] data) {
        try {
            return decompressor.apply(data);
        } catch (Throwable e) {
            throw new RuntimeException("Couldn't compress using " + name(), e);
        }
    }

    private static CheckedFunction1<byte[], byte[]> streamCompressor(
            CheckedFunction1<OutputStream, OutputStream> newStream) {
        return data -> {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 OutputStream compressedStream = newStream.apply(outputStream)) {
                compressedStream.write(data);
                compressedStream.close();
                return outputStream.toByteArray();
            }
        };
    }

    private static CheckedFunction1<byte[], byte[]> streamDecompressor(
            CheckedFunction1<InputStream, InputStream> newStream) {
        return compressedData -> {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 InputStream compressedStream = newStream
                         .apply(new ByteArrayInputStream(compressedData))) {
                IOUtils.copy(compressedStream, outputStream);
                return outputStream.toByteArray();
            }
        };
    }
}
