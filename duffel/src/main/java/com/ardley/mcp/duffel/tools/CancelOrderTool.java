package com.ardley.mcp.duffel.tools;

import com.ardley.mcp.core.McpTool;
import com.ardley.mcp.duffel.DuffelApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CancelOrderTool implements McpTool {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final DuffelApiClient client;

    public CancelOrderTool(DuffelApiClient client) { this.client = client; }

    @Override public String getName() { return "duffel_cancel_order"; }
    @Override public String getDescription() {
        return "Cancel a flight booking by order ID. This will attempt to cancel and refund the order.";
    }

    @Override public JsonNode getInputSchema() {
        ObjectNode schema = mapper.createObjectNode().put("type", "object");
        schema.putObject("properties").putObject("order_id").put("type", "string")
                .put("description", "The Duffel order ID to cancel");
        schema.putArray("required").add("order_id");
        return schema;
    }

    @Override public JsonNode execute(JsonNode arguments) throws Exception {
        String orderId = arguments.path("order_id").asText();
        return client.postEmpty("/air/orders/" + orderId + "/actions/cancel");
    }
}
