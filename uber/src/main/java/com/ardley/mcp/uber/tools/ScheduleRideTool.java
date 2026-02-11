package com.ardley.mcp.uber.tools;

import com.ardley.mcp.core.McpTool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Schedule a ride for a future time.
 * Uber API: POST /v1.2/requests (with pickup_time field)
 */
public class ScheduleRideTool implements McpTool {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getName() {
        return "uber_schedule_ride";
    }

    @Override
    public String getDescription() {
        return "Schedule an Uber ride for a future time. Requires pickup/dropoff and an ISO 8601 scheduled time.";
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
        props.putObject("product_id").put("type", "string").put("description", "Uber product ID");
        props.putObject("scheduled_time").put("type", "string").put("description", "ISO 8601 datetime for pickup (e.g. 2026-02-15T09:00:00-05:00)");
        ArrayNode required = schema.putArray("required");
        required.add("start_latitude").add("start_longitude").add("end_latitude").add("end_longitude").add("scheduled_time");
        return schema;
    }

    @Override
    public JsonNode execute(JsonNode arguments) throws Exception {
        String scheduledTime = arguments.path("scheduled_time").asText();
        // TODO: Call Uber API — POST https://api.uber.com/v1.2/requests with pickup_time
        ObjectNode result = mapper.createObjectNode();
        result.put("status", "placeholder");
        result.put("request_id", "placeholder-scheduled-id");
        result.put("scheduled_time", scheduledTime);
        result.put("message", "Would schedule ride for " + scheduledTime + " (Uber API not yet connected)");
        return result;
    }
}
