package frc.robot.Auto.Missions;

import frc.robot.Auto.AutoMissionEndedException;
import frc.robot.Auto.Actions.IntakeAction;
import frc.robot.Auto.Actions.ShootAction;
import frc.robot.Auto.Actions.MoveSwerve;
import frc.robot.Auto.Actions.WaitAction;
import frc.robot.Auto.Actions.ParallelAction;
import frc.robot.Auto.Actions.ParallelRaceAction;

/*
    This sets the state of the Intake to either "Standby", "Intaking", "Reverse",or "Disabled"
 */

public class DepotShootMission extends MissionBase {
    @Override
    public void routine() throws AutoMissionEndedException{
        // Move to depot while intaking (race action finishes when movement is done)
        runAction(new ParallelRaceAction(
            new MoveSwerve("DepotPath", true),
            new IntakeAction(999, "Intaking") // high timeout so it won't finish early
        ));

        runAction(new IntakeAction(2, "Intaking"));
        
        // Turn off intake completely before moving
        runAction(new IntakeAction(0.1, "Standby"));
        
        // Move to shoot position and spool up/shoot while moving
        runAction(new MoveSwerve("DepotToShootPath", false)); // don't reset odometry
        runAction(new ShootAction(4.0));
    }
    
        
        
     

}