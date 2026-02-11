package com.ardley.mcp.duffel.tools;

import com.ardley.mcp.core.McpTool;
import com.ardley.mcp.duffel.DuffelApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Get details of a specific flight offer.
 * GET /air/offers/{id}
 */
public class GetOfferTool implements McpTool {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final DuffelApiClient client;

    public GetOfferTool(DuffelApiClient client) { this.client = client; }

    @Override public String getName() { return "duffel_get_offer"; }
    @Override public String getDescription() {
        return "Get full details of a specific flight offer by its ID, including segments, pricing, and baggage.";
    }

    @Override public JsonNode getInputSchema() {
        ObjectNode schema = mapper.createObjectNode().put("type", "object");
        schema.putObject("properties").putObject("offer_id").put("type", "string")
                .put("description", "The Duffel offer ID (e.g. off_...)");
        schema.putArray("required").add("offer_id");
        return schema;
    }

    @Override public JsonNode execute(JsonNode arguments) throws Exception {
        String offerId = arguments.path("offer_id").asText();
        return client.get("/air/offers/" + offerId);
    }
}
