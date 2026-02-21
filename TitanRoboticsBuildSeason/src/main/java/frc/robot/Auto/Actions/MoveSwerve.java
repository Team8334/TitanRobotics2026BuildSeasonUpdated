package frc.robot.Auto.Actions;

import java.lang.module.ResolutionException;
import java.util.Optional;
import choreo.Choreo;
import choreo.trajectory.*;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Subsystems.SwerveBase;
import frc.robot.Interfaces.Actions;
import frc.robot.Auto.ChoreoTraj;

/*  Class: Move Swerve Action
    Description: Ties our swerve base to the Choreo Trajectory platform so that we can make autos way easier
    Author: Rhea Sneller

    helpful links:  https://choreo.autos/choreolib/getting-started/ this is how to connect the swerve to choreo
                    https://github.com/SleipnirGroup/Choreo/releases/tag/v2026.0.1 this is where to download choreo
*/

public class MoveSwerve implements Actions{
    private final Optional<Trajectory<SwerveSample>> trajectory;
    //private final ChoreoTraj choreoTraj;
    private final boolean resetOdometry;
    SwerveBase swerveBase;
    Timer timer;

    private final PIDController xController = new PIDController(1.0, 0.0, 0.0);
    private final PIDController yController = new PIDController(1.0, 0.0, 0.0);
    // heading controller lets us go in a full circle
    private final PIDController headingController;

    public MoveSwerve(String trajectoryName, boolean resetOdometry){
        // sets up the swerve base, controller, trajectory, timer, and odometry
        swerveBase = SwerveBase.getInstance();
        var swerveConfig = swerveBase.getSwerveController();
        headingController = new PIDController(
            swerveConfig.config.headingPIDF.p,
            swerveConfig.config.headingPIDF.i,
            swerveConfig.config.headingPIDF.d);
        headingController.enableContinuousInput(-Math.PI, Math.PI);
        this.trajectory = Choreo.loadTrajectory(trajectoryName);
        this.timer = new Timer();
        this.resetOdometry = resetOdometry;
    }

    private boolean isRedAlliance(){
        // finds out which alliance we are on
        if (DriverStation.getAlliance().get() == Alliance.Red){
        return true;
       }
       if (DriverStation.getAlliance().get() == Alliance.Blue){
        return false;
       }
       else{
        return true;
       }
    }

    @Override
    public void start() {
        // start the timer and getting the robot pose
        timer.restart();

        if (resetOdometry) {
            Optional<Pose2d> startPose = trajectory.get().getInitialPose(isRedAlliance());
            if (startPose != null) {
                swerveBase.resetOdometry(startPose.get());
            }
        }
    }
    
    public void update() {
        double time = timer.get();
        SwerveSample sample = trajectory.get().sampleAt(time, isRedAlliance()).get();
        // Get the current currentRobotPose the robot
        Pose2d currentRobotPose = swerveBase.getPose();
        Pose2d targetPose = sample.getPose();

        // Generate the next speeds for the robot
        ChassisSpeeds autoSpeeds = new ChassisSpeeds(
            sample.vx + xController.calculate(currentRobotPose.getX(), sample.x),
            sample.vy + yController.calculate(currentRobotPose.getY(), sample.y),
            sample.omega + headingController.calculate(currentRobotPose.getRotation().getRadians(), targetPose.getRotation().getRadians())
        );

        // Apply the generated speeds into swerve
        swerveBase.driveFieldOriented(autoSpeeds);
    }

    @Override
    public boolean isFinished(){
        //the timer is done, so we reached end of trajectory
        // add one second to time so that it can finish rotation
        return timer.hasElapsed(trajectory.get().getTotalTime() + 1);
    }

    @Override
    public void done() {
        //Completely stop robot and timer
        timer.stop();
        swerveBase.driveFieldOriented(new ChassisSpeeds());
    }
}
