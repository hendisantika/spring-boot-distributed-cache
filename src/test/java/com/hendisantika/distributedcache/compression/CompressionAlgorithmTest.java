package com.hendisantika.distributedcache.compression;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompressionAlgorithmTest {

    @ParameterizedTest
    @EnumSource(CompressionAlgorithm.class)
    void roundTripsPayload(CompressionAlgorithm algorithm) {
        byte[] original = """
                {"id":42,"name":"Kopi Gayo","price":19.99}""".getBytes(StandardCharsets.UTF_8);

        assertThat(algorithm.decompress(algorithm.compress(original))).isEqualTo(original);
    }

    @ParameterizedTest
    @EnumSource(CompressionAlgorithm.class)
    void roundTripsEmptyPayload(CompressionAlgorithm algorithm) {
        byte[] empty = new byte[0];

        assertThat(algorithm.decompress(algorithm.compress(empty))).isEqualTo(empty);
    }

    @ParameterizedTest
    @EnumSource(CompressionAlgorithm.class)
    void shrinksHighlyRepetitivePayload(CompressionAlgorithm algorithm) {
        byte[] repetitive = "hendisantika".repeat(500).getBytes(StandardCharsets.UTF_8);

        assertThat(algorithm.compress(repetitive).length).isLessThan(repetitive.length);
    }

    @Test
    void reportsTheAlgorithmThatFailedToDecompress() {
        byte[] notCompressed = "definitely not gzip".getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> CompressionAlgorithm.GZIP.decompress(notCompressed))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("GZIP");
    }

    @Test
    void producesDistinctEncodingsPerAlgorithm() {
        byte[] original = "hendisantika".repeat(50).getBytes(StandardCharsets.UTF_8);

        assertThat(CompressionAlgorithm.GZIP.compress(original))
                .isNotEqualTo(CompressionAlgorithm.SNAPPY.compress(original));
    }
}
