package frc.robot.Auto.Missions;

import frc.robot.Auto.AutoMissionChooser;
import frc.robot.Auto.AutoMissionEndedException;
import frc.robot.Auto.Actions.WaitAction;
import frc.robot.Auto.Actions.MoveSwerve;

public class DoSomething extends MissionBase{
        @Override
    protected void routine() throws AutoMissionEndedException {
        runAction(new MoveSwerve("AnotherPath", true));
        //runAction(new WaitAction(AutoMissionChooser.delay));
    }
}
