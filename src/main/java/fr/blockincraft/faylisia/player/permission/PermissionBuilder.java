package fr.blockincraft.faylisia.player.permission;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Builder to create an array of {@link Permission}, with that, we can use 'perm.*' to give all permission which start with 'perm' <br/>
 * Order is important, if set permission to false but use '.*' after permission will be true, if use '.*' and then set permission to false, permission will be false
 */
public class PermissionBuilder {
    private final Map<String, PermissionState> permissions = new HashMap<>();

    /**
     * Set one or multiple permissions to a {@link PermissionState}
     * @param perm permission to set or 'perm.*' to set multiple
     * @param state state of this permission(s)
     * @return this instance
     */
    @NotNull
    public PermissionBuilder set(@NotNull String perm, @NotNull  PermissionState state) {
        // Check if we need to set multiple permissions or only one
        if (perm.endsWith(".*")) {
            // Remove '.*' of the permission to form the permission prefix
            String subString = perm.substring(0, perm.length() - 2);

            // Get all permissions which start with permission prefix
            for (String p : Permission.allPerms) {
                // Add or update it
                if (p.startsWith(subString)) permissions.put(p, state);
            }
            for (String p : Permission.otherPerms) {
                // Add or update it
                if (p.startsWith(subString)) permissions.put(p, state);
            }
        } else {
            // Check if permission exist
            boolean contain = Arrays.asList(Permission.allPerms).contains(perm) || Arrays.asList(Permission.otherPerms).contains(perm);
            if (!contain) return this;

            // Then add or update it
            permissions.put(perm, state);
        }

        return this;
    }

    /**
     * Construct a permission array
     * @return permissions
     */
    @NotNull
    public Permission[] build() {
        List<Permission> permissions = new ArrayList<>();

        for (Map.Entry<String, PermissionState> entry : this.permissions.entrySet()) {
            permissions.add(new Permission(entry.getKey(), entry.getValue()));
        }

        return permissions.toArray(new Permission[0]);
    }
}