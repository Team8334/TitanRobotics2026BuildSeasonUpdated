package frc.robot.Auto.Missions;

import frc.robot.Auto.AutoMissionEndedException;
import frc.robot.Auto.Actions.IntakeAction;
import frc.robot.Auto.Actions.ShootAction;
import frc.robot.Auto.Actions.MoveSwerve;
import frc.robot.Auto.Actions.WaitAction;
import frc.robot.Auto.Actions.ParallelAction;
import frc.robot.Auto.Actions.ParallelRaceAction;
import choreo.Choreo;
import choreo.trajectory.SwerveSample;
import choreo.trajectory.Trajectory;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

/*
    This sets the state of the Intake to either "Standby", "Intaking", "Reverse",or "Disabled"
 */

public class DepotShootMission extends MissionBase {

    // Read the DepotPath trajectory once at construction to extract the start pose.
    private final Optional<Trajectory<SwerveSample>> trajectory = Choreo.loadTrajectory("DepotPath");

    @Override
    public void routine() throws AutoMissionEndedException {
        // Move to depot while intaking (race action finishes when movement is done)
        runAction(new ParallelRaceAction(
            new MoveSwerve("DepotPath", true),
            new IntakeAction(999, "Intaking") // high timeout so it won't finish early
        ));

        runAction(new IntakeAction(4, "Intaking"));
        
        // Turn off intake completely before moving
        runAction(new IntakeAction(0.1, "Standby"));
        
        // Move to shoot position and spool up/shoot while moving
        runAction(new MoveSwerve("DepotToShootPath", false)); // don't reset odometry
        runAction(new ShootAction(4.0));
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