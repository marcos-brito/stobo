package com.stobo.server.common.proto;

public class Item {
    public static com.stobo.proto.types.Item from(long productId, int quantity) {
        return com.stobo.proto.types.Item.newBuilder().setProductId(Id.encode(productId))
                .setQuantity(quantity)
                .build();
    }

    public static com.stobo.proto.types.Item from(com.stobo.server.common.domain.Item item) {
        return com.stobo.proto.types.Item.newBuilder().setProductId(Id.encode(item.getProductId()))
                .setQuantity(item.getQuantity())
                .build();
    }
}
