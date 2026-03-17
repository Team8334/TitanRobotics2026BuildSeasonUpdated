package frc.robot.Auto.Missions;

import edu.wpi.first.math.geometry.Pose2d;
//import these so that the mission is an option when testing
import frc.robot.Auto.AutoMissionChooser;
import frc.robot.Auto.AutoMissionEndedException;

// import the actions from the auto.actions folder
import frc.robot.Auto.Actions.WaitAction;
import frc.robot.Auto.Actions.MoveSwerve;
import frc.robot.Auto.Actions.ShootAction;

/*
 * Class: ShooterMission
 * Description: This mission is to shoot our eight fuel into the hub using a
 *              Choreo movement and then a shoot action.
 * Notes: Choreo (the path) can be changed any time, just remember to generate
 *        the code and deploy the new code
 * Author: Rhea Sneller
 */

public class ShooterMission extends MissionBase {
    @Override
    protected void routine() throws AutoMissionEndedException {
       
        //Possible rpm and distance measurements
        //*4000 hit the ceiling
        //115 inches (back of bot without bumpers to our hub wall) at *3500
        //96.5 inches (back of bot without bumpers to our hub wall) at *3000
        //1800 drops the fuel just in front of the robot in case you need to hopper dump

        // runAction(new MoveSwerve("Shooter", true));
        runAction(new ShootAction(5, 3500)); 
    }
}