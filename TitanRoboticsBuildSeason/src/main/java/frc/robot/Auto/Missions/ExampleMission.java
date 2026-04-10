package frc.robot.Auto.Missions;

//import these so that the mission is an option when testing
import frc.robot.Auto.AutoMissionChooser;
import frc.robot.Auto.AutoMissionEndedException;

// import the actions from the auto.actions folder
import frc.robot.Auto.Actions.WaitAction;
import frc.robot.Auto.Actions.MoveSwerve;
import choreo.Choreo;
import choreo.trajectory.SwerveSample;
import choreo.trajectory.Trajectory;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

/*
 * Class: ExampleMission
 * Description: This mission is an example mission.
 *              It has examples of a normal action and a choreo path action
 * Author: Rhea
 */

public class ExampleMission extends MissionBase {

    // Read the ExamplePath trajectory once at construction to extract the start pose.
    private final Optional<Trajectory<SwerveSample>> trajectory = Choreo.loadTrajectory("ExamplePath");

    @Override
    protected void routine() throws AutoMissionEndedException {
       
        //put the actions you want to do here in order of execution

        runAction(new WaitAction(AutoMissionChooser.delay));
        runAction(new MoveSwerve("ExamplePath", true));
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