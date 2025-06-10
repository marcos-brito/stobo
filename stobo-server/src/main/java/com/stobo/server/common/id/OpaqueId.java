package com.stobo.server.common.id;

import com.stobo.proto.id.Id;
import java.util.Base64;
import java.util.Optional;

public class OpaqueId {
  private long sequentialId;

  public OpaqueId(Id id) { this.sequentialId = id.getSeqId(); }

  public OpaqueId(long id) { this.sequentialId = id; }

  public String encode() { return encode(this); }

  public static String encode(OpaqueId id) {
    byte[] bytes =
        Id.newBuilder().setSeqId(id.getSequentialId()).build().toByteArray();

    return Base64.getUrlEncoder().encodeToString(bytes);
  }

  public static String encode(long sequentialId) {
    return encode(new OpaqueId(sequentialId));
  }

  public static Optional<OpaqueId> decode(String id) {
    try {
      byte[] bytes = Base64.getUrlDecoder().decode(id);
      OpaqueId opaqueId = new OpaqueId(Id.parseFrom(bytes));

      return Optional.of(opaqueId);
    } catch (Exception _) {
      return Optional.empty();
    }
  }

  public static Optional<Long> decodeLong(String id) {
    return decode(id).map((opaqueId) -> opaqueId.getSequentialId());
  }

  public long getSequentialId() { return sequentialId; }
}
