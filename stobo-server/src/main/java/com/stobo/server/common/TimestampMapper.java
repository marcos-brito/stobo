package com.stobo.server.common;

import java.time.Instant;

import com.google.protobuf.Timestamp;

public class TimestampMapper {
    public static Timestamp fromInstant(Instant instant) {
        return Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();
    }

    public static Instant toInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
