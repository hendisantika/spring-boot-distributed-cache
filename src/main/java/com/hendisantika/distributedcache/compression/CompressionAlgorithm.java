package com.hendisantika.distributedcache.compression;

import io.vavr.CheckedFunction1;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.xerial.snappy.Snappy;

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
}
