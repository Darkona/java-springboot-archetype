package com.fci.sdk.constant;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the different types of FCI contracts.
 * Maps to string values when serialized to JSON.
 */
public enum FciContractEnum {

    FCI_INIT("fci_init"),
    FCI_PAYLOAD("fci_payload"),
    FCI_END("fci_end");

    /** The string value representing this contract type. */
    private final String value;

    FciContractEnum(final String value) {
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
    public static FciContractEnum fromValue(final String value) {
        for (final FciContractEnum contract : values()) {
            if (contract.value.equals(value)) {
                return contract;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
} 
