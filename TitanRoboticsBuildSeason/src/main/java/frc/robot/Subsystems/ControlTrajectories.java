package frc.robot.Subsystems;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class ControlTrajectories {
    
    
    public ControlTrajectories(){
    
        HolonomicDriveController controller = new HolonomicDriveController(
        new PIDController(1, 0, 0), new PIDController(1, 0, 0),
        new ProfiledPIDController(1, 0, 0,
        new TrapezoidProfile.Constraints(6.28, 3.14)));



        SwerveBase.getInstance().drive(null);
    // Here, our rotation profile constraints were a max velocity
    // of 1 rotation per second and a max acceleration of 180 degrees
    // per second squared.

    }
}