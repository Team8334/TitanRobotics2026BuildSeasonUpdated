package frc.robot.Subsystems;

import choreo.trajectory.SwerveSample;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Subsystems.SwerveBase;
import frc.robot.Interfaces.Actions;

public class AutoDrive {
    public class Drive extends SubsystemBase {
    private final PIDController xController = new PIDController(10.0, 0.0, 0.0);
    private final PIDController yController = new PIDController(10.0, 0.0, 0.0);
    private final PIDController headingController = new PIDController(7.5, 0.0, 0.0);

    SwerveBase swerveBase;
    Trajectory trajectory;
    HolonomicDriveController controller;

    public void AutoDrive(){
    
    }
    
    public void DriveAuto() {
        // Other subsystem initialization code
        // ...

        headingController.enableContinuousInput(-Math.PI, Math.PI);
    }

    public void followTrajectory(SwerveSample sample) {
        // Get the current currentRobotPose the robot
        Pose2d currentRobotPose = swerveBase.getPose();

        // Generate the next speeds for the robot
        ChassisSpeeds autoSpeeds = new ChassisSpeeds(
            sample.vx + xController.calculate(currentRobotPose.getX(), sample.x),
            sample.vy + yController.calculate(currentRobotPose.getY(), sample.y),
            sample.omega + headingController.calculate(currentRobotPose.getRotation().getRadians(), sample.heading)
        );

        // Apply the generated speeds
        swerveBase.drive(autoSpeeds);
    }
}
}