# Google Workspace MCP Server

> **Status:** Placeholder — will be migrated from the existing standalone server at `~/.openclaw/google-workspace-mcp/`.

This module will provide MCP tools for:
- **Gmail** — list, read, search messages and threads
- **Google Calendar** — list events, check availability, create events
- **Google Drive** — list, upload, export files; manage comments
- **Google Docs** — create, read, edit documents with full formatting support
- **Google Sheets** — read/write cell data, formatting, sheet management
- **Google Slides** — create presentations, add slides/text/images

## Migration Notes

The existing server already implements all of the above using:
- Java 17 + Maven
- Google API Client Library for Java
- OAuth2 with stored refresh tokens
- Fat JAR via maven-shade-plugin

Migration will involve:
1. Moving tool implementations into this module
2. Refactoring shared code (JSON-RPC, OAuth2) into `core/`
3. Updating imports and build configuration
