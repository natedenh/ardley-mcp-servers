package com.ardley.mcp.duffel.tools;

import com.ardley.mcp.core.McpTool;
import com.ardley.mcp.duffel.DuffelApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GetOrderTool implements McpTool {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final DuffelApiClient client;

    public GetOrderTool(DuffelApiClient client) { this.client = client; }

    @Override public String getName() { return "duffel_get_order"; }
    @Override public String getDescription() {
        return "Get booking details for an existing order by its ID.";
    }

    @Override public JsonNode getInputSchema() {
        ObjectNode schema = mapper.createObjectNode().put("type", "object");
        schema.putObject("properties").putObject("order_id").put("type", "string")
                .put("description", "The Duffel order ID (e.g. ord_...)");
        schema.putArray("required").add("order_id");
        return schema;
    }

    @Override public JsonNode execute(JsonNode arguments) throws Exception {
        return client.get("/air/orders/" + arguments.path("order_id").asText());
    }
}
