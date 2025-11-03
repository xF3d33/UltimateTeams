package dev.xf3d3.ultimateteams.models;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the rank/role of a team member
 * Higher weight = higher authority
 */
public enum TeamRank {
    MEMBER(1, "Member"),
    MANAGER(2, "Manager"),
    CO_OWNER(3, "Co-Owner"),
    OWNER(4, "Owner");
    
    private final int weight;
    private final String displayName;
    
    TeamRank(int weight, String displayName) {
        this.weight = weight;
        this.displayName = displayName;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get TeamRank from weight value
     */
    @NotNull
    public static TeamRank fromWeight(int weight) {
        return switch (weight) {
            case 4 -> OWNER;
            case 3 -> CO_OWNER;
            case 2 -> MANAGER;
            default -> MEMBER;
        };
    }
    
    /**
     * Check if this rank is owner
     */
    public boolean isOwner() {
        return this == OWNER;
    }
    
    /**
     * Check if this rank is co-owner or higher
     */
    public boolean isCoOwnerOrHigher() {
        return weight >= CO_OWNER.weight;
    }
    
    /**
     * Check if this rank is manager or higher
     */
    public boolean isManagerOrHigher() {
        return weight >= MANAGER.weight;
    }
    
    /**
     * Check if this rank can perform actions that co-owners can do
     * (invite, kick, promote, demote, etc.)
     */
    public boolean canManageMembers() {
        return weight >= CO_OWNER.weight;
    }
    
    /**
     * Check if this rank can disband the team
     * Only OWNER can disband
     */
    public boolean canDisband() {
        return this == OWNER;
    }
}
