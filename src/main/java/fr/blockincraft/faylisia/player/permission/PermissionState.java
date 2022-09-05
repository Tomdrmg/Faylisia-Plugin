package fr.blockincraft.faylisia.player.permission;

import fr.blockincraft.faylisia.Faylisia;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * All possible states of a permission
 */
public enum PermissionState {
    TRUE,  // True
    DEV,   // True only during development
    FALSE; // False

    /**
     * Return if a state equal to false or true depending on development of the server
     * @param state state to check
     * @return if state equal to false or true
     */
    public static boolean getValue(PermissionState state) {
        if (state == TRUE) return true;
        if (state == FALSE) return false;
        return Faylisia.development;
    }
}
