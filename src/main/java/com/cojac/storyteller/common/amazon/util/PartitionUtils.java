package com.cojac.storyteller.common.amazon.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ChunkSize 만큼 List를 분할
 */
public class PartitionUtils {

    public static <T> Collection<List<T>> chunking(List<T> collection, int chunkSize) {
        return IntStream.range(0, collection.size()).boxed()
                .collect(Collectors.groupingBy(
                        idx -> idx / chunkSize,
                        Collectors.mapping(collection::get, Collectors.toList())
                ))
                .values();
    }
}
