package frc.robot.utils;

public class Logger {
    public static void info(String message) {
        if (Constants.MODE.equals("DEV")) {
            System.out.println("[INFO] " + message);
        }
    }

    public static void error(String errorMessage) {
        error(errorMessage, null);
    }

    public static void error(String errorMessage, Exception ex) {
        System.err.println("[ERROR] " + errorMessage);
        if (ex != null) {
            ex.printStackTrace(System.err);
        }
    }
}
