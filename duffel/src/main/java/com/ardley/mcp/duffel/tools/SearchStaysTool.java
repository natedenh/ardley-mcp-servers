package com.ardley.mcp.duffel.tools;

import com.ardley.mcp.core.McpTool;
import com.ardley.mcp.duffel.DuffelApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Search for hotel/stay options.
 * POST /stays/search
 */
public class SearchStaysTool implements McpTool {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final DuffelApiClient client;

    public SearchStaysTool(DuffelApiClient client) { this.client = client; }

    @Override public String getName() { return "duffel_search_stays"; }
    @Override public String getDescription() {
        return "Search for hotel/accommodation options. Provide a location (city, airport code, or coordinates), dates, and guest count.";
    }

    @Override public JsonNode getInputSchema() {
        ObjectNode schema = mapper.createObjectNode().put("type", "object");
        ObjectNode props = schema.putObject("properties");
        props.putObject("location").put("type", "string")
                .put("description", "Location to search — can be a city name, IATA code, or 'lat,lng' coordinates");
        props.putObject("check_in").put("type", "string").put("description", "Check-in date (YYYY-MM-DD)");
        props.putObject("check_out").put("type", "string").put("description", "Check-out date (YYYY-MM-DD)");
        props.putObject("guests").put("type", "integer").put("description", "Number of guests (default 1)");
        props.putObject("rooms").put("type", "integer").put("description", "Number of rooms (default 1)");
        schema.putArray("required").add("location").add("check_in").add("check_out");
        return schema;
    }

    @Override public JsonNode execute(JsonNode arguments) throws Exception {
        String location = arguments.path("location").asText();
        String checkIn = arguments.path("check_in").asText();
        String checkOut = arguments.path("check_out").asText();
        int guests = arguments.path("guests").asInt(1);
        int rooms = arguments.path("rooms").asInt(1);

        ObjectNode body = mapper.createObjectNode();
        ObjectNode data = body.putObject("data");
        data.put("check_in_date", checkIn);
        data.put("check_out_date", checkOut);

        // Determine if location is coordinates or a place name
        if (location.matches("-?\\d+\\.\\d+,-?\\d+\\.\\d+")) {
            String[] parts = location.split(",");
            ObjectNode geo = data.putObject("location");
            ObjectNode geographic = geo.putObject("geographic_coordinates");
            geographic.put("latitude", Double.parseDouble(parts[0]));
            geographic.put("longitude", Double.parseDouble(parts[1]));
        } else {
            data.put("location", location);
        }

        data.put("guests", guests);
        data.put("rooms", rooms);

        return client.post("/stays/search", body);
    }
}
