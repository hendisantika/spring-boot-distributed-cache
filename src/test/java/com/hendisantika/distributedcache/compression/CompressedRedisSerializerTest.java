package com.hendisantika.distributedcache.compression;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class CompressedRedisSerializerTest {

    @ParameterizedTest
    @EnumSource(CompressionAlgorithm.class)
    void serializesAndDeserializesBackToTheOriginalBytes(CompressionAlgorithm algorithm) {
        CompressedRedisSerializer serializer = new CompressedRedisSerializer(algorithm);
        byte[] original = """
                {"id":42,"name":"Kopi Gayo","price":19.99}""".getBytes(StandardCharsets.UTF_8);

        assertThat(serializer.deserialize(serializer.serialize(original))).isEqualTo(original);
    }

    @ParameterizedTest
    @EnumSource(CompressionAlgorithm.class)
    void storesSomethingOtherThanThePlainBytes(CompressionAlgorithm algorithm) {
        CompressedRedisSerializer serializer = new CompressedRedisSerializer(algorithm);
        byte[] original = "hendisantika".repeat(100).getBytes(StandardCharsets.UTF_8);

        assertThat(serializer.serialize(original)).isNotEqualTo(original);
    }

    @Test
    void passesNullThroughUntouched() {
        CompressedRedisSerializer serializer = new CompressedRedisSerializer(CompressionAlgorithm.SNAPPY);

        assertThat(serializer.serialize(null)).isNull();
        assertThat(serializer.deserialize(null)).isNull();
    }
}
