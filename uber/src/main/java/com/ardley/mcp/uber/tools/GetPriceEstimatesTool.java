package com.ardley.mcp.uber.tools;

import com.ardley.mcp.core.McpTool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Get price estimates for a trip between two locations.
 * Uber API: GET /v1.2/estimates/price
 */
public class GetPriceEstimatesTool implements McpTool {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getName() {
        return "uber_get_price_estimates";
    }

    @Override
    public String getDescription() {
        return "Get price estimates for an Uber ride between two locations. Returns estimates for all available products (UberX, UberXL, etc.).";
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
        ArrayNode required = schema.putArray("required");
        required.add("start_latitude").add("start_longitude").add("end_latitude").add("end_longitude");
        return schema;
    }

    @Override
    public JsonNode execute(JsonNode arguments) throws Exception {
        double startLat = arguments.path("start_latitude").asDouble();
        double startLng = arguments.path("start_longitude").asDouble();
        double endLat = arguments.path("end_latitude").asDouble();
        double endLng = arguments.path("end_longitude").asDouble();

        // TODO: Call Uber API — GET https://api.uber.com/v1.2/estimates/price
        //   ?start_latitude={}&start_longitude={}&end_latitude={}&end_longitude={}

        ObjectNode result = mapper.createObjectNode();
        result.put("status", "placeholder");
        result.put("message", String.format(
                "Would fetch price estimates from (%.4f, %.4f) to (%.4f, %.4f)",
                startLat, startLng, endLat, endLng));

        ArrayNode prices = result.putArray("prices");
        ObjectNode uberx = prices.addObject();
        uberx.put("product_id", "placeholder-uberx-id");
        uberx.put("display_name", "UberX");
        uberx.put("estimate", "$12-16");
        uberx.put("currency_code", "USD");
        uberx.put("duration", 900);
        uberx.put("distance", 5.2);

        return result;
    }
}
