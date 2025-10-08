package com.fci.sdk.skeleton.constant;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the different Skeleton supported channels
 * Maps to string values when serialized to JSON.
 */
public enum SkeletonChannelEnum {
    MOBILE("mobile"),
    MOBILE_WEB("mobile_web"),
    CONVERSATIONAL("conversational"),
    WEB("web"),
    SELF_SERVICE("self_ßservice"),
    DESKTOP("desktop"),
    API("api");

    /** The string value representing this contract type. */
    private final String value;

    SkeletonChannelEnum(final String value) {
        this.value = value;
    }
    /**
     * Returns the string value for JSON serialization.
     * @return The string representation of the contract type
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Converts a string value to the corresponding enum.
     * @param value The string value to convert
     * @return The corresponding FciContractEnum, or null if not found
     */
    public static SkeletonChannelEnum fromValue(final String value) {
        for (final SkeletonChannelEnum channel : values()) {
            if (channel.value.equals(value)) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
