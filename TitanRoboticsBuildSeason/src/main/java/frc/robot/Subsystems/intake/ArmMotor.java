package frc.robot.Subsystems.intake;

import frc.robot.Devices.NeoSparkMaxMotor;

/*
 * Class: Arm Motor
 * Description: Creates an arm motor used to move the intake up and down
 * Author: Mai
 */

public class ArmMotor extends NeoSparkMaxMotor {
  
    public ArmMotor(int CANID) {
        super(CANID, true);
    }
}