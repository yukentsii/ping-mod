# Ping Mod

A lightweight, **server-side** Fabric mod for Minecraft that provides real-time ping measurement and an Action Bar HUD.
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

## License

This project is licensed under the CC0-1.0 License.
