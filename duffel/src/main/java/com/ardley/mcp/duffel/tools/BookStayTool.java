package com.ardley.mcp.duffel.tools;

import com.ardley.mcp.core.McpTool;
import com.ardley.mcp.duffel.DuffelApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BookStayTool implements McpTool {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final DuffelApiClient client;

    public BookStayTool(DuffelApiClient client) { this.client = client; }

    @Override public String getName() { return "duffel_book_stay"; }
    @Override public String getDescription() {
        return "Book a hotel stay using a quote ID. Requires guest details (name, email, phone).";
    }

    @Override public JsonNode getInputSchema() {
        ObjectNode schema = mapper.createObjectNode().put("type", "object");
        ObjectNode props = schema.putObject("properties");
        props.putObject("quote_id").put("type", "string").put("description", "The quote ID from get_stay_quote");
        props.putObject("given_name").put("type", "string").put("description", "Guest first name");
        props.putObject("family_name").put("type", "string").put("description", "Guest last name");
        props.putObject("email").put("type", "string").put("description", "Guest email address");
        props.putObject("phone_number").put("type", "string").put("description", "Guest phone with country code");
        schema.putArray("required").add("quote_id").add("given_name").add("family_name").add("email").add("phone_number");
        return schema;
    }

    @Override public JsonNode execute(JsonNode arguments) throws Exception {
        ObjectNode body = mapper.createObjectNode();
        ObjectNode data = body.putObject("data");
        data.put("quote_id", arguments.path("quote_id").asText());

        ArrayNode guests = data.putArray("guests");
        ObjectNode guest = guests.addObject();
        guest.put("given_name", arguments.path("given_name").asText());
        guest.put("family_name", arguments.path("family_name").asText());

        data.put("email", arguments.path("email").asText());
        data.put("phone_number", arguments.path("phone_number").asText());

        return client.post("/stays/bookings", body);
    }
}
