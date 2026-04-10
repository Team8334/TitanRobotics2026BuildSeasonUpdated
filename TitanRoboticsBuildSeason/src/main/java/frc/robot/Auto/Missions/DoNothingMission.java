package frc.robot.Auto.Missions;

import frc.robot.Auto.AutoMissionEndedException;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

/*
 * This mission has the robot sit and wait till the
 * teleop phase of the match
 * Can be used for both red and blue alliances
 */

public class DoNothingMission extends MissionBase {
    @Override
    protected void routine() throws AutoMissionEndedException {
        System.out.println("Do nothing auto mission");
    }

    @Override
    public Pose2d getStartingPose() {
        // No path to read from, use a safe alliance-aware default.
        // Place the robot at the center of the field facing the hub.
        boolean isRed = DriverStation.getAlliance().orElse(Alliance.Blue) == Alliance.Red;
        return new Pose2d(isRed ? 13.0 : 3.58, 4.035, Rotation2d.fromDegrees(isRed ? 180 : 0));
    }
}