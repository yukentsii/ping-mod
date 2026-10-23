# Ping Mod

A lightweight, strictly **server-side** Fabric mod for Minecraft 26.3 that provides real-time ping measurement and an Action Bar HUD.

No client-side mod required! Works seamlessly for vanilla clients.

---

## Features

- **⚡ Live On-Demand Ping**: Uses native ping/pong packets to measure the exact round-trip latency at that precise millisecond (no 15-second keepalive delay or stale rolling averages).
- **📊 Action Bar HUD (`/hudping`)**: Displays your live ping directly above the hotbar without cluttering chat.
- **🎨 Dynamic Color Coding**:
  - `< 80 ms`: **Green**
  - `80 - 150 ms`: **Yellow**
  - `> 150 ms`: **Red**
- **⚙️ Configurable Refresh Rate**: Adjust how often the HUD updates per player.
- **🛡️ Server-Side Only & Dependency-Free**: Pure Java, no client-only code, and no extra language runtimes needed.

---

## Commands

| Command | Permission | Description |
|---|---|---|
| `/ping` | Everyone | Measures your live round-trip latency and prints it to chat. |
| `/ping <player>` | OP (Level 2+) | Checks another player's current latency. |
| `/hudping` | Everyone | Toggles the live ping display on your Action Bar (above hotbar). |
| `/hudping <on\|off>` | Everyone | Explicitly enables or disables the Action Bar ping display. |
| `/ping refresh <seconds>` | Everyone | Sets your HUD refresh interval (1–60 seconds, default: 1s). |
| `/hudping refresh <seconds>` | Everyone | Alias for setting the HUD refresh interval. |

---

## Installation

1. Install **Fabric Loader** and **Fabric API** on your Minecraft server (26.3).
2. Download `ping-mod-1.0.0.jar` from the [Releases](https://github.com/yukentsii/ping-mod/releases) page.
3. Place the `.jar` into your server's `mods/` directory and restart the server.
4. Players connecting with a regular vanilla Minecraft client can immediately use `/ping` and `/hudping`.

---

## License

This project is licensed under the CC0-1.0 License.
