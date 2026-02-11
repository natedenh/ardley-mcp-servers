package com.ardley.mcp.core;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Base interface for all MCP tools.
 */
public interface McpTool {
    /** Tool name as exposed via MCP (e.g. "uber_get_price_estimates"). */
    String getName();

    /** Human-readable description of the tool. */
    String getDescription();

    /** JSON Schema describing the tool's input parameters. */
    JsonNode getInputSchema();

    /** Execute the tool with the given arguments and return a JSON result. */
    JsonNode execute(JsonNode arguments) throws Exception;
}
