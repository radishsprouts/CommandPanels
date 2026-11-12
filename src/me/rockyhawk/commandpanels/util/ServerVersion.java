package me.rockyhawk.commandpanels.util;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServerVersion {

    private static final int MAJOR;
    private static final int MINOR;
    private static final int PATCH;
    private static final boolean FOLIA;

    static {
        // Parse Bukkit version (e.g. "1.20.4-R0.1-SNAPSHOT" or "1.16.5-R0.1-SNAPSHOT")
        int major = 1;
        int minor = 16;
        int patch = 0;

        try {
            String bukkitVer = Bukkit.getBukkitVersion();
            Pattern pattern = Pattern.compile("^(\\d+)\\.(\\d+)(?:\\.(\\d+))?");
            Matcher matcher = pattern.matcher(bukkitVer);
            if (matcher.find()) {
                major = Integer.parseInt(matcher.group(1));
                minor = Integer.parseInt(matcher.group(2));
                String patchGroup = matcher.group(3);
                patch = (patchGroup != null && !patchGroup.isEmpty()) ? Integer.parseInt(patchGroup) : 0;
            }
        } catch (Exception ignored) {
            major = 1;
            minor = 20;
            patch = 0;
        }

        MAJOR = major;
        MINOR = minor;
        PATCH = patch;

        String versionStr = Bukkit.getVersion();
        String nameStr = Bukkit.getName();
        FOLIA = (versionStr != null && versionStr.contains("Folia"))
                || (nameStr != null && nameStr.contains("Folia"));
    }

    private ServerVersion() {}

    public static int getMajor() {
        return MAJOR;
    }

    public static int getMinor() {
        return MINOR;
    }

    public static int getPatch() {
        return PATCH;
    }

    public static boolean isFolia() {
        return FOLIA;
    }

    public static boolean isAtLeast(int major, int minor) {
        return isAtLeast(major, minor, 0);
    }

    public static boolean isAtLeast(int major, int minor, int patch) {
        if (MAJOR > major) return true;
        if (MAJOR < major) return false;
        if (MINOR > minor) return true;
        if (MINOR < minor) return false;
        return PATCH >= patch;
    }
}
