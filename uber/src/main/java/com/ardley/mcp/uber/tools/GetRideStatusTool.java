package com.ardley.mcp.uber.tools;

import com.ardley.mcp.core.McpTool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Get the status of an existing ride.
 * Uber API: GET /v1.2/requests/{request_id}
 */
public class GetRideStatusTool implements McpTool {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getName() {
        return "uber_get_ride_status";
    }

    @Override
    public String getDescription() {
        return "Get the current status of an Uber ride by request ID.";
    }

    @Override
    public JsonNode getInputSchema() {
        ObjectNode schema = mapper.createObjectNode();
        schema.put("type", "object");
        ObjectNode props = schema.putObject("properties");
        props.putObject("ride_id").put("type", "string").put("description", "The ride request ID");
        schema.putArray("required").add("ride_id");
        return schema;
    }

    @Override
    public JsonNode execute(JsonNode arguments) throws Exception {
        String rideId = arguments.path("ride_id").asText();
        // TODO: Call Uber API — GET https://api.uber.com/v1.2/requests/{rideId}
        ObjectNode result = mapper.createObjectNode();
        result.put("status", "placeholder");
        result.put("request_id", rideId);
        result.put("ride_status", "processing");
        result.put("message", "Would fetch ride status (Uber API not yet connected)");
        return result;
    }
}
