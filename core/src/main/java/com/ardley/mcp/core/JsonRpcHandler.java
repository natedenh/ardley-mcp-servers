package com.ardley.mcp.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Handles JSON-RPC 2.0 messages over stdin/stdout for the MCP protocol.
 */
public class JsonRpcHandler {
    private static final Logger log = LoggerFactory.getLogger(JsonRpcHandler.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final ToolRegistry registry;

    public JsonRpcHandler(ToolRegistry registry) {
        this.registry = registry;
    }

    /**
     * Run the stdio JSON-RPC loop. Reads one JSON object per line from stdin.
     */
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter writer = new PrintWriter(System.out, true)) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    JsonNode request = mapper.readTree(line);
                    JsonNode response = handleRequest(request);
                    writer.println(mapper.writeValueAsString(response));
                } catch (Exception e) {
                    log.error("Error processing request", e);
                    writer.println(mapper.writeValueAsString(
                            errorResponse(null, -32603, "Internal error: " + e.getMessage())));
                }
            }
        } catch (Exception e) {
            log.error("Fatal error in JSON-RPC handler", e);
        }
    }

    private JsonNode handleRequest(JsonNode request) {
        JsonNode id = request.get("id");
        String method = request.path("method").asText("");

        return switch (method) {
            case "initialize" -> successResponse(id, buildInitializeResult());
            case "tools/list" -> successResponse(id, registry.buildToolList());
            case "tools/call" -> handleToolCall(id, request.path("params"));
            default -> errorResponse(id, -32601, "Method not found: " + method);
        };
    }

    private JsonNode handleToolCall(JsonNode id, JsonNode params) {
        String toolName = params.path("name").asText("");
        McpTool tool = registry.getTool(toolName);
        if (tool == null) {
            return errorResponse(id, -32602, "Unknown tool: " + toolName);
        }
        try {
            JsonNode result = tool.execute(params.path("arguments"));
            ObjectNode content = mapper.createObjectNode();
            content.set("content", mapper.createArrayNode().add(
                    mapper.createObjectNode().put("type", "text")
                            .put("text", mapper.writeValueAsString(result))));
            return successResponse(id, content);
        } catch (Exception e) {
            log.error("Tool execution error: {}", toolName, e);
            return errorResponse(id, -32603, "Tool error: " + e.getMessage());
        }
    }

    private ObjectNode buildInitializeResult() {
        ObjectNode result = mapper.createObjectNode();
        result.put("protocolVersion", "2024-11-05");
        ObjectNode capabilities = mapper.createObjectNode();
        capabilities.set("tools", mapper.createObjectNode());
        result.set("capabilities", capabilities);
        ObjectNode serverInfo = mapper.createObjectNode();
        serverInfo.put("name", "ardley-mcp-server");
        serverInfo.put("version", "1.0.0");
        result.set("serverInfo", serverInfo);
        return result;
    }

    private ObjectNode successResponse(JsonNode id, JsonNode result) {
        ObjectNode resp = mapper.createObjectNode();
        resp.put("jsonrpc", "2.0");
        resp.set("id", id);
        resp.set("result", result);
        return resp;
    }

    private ObjectNode errorResponse(JsonNode id, int code, String message) {
        ObjectNode resp = mapper.createObjectNode();
        resp.put("jsonrpc", "2.0");
        resp.set("id", id);
        ObjectNode error = mapper.createObjectNode();
        error.put("code", code);
        error.put("message", message);
        resp.set("error", error);
        return resp;
    }
}
