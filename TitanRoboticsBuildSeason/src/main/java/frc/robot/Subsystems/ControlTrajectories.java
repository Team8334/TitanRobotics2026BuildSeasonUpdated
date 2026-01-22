package frc.robot.Subsystems;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class ControlTrajectories {
    
    
    public ControlTrajectories(){
    
        HolonomicDriveController controller = new HolonomicDriveController(
        new PIDController(1, 0, 0), new PIDController(1, 0, 0),
        new ProfiledPIDController(1, 0, 0,
        new TrapezoidProfile.Constraints(6.28, 3.14)));
        private final PIDController xController = new PIDController(10.0, 0.0, 0.0);
        private final PIDController yController = new PIDController(10.0, 0.0, 0.0);
        final PIDController headingController = new PIDController(7.5,0.0,0.0);

        var sideStart = new Pose2d(1.54,23.23, Rotation2d.fromDegrees(-180));
        var crossScale = new Pose2d(0, 5, Rotation2d.fromDegrees(-160));

        SwerveBase.getInstance().drive(null);
    // Here, our rotation profile constraints were a max velocity
    // of 1 rotation per second and a max acceleration of 180 degrees
    // per second squared.

    }
}