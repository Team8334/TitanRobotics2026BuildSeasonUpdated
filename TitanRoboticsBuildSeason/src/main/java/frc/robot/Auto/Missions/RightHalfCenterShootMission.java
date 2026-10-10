package frc.robot.Auto.Missions;

import frc.robot.Auto.AutoMissionEndedException;
import frc.robot.Auto.Actions.IntakeAction;
import frc.robot.Auto.Actions.ShootAction;
import frc.robot.Auto.Actions.MoveSwerve;
import frc.robot.Auto.Actions.WaitAction;
import frc.robot.Auto.Actions.ParallelAction;
import frc.robot.Auto.Actions.ParallelRaceAction;
import frc.robot.Auto.Actions.SeriesAction;

/*
    This sets the state of the Intake to either "Standby", "Intaking", "Reverse",or "Disabled"
 */

public class RightHalfCenterShootMission extends MissionBase {
    @Override
    public void routine() throws AutoMissionEndedException{
        runAction(new ParallelAction(
            new MoveSwerve("RightHalfCenterPath", true),
            new IntakeAction(0.5, "Standby"),
            new IntakeAction(4, "Intaking")
    ));
        runAction(new ParallelAction(
            new ShootAction(99),
            new SeriesAction(
            new IntakeAction(0.5, "Down"),
            new IntakeAction(0.5, "Standby"),
            new IntakeAction(0.5, "Down"),
            new IntakeAction(0.5, "Standby"),
            new IntakeAction(0.5, "Down"),
            new IntakeAction(0.5, "Standby"),
            new IntakeAction(0.5, "Down"),
            new IntakeAction(0.5, "Standby"))
        ));

}} 