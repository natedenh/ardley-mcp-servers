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
- **Gradle 8.5+** (wrapper included)

## Build

```bash
# Build everything
./gradlew build

# Build a specific module
./gradlew :uber:build

# Clean build
./gradlew clean build
```

Each service module produces a fat JAR (via Shadow plugin) in its `build/libs/` directory.

## Run

```bash
# Run the Uber MCP server
java -jar uber/build/libs/uber-1.0.0-SNAPSHOT.jar

# Pipe a JSON-RPC request
echo '{"jsonrpc":"2.0","id":1,"method":"tools/list","params":{}}' | \
  java -jar uber/build/libs/uber-1.0.0-SNAPSHOT.jar
```

## Adding a New Module

1. Create a new directory: `my-module/`
2. Add a `build.gradle.kts`:
   ```kotlin
   plugins {
       id("com.github.johnrengelman.shadow")
   }
   dependencies {
       implementation(project(":core"))
   }
   tasks.shadowJar {
       archiveClassifier.set("")
       manifest {
           attributes("Main-Class" to "com.ardley.mcp.mymodule.MyMcpServer")
       }
   }
   ```
3. Add to `settings.gradle.kts`: `include("my-module")`
4. Create your tool classes implementing `McpTool`
5. Create a main class that registers tools with `ToolRegistry` and runs `JsonRpcHandler`

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
