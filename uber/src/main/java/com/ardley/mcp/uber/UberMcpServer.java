package com.ardley.mcp.uber;

import com.ardley.mcp.core.JsonRpcHandler;
import com.ardley.mcp.core.ToolRegistry;
import com.ardley.mcp.uber.tools.*;

/**
 * Uber MCP Server — provides ride-hailing tools via the MCP protocol.
 *
 * Uses Uber API v1.2 endpoints:
 * - GET  /v1.2/estimates/price
 * - POST /v1.2/requests
 * - GET  /v1.2/requests/{request_id}
 * - DELETE /v1.2/requests/{request_id}
 * - POST /v1.2/requests (with scheduled time)
 *
 * Run: java -jar ardley-mcp-uber-1.0.0-SNAPSHOT.jar [token-store-path]
 */
public class UberMcpServer {
    public static void main(String[] args) {
        ToolRegistry registry = new ToolRegistry();
        registry.register(new GetPriceEstimatesTool());
        registry.register(new RequestRideTool());
        registry.register(new GetRideStatusTool());
        registry.register(new CancelRideTool());
        registry.register(new ScheduleRideTool());

        new JsonRpcHandler(registry).run();
    }
}
