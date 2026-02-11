package com.ardley.mcp.uber.tools;

import com.ardley.mcp.core.McpTool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Cancel an existing ride.
 * Uber API: DELETE /v1.2/requests/{request_id}
 */
public class CancelRideTool implements McpTool {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getName() {
        return "uber_cancel_ride";
    }

    @Override
    public String getDescription() {
        return "Cancel an active Uber ride by request ID.";
    }

    @Override
    public JsonNode getInputSchema() {
        ObjectNode schema = mapper.createObjectNode();
        schema.put("type", "object");
        ObjectNode props = schema.putObject("properties");
        props.putObject("ride_id").put("type", "string").put("description", "The ride request ID to cancel");
        schema.putArray("required").add("ride_id");
        return schema;
    }

    @Override
    public JsonNode execute(JsonNode arguments) throws Exception {
        String rideId = arguments.path("ride_id").asText();
        // TODO: Call Uber API — DELETE https://api.uber.com/v1.2/requests/{rideId}
        ObjectNode result = mapper.createObjectNode();
        result.put("status", "placeholder");
        result.put("request_id", rideId);
        result.put("cancelled", true);
        result.put("message", "Would cancel ride (Uber API not yet connected)");
        return result;
    }
}
