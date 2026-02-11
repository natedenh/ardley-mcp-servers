package com.ardley.mcp.duffel.tools;

import com.ardley.mcp.core.McpTool;
import com.ardley.mcp.duffel.DuffelApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Search for flight offers via Duffel.
 * POST /air/offer_requests
 */
public class SearchFlightsTool implements McpTool {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final DuffelApiClient client;

    public SearchFlightsTool(DuffelApiClient client) {
        this.client = client;
    }

    @Override public String getName() { return "duffel_search_flights"; }

    @Override public String getDescription() {
        return "Search for flight offers. Returns available flights with pricing. " +
               "Specify origin/destination as IATA codes (e.g. JFK, LHR).";
    }

    @Override public JsonNode getInputSchema() {
        ObjectNode schema = mapper.createObjectNode().put("type", "object");
        ObjectNode props = schema.putObject("properties");
        props.putObject("origin").put("type", "string").put("description", "Origin IATA airport code (e.g. JFK)");
        props.putObject("destination").put("type", "string").put("description", "Destination IATA airport code (e.g. LHR)");
        props.putObject("departure_date").put("type", "string").put("description", "Departure date (YYYY-MM-DD)");
        props.putObject("return_date").put("type", "string").put("description", "Return date (YYYY-MM-DD) for round trip. Omit for one-way.");
        props.putObject("passengers").put("type", "integer").put("description", "Number of adult passengers (default 1)");
        props.putObject("cabin_class").put("type", "string").put("description", "Cabin class: economy, premium_economy, business, first (default economy)");
        schema.putArray("required").add("origin").add("destination").add("departure_date");
        return schema;
    }

    @Override public JsonNode execute(JsonNode arguments) throws Exception {
        String origin = arguments.path("origin").asText();
        String destination = arguments.path("destination").asText();
        String departureDate = arguments.path("departure_date").asText();
        String returnDate = arguments.path("return_date").asText(null);
        int passengers = arguments.path("passengers").asInt(1);
        String cabinClass = arguments.path("cabin_class").asText("economy");

        ObjectNode body = mapper.createObjectNode();
        ObjectNode data = body.putObject("data");

        // Build slices
        ArrayNode slices = data.putArray("slices");
        ObjectNode outbound = slices.addObject();
        outbound.put("origin", origin);
        outbound.put("destination", destination);
        outbound.put("departure_date", departureDate);

        if (returnDate != null && !returnDate.isEmpty()) {
            ObjectNode returnSlice = slices.addObject();
            returnSlice.put("origin", destination);
            returnSlice.put("destination", origin);
            returnSlice.put("departure_date", returnDate);
        }

        // Build passengers
        ArrayNode pax = data.putArray("passengers");
        for (int i = 0; i < passengers; i++) {
            pax.addObject().put("type", "adult");
        }

        data.put("cabin_class", cabinClass);

        return client.post("/air/offer_requests", body);
    }
}
