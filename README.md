# Ardley MCP Servers

A Java monorepo for [Model Context Protocol (MCP)](https://modelcontextprotocol.io/) servers built by [Ardley](https://meetardley.com).

Each module is a standalone MCP server that communicates via JSON-RPC 2.0 over stdin/stdout, making them compatible with any MCP client (Claude Desktop, OpenClaw, etc.).

## Modules

| Module | Description | Status |
|--------|-------------|--------|
| **core** | Shared MCP protocol, OAuth2, HTTP utilities | ✅ Ready |
| **google-workspace** | Gmail, Calendar, Drive, Docs, Sheets, Slides | 🔄 Migration pending |
| **microsoft-365** | Outlook, Teams, OneDrive via Microsoft Graph | 📋 Planned |
| **uber** | Ride estimates, requests, scheduling via Uber API | 🏗️ Scaffolded |

## Prerequisites

- **Java 17+**
- **Maven 3.8+**

## Build

```bash
# Build everything
mvn clean package

# Build a specific module
mvn clean package -pl uber -am

# Skip tests
mvn clean package -DskipTests
```

Each module produces a fat JAR in its `target/` directory.

## Run

```bash
# Run the Uber MCP server
java -jar uber/target/ardley-mcp-uber-1.0.0-SNAPSHOT.jar

# Pipe a JSON-RPC request
echo '{"jsonrpc":"2.0","id":1,"method":"tools/list","params":{}}' | \
  java -jar uber/target/ardley-mcp-uber-1.0.0-SNAPSHOT.jar
```

## Adding a New Module

1. Create a new directory: `my-module/`
2. Add a `pom.xml` with parent reference:
   ```xml
   <parent>
       <groupId>com.ardley.mcp</groupId>
       <artifactId>ardley-mcp-servers</artifactId>
       <version>1.0.0-SNAPSHOT</version>
   </parent>
   ```
3. Add `ardley-mcp-core` as a dependency
4. Register the module in the root `pom.xml` `<modules>` section
5. Create your tool classes implementing `McpTool`
6. Create a main class that registers tools with `ToolRegistry` and runs `JsonRpcHandler`

## Architecture

```
┌─────────────────────────────────────────────┐
│                  MCP Client                  │
│         (Claude, OpenClaw, etc.)             │
└──────────────────┬──────────────────────────┘
                   │ stdin/stdout (JSON-RPC 2.0)
┌──────────────────▼──────────────────────────┐
│              JsonRpcHandler                  │
│         (core — protocol layer)              │
├─────────────────────────────────────────────┤
│              ToolRegistry                    │
│        (routes calls to tools)               │
├──────┬──────┬──────┬───────────────────────┤
│ Tool │ Tool │ Tool │  ...                    │
│  A   │  B   │  C   │                        │
└──────┴──────┴──────┴───────────────────────┘
```

## License

Proprietary — Ardley Technologies
