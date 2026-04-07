package frc.robot.Auto.Missions;

import frc.robot.Auto.AutoMissionEndedException;
import frc.robot.Auto.Actions.IntakeAction;
import frc.robot.Auto.Actions.ShootAction;
import frc.robot.Auto.Actions.MoveSwerve;
import frc.robot.Auto.Actions.WaitAction;

public class DepotShootMission extends MissionBase{
    @Override
    public void routine() throws AutoMissionEndedException{
        runAction(new MoveSwerve("ExamplePath", true));
        runAction(new IntakeAction(10, "Intaking"));
        runAction(new MoveSwerve("NotherPath", true));
        runAction(new ShootAction(10, 3500));
    }

}
