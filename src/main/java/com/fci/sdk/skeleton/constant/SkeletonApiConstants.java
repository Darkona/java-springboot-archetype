package com.fci.sdk.skeleton.constant;

/**
 * Constants class for Skeleton API configuration and standard values.
 * This class provides centralized constants for API-related configurations,
 * including MIME types and standard header names used across the application.
 * 
 * @author Gonzalo Barco
 *
 */
public class SkeletonApiConstants {
    
    /** Standard MIME type for JSON content in API responses */
    public static final String API_MIME_JSON = "application/json";
    
    /** Standard header name for tracking transaction IDs in API requests */
    public static final String API_HEADER_TRACK_ID = "trackId";
    
    /** Standard header name for parent transaction IDs in API requests */
    public static final String API_HEADER_PARENT_TRACK_ID = "parentTrackId";
}
