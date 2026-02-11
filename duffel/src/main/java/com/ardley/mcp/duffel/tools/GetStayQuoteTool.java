package com.ardley.mcp.duffel.tools;

import com.ardley.mcp.core.McpTool;
import com.ardley.mcp.duffel.DuffelApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GetStayQuoteTool implements McpTool {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final DuffelApiClient client;

    public GetStayQuoteTool(DuffelApiClient client) { this.client = client; }

    @Override public String getName() { return "duffel_get_stay_quote"; }
    @Override public String getDescription() {
        return "Get a detailed pricing quote for a specific hotel stay option by its rate ID.";
    }

    @Override public JsonNode getInputSchema() {
        ObjectNode schema = mapper.createObjectNode().put("type", "object");
        schema.putObject("properties").putObject("rate_id").put("type", "string")
                .put("description", "The rate ID from search results to get a quote for");
        schema.putArray("required").add("rate_id");
        return schema;
    }

    @Override public JsonNode execute(JsonNode arguments) throws Exception {
        ObjectNode body = mapper.createObjectNode();
        body.putObject("data").put("rate_id", arguments.path("rate_id").asText());
        return client.post("/stays/quotes", body);
    }
}
