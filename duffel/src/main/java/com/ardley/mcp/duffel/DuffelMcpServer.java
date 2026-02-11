package com.ardley.mcp.duffel;

import com.ardley.mcp.core.JsonRpcHandler;
import com.ardley.mcp.core.ToolRegistry;
import com.ardley.mcp.duffel.tools.*;

/**
 * Duffel MCP Server — provides flight and hotel booking tools via the MCP protocol.
 *
 * Uses Duffel API v2:
 * - POST /air/offer_requests (search flights)
 * - GET  /air/offers/{id}
 * - POST /air/orders (book)
 * - GET  /air/orders/{id}
 * - POST /air/orders/{id}/actions/cancel
 * - POST /stays/search
 * - POST /stays/quotes
 * - POST /stays/bookings
 * - POST /stays/bookings/{id}/actions/cancel
 *
 * Credentials: ~/.openclaw/duffel-credentials.json
 */
public class DuffelMcpServer {
    public static void main(String[] args) {
        DuffelApiClient client = new DuffelApiClient();

        ToolRegistry registry = new ToolRegistry();
        registry.register(new SearchFlightsTool(client));
        registry.register(new GetOfferTool(client));
        registry.register(new CreateOrderTool(client));
        registry.register(new GetOrderTool(client));
        registry.register(new CancelOrderTool(client));
        registry.register(new SearchStaysTool(client));
        registry.register(new GetStayQuoteTool(client));
        registry.register(new BookStayTool(client));
        registry.register(new CancelStayTool(client));

        new JsonRpcHandler(registry).run();
    }
}
