package fr.blockincraft.faylisia.player.permission;

import java.util.*;

public class PermissionBuilder {
    private final Map<String, PermissionState> permissions = new HashMap<>();

    public PermissionBuilder set(String perm, PermissionState state) {
        if (state == null) return this;

        if (perm.endsWith(".*")) {
            String subString = perm.substring(0, perm.length() - 2);

            for (String p : Permission.allPerms) {
                if (p.startsWith(subString)) permissions.put(p, state);
            }
            for (String p : Permission.otherPerms) {
                if (p.startsWith(subString)) permissions.put(p, state);
            }
        } else {
            boolean contain = Arrays.asList(Permission.allPerms).contains(perm) || Arrays.asList(Permission.otherPerms).contains(perm);
            if (!contain) return this;

            permissions.put(perm, state);
        }

        return this;
    }

    public Permission[] build() {
        List<Permission> permissions = new ArrayList<>();

        for (Map.Entry<String, PermissionState> entry : this.permissions.entrySet()) {
            permissions.add(new Permission(entry.getKey(), entry.getValue()));
        }

        return permissions.toArray(new Permission[0]);
    }
}
