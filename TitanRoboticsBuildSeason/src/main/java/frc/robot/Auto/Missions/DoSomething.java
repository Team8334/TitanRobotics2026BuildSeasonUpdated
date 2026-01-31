package frc.robot.Auto.Missions;

import choreo.auto.AutoFactory;
import choreo.auto.AutoRoutine;
import choreo.auto.AutoTrajectory;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Auto.AutoMissionChooser;
import frc.robot.Auto.AutoMissionEndedException;
import frc.robot.Auto.Actions.WaitAction;
import frc.robot.Auto.Actions.MoveSwerve;
import frc.robot.Auto.ChoreoTraj;

public class DoSomething extends MissionBase{
    @Override
    protected void routine() throws AutoMissionEndedException {
       runAction(new MoveSwerve("AnotherPath", true));
    }
}