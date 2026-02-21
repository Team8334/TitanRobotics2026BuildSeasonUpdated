package frc.robot.Devices;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Interfaces.Subsystem;
import frc.robot.ThirdParty.LimelightHelpers;

public class Limelight implements Subsystem {
    private static Limelight instance = null;
    private final String name = "limelight-front";

    // PID Controller for rotation
    // kP: 0.04 is a standard starting point for Radians Per Second output
    private final PIDController turnPID = new PIDController(0.04, 0.0, 0.002);

    public static Limelight getInstance() {
        if (instance == null) {
            instance = new Limelight();
        }
        return instance;
    }

    private Limelight() {
        turnPID.setSetpoint(0); // We want the target centered (tx = 0)
        turnPID.setTolerance(1.0); // 1 degree of error is acceptable
    }

    /**
     * Calculates the rotation speed needed to face an AprilTag.
     * @param manualRotation The driver's current rotation input (used if no target seen).
     * @return Rotation speed in Radians Per Second.
     */
    public double getRotationTarget(double manualRotation) {
        // Use LimelightHelpers to check for target
        if (LimelightHelpers.getTV(name)) {
            // Get horizontal offset tx
            double tx = LimelightHelpers.getTX(name);
            return turnPID.calculate(tx);
        }
        // Return driver input if no target is found
        return manualRotation;
    }

    /**
     * Checks if the Limelight sees any targets.
     */
    public boolean hasTarget() {
        return LimelightHelpers.getTV(name);
    }

    @Override
    public void update() {
        // Data for the dashboard
        SmartDashboard.putBoolean("Limelight/Has Target", hasTarget());
        SmartDashboard.putNumber("Limelight/TX", LimelightHelpers.getTX(name));
    }

    @Override
    public void initialize() {}

    @Override
    public void log() {
        SmartDashboard.updateValues();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }
}