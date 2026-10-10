package frc.robot.Devices;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Interfaces.Subsystem;
import frc.robot.ThirdParty.LimelightHelpers;

/*
 * Class: Limelight
 * Description: Holds the object for the limelight which is our camera that can read
 *              and process april tags
 * Author: Austin
 */

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
     * A wrapper class to hold the Pose2d and the timestamp.
     */
    public static class VisionPose {
        public final Pose2d pose;
        public final double timestampSeconds;
        public final boolean hasTarget;

        public VisionPose(Pose2d pose, double timestampSeconds, boolean hasTarget) {
            this.pose = pose;
            this.timestampSeconds = timestampSeconds;
            this.hasTarget = hasTarget;
        }
    }

    /**
     * Reads the botpose_wpiblue array from NetworkTables (via LimelightHelpers)
     * and extracts the X, Y, Rotation, and Timestamp (latency compensated).
     * 
     * @return VisionPose object containing the Pose2d, timestamp, and target status.
     */
    public VisionPose getEstimatedGlobalPose() {
        if (!hasTarget()) {
            return new VisionPose(new Pose2d(), 0.0, false);
        }

        // LimelightHelpers handles reading the botpose_wpiblue array and automatically 
        // extracts the X, Y, Rotation and calculates the latency-compensated timestamp.
        LimelightHelpers.PoseEstimate poseEstimate = LimelightHelpers.getBotPoseEstimate_wpiBlue(name);

        return new VisionPose(poseEstimate.pose, poseEstimate.timestampSeconds, true);
    }

    /**
     * Reads a Neural Network object detection pipeline (assuming pipeline 1) from a SECOND camera.
     * Calculates the estimated field positions of the detected robots.
     * 
     * @param currentRobotPose The robot's current estimated Pose2d on the field.
     * @return A list of Translation2d representing the center of the detected obstacles on the field.
     */
    public java.util.List<edu.wpi.first.math.geometry.Translation2d> getDetectedRobotObstacles(Pose2d currentRobotPose) {
        java.util.List<edu.wpi.first.math.geometry.Translation2d> obstacles = new java.util.ArrayList<>();
        
        // IMPORTANT: We use a different camera name here ("limelight-detector") because a Limelight 2
        // CANNOT run AprilTags and Object Detection at the same time. 
        String detectorName = "limelight-detector";

        if (!LimelightHelpers.getTV(detectorName)) {
            return obstacles;
        }

        // Get the X offset (tx) and Area (ta) of the detected robot
        double tx = LimelightHelpers.getTX(detectorName);
        double ta = LimelightHelpers.getTA(detectorName);

        if (ta > 0.0) {
            // Rough distance estimation using target area. 
            // (You will need to tune this constant based on your actual neural network model)
            double estimatedDistanceMeters = 2.5 / Math.sqrt(ta); 

            // Calculate the absolute angle to the obstacle on the field
            edu.wpi.first.math.geometry.Rotation2d globalAngle = currentRobotPose.getRotation().plus(edu.wpi.first.math.geometry.Rotation2d.fromDegrees(-tx));

            // Calculate the absolute X/Y field coordinates of the obstacle
            double obstacleX = currentRobotPose.getX() + (estimatedDistanceMeters * globalAngle.getCos());
            double obstacleY = currentRobotPose.getY() + (estimatedDistanceMeters * globalAngle.getSin());

            obstacles.add(new edu.wpi.first.math.geometry.Translation2d(obstacleX, obstacleY));
        }

        return obstacles;
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