package com.ardley.mcp.duffel.tools;

import com.ardley.mcp.core.McpTool;
import com.ardley.mcp.duffel.DuffelApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Book a flight by creating an order.
 * POST /air/orders
 */
public class CreateOrderTool implements McpTool {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final DuffelApiClient client;

    public CreateOrderTool(DuffelApiClient client) { this.client = client; }

    @Override public String getName() { return "duffel_create_order"; }
    @Override public String getDescription() {
        return "Book a flight by creating an order from an offer. Requires passenger details " +
               "(name, email, phone, date_of_birth, gender) and optionally loyalty programme accounts.";
    }

    @Override public JsonNode getInputSchema() {
        ObjectNode schema = mapper.createObjectNode().put("type", "object");
        ObjectNode props = schema.putObject("properties");
        props.putObject("offer_id").put("type", "string").put("description", "The offer ID to book");

        ObjectNode passengersSchema = props.putObject("passengers");
        passengersSchema.put("type", "array").put("description", "List of passenger details");
        ObjectNode passengerItem = passengersSchema.putObject("items").put("type", "object");
        ObjectNode pProps = passengerItem.putObject("properties");
        pProps.putObject("id").put("type", "string").put("description", "Passenger ID from the offer");
        pProps.putObject("given_name").put("type", "string").put("description", "First name");
        pProps.putObject("family_name").put("type", "string").put("description", "Last name");
        pProps.putObject("email").put("type", "string").put("description", "Email address");
        pProps.putObject("phone_number").put("type", "string").put("description", "Phone number with country code (e.g. +12025551234)");
        pProps.putObject("born_on").put("type", "string").put("description", "Date of birth (YYYY-MM-DD)");
        pProps.putObject("gender").put("type", "string").put("description", "Gender: m or f");
        pProps.putObject("title").put("type", "string").put("description", "Title: mr, ms, mrs, miss, dr");

        ObjectNode loyaltySchema = pProps.putObject("loyalty_programme_accounts");
        loyaltySchema.put("type", "array").put("description", "Loyalty/frequent flyer accounts");
        ObjectNode loyaltyItem = loyaltySchema.putObject("items").put("type", "object");
        ObjectNode lProps = loyaltyItem.putObject("properties");
        lProps.putObject("airline_iata_code").put("type", "string").put("description", "Airline IATA code (e.g. BA)");
        lProps.putObject("account_number").put("type", "string").put("description", "Loyalty account number");

        schema.putArray("required").add("offer_id").add("passengers");
        return schema;
    }

    @Override public JsonNode execute(JsonNode arguments) throws Exception {
        String offerId = arguments.path("offer_id").asText();
        JsonNode passengersInput = arguments.path("passengers");

        ObjectNode body = mapper.createObjectNode();
        ObjectNode data = body.putObject("data");
        data.put("type", "instant");
        data.putArray("selected_offers").add(offerId);

        ArrayNode passengers = data.putArray("passengers");
        for (JsonNode pIn : passengersInput) {
            ObjectNode p = passengers.addObject();
            p.put("id", pIn.path("id").asText());
            p.put("given_name", pIn.path("given_name").asText());
            p.put("family_name", pIn.path("family_name").asText());
            p.put("email", pIn.path("email").asText());
            p.put("phone_number", pIn.path("phone_number").asText());
            p.put("born_on", pIn.path("born_on").asText());
            p.put("gender", pIn.path("gender").asText());
            p.put("title", pIn.path("title").asText("mr"));

            JsonNode loyalty = pIn.path("loyalty_programme_accounts");
            if (loyalty.isArray() && loyalty.size() > 0) {
                ArrayNode lpa = p.putArray("loyalty_programme_accounts");
                for (JsonNode l : loyalty) {
                    ObjectNode acct = lpa.addObject();
                    acct.put("airline_iata_code", l.path("airline_iata_code").asText());
                    acct.put("account_number", l.path("account_number").asText());
                }
            }
        }

        return client.post("/air/orders", body);
    }
}
