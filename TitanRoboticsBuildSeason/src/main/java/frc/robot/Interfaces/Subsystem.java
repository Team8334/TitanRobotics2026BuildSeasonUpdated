package frc.robot.Interfaces;

/* This interface is for all of the subsystems. Each subsystem must
 * have all of these functions.
 */

public interface Subsystem {
    public void update();

    public void initialize();

    public void log();

    public boolean isEnabled();

    public String getName();
}