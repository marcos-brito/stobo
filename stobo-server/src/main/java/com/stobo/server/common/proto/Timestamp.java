package com.stobo.server.common.proto;

import java.time.Instant;

public class Timestamp {
    public static com.google.protobuf.Timestamp fromInstant(Instant instant) {
        return com.google.protobuf.Timestamp.newBuilder().setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano()).build();
    }

    public static Instant toInstant(com.google.protobuf.Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
