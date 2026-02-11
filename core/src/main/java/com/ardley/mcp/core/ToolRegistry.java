package com.ardley.mcp.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

/**
 * Registry that holds all available MCP tools and dispatches calls.
 */
public class ToolRegistry {
    private final Map<String, McpTool> tools = new LinkedHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public void register(McpTool tool) {
        tools.put(tool.getName(), tool);
    }

    public McpTool getTool(String name) {
        return tools.get(name);
    }

    public Collection<McpTool> getAllTools() {
        return Collections.unmodifiableCollection(tools.values());
    }

    /**
     * Build the tools/list response content.
     */
    public JsonNode buildToolList() {
        ArrayNode arr = mapper.createArrayNode();
        for (McpTool tool : tools.values()) {
            ObjectNode t = mapper.createObjectNode();
            t.put("name", tool.getName());
            t.put("description", tool.getDescription());
            t.set("inputSchema", tool.getInputSchema());
            arr.add(t);
        }
        ObjectNode result = mapper.createObjectNode();
        result.set("tools", arr);
        return result;
    }
}
