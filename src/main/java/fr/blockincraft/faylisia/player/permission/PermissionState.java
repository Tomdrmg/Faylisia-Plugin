package fr.blockincraft.faylisia.player.permission;

import fr.blockincraft.faylisia.Faylisia;

public enum PermissionState {
    TRUE,
    DEV,
    FALSE;

    public static boolean getValue(PermissionState state) {
        if (state == null) return false;
        if (state == TRUE) return true;
        if (state == FALSE) return false;
        return Faylisia.development;
    }
}
