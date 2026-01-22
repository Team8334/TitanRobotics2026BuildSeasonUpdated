package frc.robot.Auto.Actions;

import java.util.ArrayList;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.TrajectoryGenerator.ControlVectorList;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Subsystems.SwerveBase;
import frc.robot.Subsystems.ControlTrajectories;
import frc.robot.Interfaces.Actions;
import frc.robot.Subsystems.AutoDrive;

// helpful link: https://choreo.autos/usage/code-generation/

public class MoveRobotForward implements Actions{
    ControlTrajectories trajectory;
    HolonomicDriveController controller;
    SwerveBase swerveBase;
    Timer timer;
    AutoDrive autoDrive;


    public MoveRobotForward(){
        swerveBase = SwerveBase.getInstance();
            
        /*controller = new HolonomicDriveController(
        new PIDController(1, 0, 0), new PIDController(1, 0, 0),
        new ProfiledPIDController(1, 0, 0,
        new TrapezoidProfile.Constraints(6.28, 3.14)));
*/     // private final PIDController xController = new PIDController(10.0, 0.0, 0.0);
        //private final PIDController yController = new PIDController(10.0, 0.0, 0.0);
        //final PIDController headingController = new PIDController(7.5,0.0,0.0);

        //headingController.enableContinuousInput(-Math.PI, Math.PI);
        var sideStart = new Pose2d(1.54,23.23, Rotation2d.fromDegrees(-180));
        var crossScale = new Pose2d(0, 5, Rotation2d.fromDegrees(-160));
       /* 
        trajectory = TrajectoryGenerator.generateTrajectory(
        sideStart,
        null, 
        crossScale, 
        null);

        new TrapezoidProfile.Constraints(6.28, 3.14);
*/
        trajectory = TrajectoryGenerator.generateTrajectory(
        sideStart, 
        crossScale,
        null);
    }

    @Override
    public void start() {
        //timer = new Timer();
        //timer.start();
    }
    
    
    public void update() {
        autoDrive.followTrajectory(trajectory);//what is swerve sample?
    }

    @Override
    public boolean isFinished(){
        return true;
        //return timer.get() >= seconds;
    }

    @Override
    public void done() {
        //timer.stop();
        //mDrive.driveWithSpeed(0, 0, 0);
    }


    /*
     * var interiorWaypoints = new ArrayList<Translation2d>();
     * interiorWaypoints.add(new Translation2d(Units.feetToMeters(14.54),
     * Units.feetToMeters(23.23)));
     * interiorWaypoints.add(new Translation2d(Units.feetToMeters(21.04),
     * Units.feetToMeters(18.23)));
     * 
     * TrajectoryConfig config = new TrajectoryConfig(Units.feetToMeters(12),
     * Units.feetToMeters(12));
     * config.setReversed(true);
*/
}
