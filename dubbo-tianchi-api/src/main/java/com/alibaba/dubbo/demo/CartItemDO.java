package com.alibaba.dubbo.demo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CartItemDO implements Serializable {
    private static final long serialVersionUID = -3291877592429392571L;
    private String id = "myId";
    private long cartId = Long.MAX_VALUE;
    private long userId= Long.MAX_VALUE;
    private String trackId ="myTrackId";
    private long itemId= Long.MAX_VALUE;
    private long skuId= Long.MAX_VALUE;
    private int quantity = Integer.MAX_VALUE;
    private int mainType = Integer.MAX_VALUE;
    private long tpId= Long.MAX_VALUE;
    private long subType = Long.MAX_VALUE;
    public long cityCode= Long.MAX_VALUE;
    private Map<String, String> attributes = new HashMap();

    public CartItemDO() {
    }

    public int getMainType() {
        return this.mainType;
    }

    public void setMainType(int mainType) {
        this.mainType = mainType;
    }

    public long getSubType() {
        return this.subType;
    }

    public void setSubType(long subType) {
        this.subType = subType;
    }

    public long getCartId() {
        return this.cartId;
    }

    public void setCartId(long cartId) {
        this.cartId = cartId;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getItemId() {
        return this.itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getSkuId() {
        return this.skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public String getTrackId() {
        return this.trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTpId() {
        return this.tpId;
    }

    public void setTpId(long tpId) {
        this.tpId = tpId;
    }

    public int hashCode() {
        boolean prime = true;
        byte result = 1;
        int result1 = 31 * result + (int) (this.cartId ^ this.cartId >>> 32);
        result1 = 31 * result1 + (int) (this.itemId ^ this.itemId >>> 32);
        result1 = 31 * result1 + this.quantity;
        result1 = 31 * result1 + (int) (this.skuId ^ this.skuId >>> 32);
        result1 = 31 * result1 + (int) (this.userId ^ this.userId >>> 32);
        return result1;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            CartItemDO other = (CartItemDO) obj;
            return this.cartId != other.cartId ? false : (this.itemId != other.itemId ? false : (this.quantity != other.quantity ? false : (this.skuId != other.skuId ? false : this.userId == other.userId)));
        }
    }

    public String toString() {
        return "com.taobao.hsf.CartItemDO [cartId=" + this.cartId + ", userId=" + this.userId + ", itemId=" + this.itemId + ", skuId=" + this.skuId + ", quantity=" + this.quantity + "]";
    }
}

