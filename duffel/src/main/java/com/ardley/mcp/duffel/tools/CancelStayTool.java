package com.ardley.mcp.duffel.tools;

import com.ardley.mcp.core.McpTool;
import com.ardley.mcp.duffel.DuffelApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CancelStayTool implements McpTool {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final DuffelApiClient client;

    public CancelStayTool(DuffelApiClient client) { this.client = client; }

    @Override public String getName() { return "duffel_cancel_stay"; }
    @Override public String getDescription() {
        return "Cancel a hotel booking by its booking ID.";
    }

    @Override public JsonNode getInputSchema() {
        ObjectNode schema = mapper.createObjectNode().put("type", "object");
        schema.putObject("properties").putObject("booking_id").put("type", "string")
                .put("description", "The Duffel stays booking ID to cancel");
        schema.putArray("required").add("booking_id");
        return schema;
    }

    @Override public JsonNode execute(JsonNode arguments) throws Exception {
        String bookingId = arguments.path("booking_id").asText();
        return client.postEmpty("/stays/bookings/" + bookingId + "/actions/cancel");
    }
}
