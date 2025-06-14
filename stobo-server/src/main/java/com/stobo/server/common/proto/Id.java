package com.stobo.server.common.proto;

import java.util.Base64;
import java.util.Optional;

public class Id {
    private long sequentialId;

    public Id(com.stobo.proto.types.Id id) {
        this.sequentialId = id.getSeqId();
    }

    public Id(long id) {
        this.sequentialId = id;
    }

    public String encode() {
        return encode(this);
    }

    public static String encode(Id id) {
        byte[] bytes = com.stobo.proto.types.Id.newBuilder().setSeqId(id.getSequentialId()).build().toByteArray();

        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    public static String encode(long sequentialId) {
        return encode(new Id(sequentialId));
    }

    public static Optional<Id> decode(String id) {
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(id);
            Id opaqueId = new Id(com.stobo.proto.types.Id.parseFrom(bytes));

            return Optional.of(opaqueId);
        } catch (Exception _) {
            return Optional.empty();
        }
    }

    public static Optional<Long> decodeLong(String id) {
        return decode(id).map((opaqueId) -> opaqueId.getSequentialId());
    }

    public long getSequentialId() {
        return sequentialId;
    }
}
