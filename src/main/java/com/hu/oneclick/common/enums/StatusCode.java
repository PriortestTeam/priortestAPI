package com.hu.oneclick.common.enums;

import lombok.Getter;

@Getter
public enum StatusCode {
    INVALID((byte) 0), PASS((byte) 1), FAIL((byte) 2), SKIP((byte) 3), BLOCKED((byte) 4), NO_RUN((byte) 5), NOT_COMPLETED((byte) 6);

    private final byte value;

    StatusCode(byte value) {
        this.value = value;
    }

}
