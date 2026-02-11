package com.ardley.mcp.uber.tools;

import com.ardley.mcp.core.McpTool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Request an Uber ride.
 * Uber API: POST /v1.2/requests
 */
public class RequestRideTool implements McpTool {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getName() {
        return "uber_request_ride";
    }

    @Override
    public String getDescription() {
        return "Request an Uber ride. Requires pickup/dropoff coordinates and a product ID (from price estimates).";
    }

    @Override
    public JsonNode getInputSchema() {
        ObjectNode schema = mapper.createObjectNode();
        schema.put("type", "object");
        ObjectNode props = schema.putObject("properties");
        props.putObject("start_latitude").put("type", "number").put("description", "Pickup latitude");
        props.putObject("start_longitude").put("type", "number").put("description", "Pickup longitude");
        props.putObject("end_latitude").put("type", "number").put("description", "Dropoff latitude");
        props.putObject("end_longitude").put("type", "number").put("description", "Dropoff longitude");
        props.putObject("product_id").put("type", "string").put("description", "Uber product ID (e.g. UberX ID from price estimates)");
        ArrayNode required = schema.putArray("required");
        required.add("start_latitude").add("start_longitude").add("end_latitude").add("end_longitude").add("product_id");
        return schema;
    }

    @Override
    public JsonNode execute(JsonNode arguments) throws Exception {
        // TODO: Call Uber API — POST https://api.uber.com/v1.2/requests
        ObjectNode result = mapper.createObjectNode();
        result.put("status", "placeholder");
        result.put("request_id", "placeholder-request-id");
        result.put("product_id", arguments.path("product_id").asText());
        result.put("message", "Would request a ride (Uber API not yet connected)");
        result.put("ride_status", "processing");
        return result;
    }
}
