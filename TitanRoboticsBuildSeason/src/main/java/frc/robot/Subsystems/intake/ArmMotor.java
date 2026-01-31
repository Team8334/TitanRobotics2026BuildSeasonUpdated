package frc.robot.Subsystems.intake;

import frc.robot.Devices.NeoSparkMaxMotor;
import frc.robot.utils.Logger;


public class ArmMotor extends NeoSparkMaxMotor {


   
    public ArmMotor(int CANID) {
        super(CANID);
        Logger.info("Initializing intake arm motor");
    }
}
