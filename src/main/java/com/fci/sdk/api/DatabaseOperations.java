package com.fci.sdk.api;

import com.fci.sdk.skeleton.constant.SkeletonApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Interface defining standard database operations for storing and retrieving string data.
 * This interface provides a contract for different database implementations (MongoDB, PostgreSQL, Redis)
 * to ensure consistent API behavior across different data storage mechanisms.
 */
@Tag(name = "Database Operations", description = "Operations for storing and retrieving data from different databases")
public interface DatabaseOperations {

    @Operation(
        summary = "Store strings in database",
        description = "Stores a list of strings in the database"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Strings successfully stored",
            content = @Content(
                mediaType = SkeletonApiConstants.API_MIME_JSON,
                schema = @Schema(implementation = List.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = SkeletonApiConstants.API_MIME_JSON,
                schema = @Schema(implementation = String.class),
                examples = @ExampleObject(
                    value = "\"Error storing data: Connection refused\""
                )
            )
        )
    })
    @PostMapping("/store")
    ResponseEntity<List<String>> store(
        @Parameter(description = "List of strings to store", example = "[\"item1\", \"item2\", \"item3\"]")
        @RequestBody final List<String> data
    );

    @Operation(
        summary = "Retrieve strings from database",
        description = "Retrieves all strings from the database"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Strings successfully retrieved",
            content = @Content(
                mediaType = SkeletonApiConstants.API_MIME_JSON,
                schema = @Schema(implementation = List.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Data not found",
            content = @Content(
                mediaType = SkeletonApiConstants.API_MIME_JSON,
                schema = @Schema(implementation = String.class),
                examples = @ExampleObject(
                    value = "\"Data not found\""
                )
            )
        )
    })
    @GetMapping("/retrieve")
    ResponseEntity<List<String>> retrieve();
} 
