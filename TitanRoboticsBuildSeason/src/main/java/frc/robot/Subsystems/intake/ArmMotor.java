package frc.robot.Subsystems.intake;

import frc.robot.Devices.NeoSparkMaxMotor;

public class ArmMotor extends NeoSparkMaxMotor {
  
    public ArmMotor(int CANID) {
        super(CANID, true);
    }
}