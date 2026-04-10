package frc.robot.Auto.Missions;

//import these so that the mission is an option when testing
import frc.robot.Auto.AutoMissionChooser;
import frc.robot.Auto.AutoMissionEndedException;

// import the actions from the auto.actions folder
import frc.robot.Auto.Actions.WaitAction;
import frc.robot.Auto.Actions.IntakeAction;
import frc.robot.Auto.Actions.MoveSwerve;
import frc.robot.Auto.Actions.ShootAction;
import choreo.Choreo;
import choreo.trajectory.SwerveSample;
import choreo.trajectory.Trajectory;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

/*
 * Class: ShooterMission
 * Description: This mission is to shoot our eight fuel into the hub using a
 *              Choreo movement and then a shoot action.
 * Notes: Choreo (the path) can be changed any time, just remember to generate
 *        the code and deploy the new code
 * Author: Rhea
 */

public class ShooterMission extends MissionBase {

    // Read the ShootPath trajectory once at construction to extract the start pose.
    private final Optional<Trajectory<SwerveSample>> trajectory = Choreo.loadTrajectory("ShootPath");

    @Override
    protected void routine() throws AutoMissionEndedException {
       
        runAction(new MoveSwerve("ShootPath", true));
        //runAction(new IntakeAction(5, "Intaking"));
        
        runAction(new ShootAction(10));
    }

    @Override
    public Pose2d getStartingPose() {
        boolean isRed = DriverStation.getAlliance().orElse(Alliance.Blue) == Alliance.Red;
        if (trajectory.isPresent()) {
            Optional<Pose2d> startPose = trajectory.get().getInitialPose(isRed);
            if (startPose.isPresent()) {
                return startPose.get();
            }
        }
        // Fallback: center field, facing hub direction by alliance
        return new Pose2d(isRed ? 13.0 : 3.58, 4.035, Rotation2d.fromDegrees(isRed ? 180 : 0));
    }
}