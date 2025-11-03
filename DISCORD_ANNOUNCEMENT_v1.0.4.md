# 🎉 UltimateTeams v1.0.4-dei2004 Released! 🎉

## 🚀 Major New Features

### 🔐 **DiscordSRV Integration & Privacy**
- ✅ Added **DiscordSRV** soft dependency for future Discord features
- 🔇 **Team chat is now hidden from Discord** - your private conversations stay private!
- 🕵️ **Chat spy messages hidden from Discord** - admin surveillance remains in-game only
- 🛡️ Enhanced privacy with `LOWEST` event priority handling

---

### 💾 **Ender Chest Rollback System** (Admin Only)
The most requested feature is finally here! Complete backup and rollback system for team ender chests!

**⏰ Automatic Backups**
- 🔄 Auto-saves every **30 minutes**
- 📦 Keeps last **10 backups** per chest (5 hours of history)
- 💿 Persistent storage in `echest_backups.json`

**🛠️ Admin Commands**
- `/ta echest backups <team> <chest#>` - View available restore points
- `/ta echest rollback <team> <chest#> <backup#>` - Restore with team notification
- `/ta echest forcerollback <team> <chest#> <backup#>` - **Silent restore** (no team notification)
- `/ta echest allbackup <team>` - Backup all chests for a team instantly

**📊 Chest Management**
- `/ta removerow <team> <chest#> <rows>` - Remove rows from a chest (1-5)
- `/ta removechest <team> <chest#>` - Delete an entire chest

---

## 🔑 **New Permissions**
```
ultimateteams.admin.echest.rollback  - Access to rollback commands
ultimateteams.admin.echest.backup    - Access to manual backup command
```

---

## 📋 **Complete Command List**

### **Rollback & Backup Commands**
```
/ta echest backups <team-name> <chest-number>
  └─ List all available backups with timestamps

/ta echest rollback <team-name> <chest-number> <backup-number>
  └─ Restore chest (team gets notified)

/ta echest forcerollback <team-name> <chest-number> <backup-number>
  └─ Restore chest (SILENT - team NOT notified)

/ta echest allbackup <team-name>
  └─ Manually backup all team chests now
```

### **Chest Management Commands**
```
/ta removerow <team-name> <chest-number> <rows-to-remove>
  └─ Remove 1-5 rows from a chest

/ta removechest <team-name> <chest-number>
  └─ Delete an entire chest
```

---

## 💡 **Use Cases**

### 🔙 **Rollback Scenarios**
1. **Griefing Recovery**: Player steals items → rollback to last backup
2. **Accident Recovery**: Wrong items deleted → restore previous state
3. **Silent Fixes**: Fix admin mistakes without alerting team (force rollback)

### 📦 **Backup Management**
- Manual backups before risky operations
- Team-wide chest backups for safety
- Scheduled protection every 30 minutes

---

## 🎯 **What's Next?**
Stay tuned for more DiscordSRV features coming soon:
- 📢 Discord notifications for team events
- 💬 Discord channel linking
- 👑 Role synchronization
- And more!

---

## 📥 **Download & Installation**

**Requirements:**
- Spigot/Paper 1.16.5+
- Java 16+
- Optional: Vault (for economy features)
- Optional: DiscordSRV (for Discord integration)

**Download:** [GitHub Releases](https://github.com/dei2004/UltimateTeams/releases)

---

## 🐛 **Bug Fixes & Improvements**
- ✅ Enhanced event priority for better plugin compatibility
- ✅ Improved chat message handling
- ✅ Better null safety for economy features
- ✅ Optimized database operations

---

## 👨‍💻 **Credits**
- **Original Author:** xF3d3
- **Improved by:** dei0 (dei2004)
- **Version:** v1.0.4-dei2004

---

## 📝 **Full Changelog**

### Added
- DiscordSRV soft dependency
- Team chat privacy from Discord
- Chat spy privacy from Discord
- Complete ender chest rollback system
- Automatic 30-minute backups
- Manual backup commands
- Force rollback (silent) option
- Remove row command
- All backup command for teams
- 10 backup history per chest
- Persistent backup storage

### Changed
- Chat event priority to LOWEST
- Bukkit.broadcast replaced with direct player messages
- Rollback commands moved to admin-only

### Fixed
- Team chat leaking to Discord
- Chat spy messages appearing in Discord
- Event handling priority issues

---

**Enjoy the new features! 🎮**

*For support, issues, or suggestions, visit our [GitHub Repository](https://github.com/dei2004/UltimateTeams)*
