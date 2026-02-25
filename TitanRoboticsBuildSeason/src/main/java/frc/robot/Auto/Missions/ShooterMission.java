package frc.robot.Auto.Missions;

//import these so that the mission is an option when testing
import frc.robot.Auto.AutoMissionChooser;
import frc.robot.Auto.AutoMissionEndedException;

// import the actions from the auto.actions folder
import frc.robot.Auto.Actions.WaitAction;
import frc.robot.Auto.Actions.MoveSwerve;
import frc.robot.Auto.Actions.ShootAction;

public class ShooterMission extends MissionBase {
    @Override
    protected void routine() throws AutoMissionEndedException {
       
        runAction(new MoveSwerve("Shooter", true));
        runAction(new ShootAction(3, robotPose));
    }
}