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

public class CenterShootMission extends MissionBase {
    @Override
    public void routine() throws AutoMissionEndedException{

        /* The robot will move to the neutral zone and intake fuel. */
        runAction(new ParallelAction(
            new MoveSwerve("CenterAndBackPath", true),
            new IntakeAction(1.5, "Standby"),
            new IntakeAction(99, "Intaking")
    ));

        /* When the robot is in shooting position it will shoot for the rest of the autonomous round */
        runAction(new ParallelAction(
            new ShootAction(99),
            new SeriesAction(
                new IntakeAction(1, "Down"),
                new IntakeAction(1, "Standby"),
                new IntakeAction(1, "Down"),
                new IntakeAction(1, "Standby"),
                new IntakeAction(1, "Down"),
                new IntakeAction(1, "Standby")
            )
        ));


        

}} 