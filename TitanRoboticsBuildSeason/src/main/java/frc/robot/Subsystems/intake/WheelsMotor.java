package frc.robot.Subsystems.intake;


import frc.robot.Devices.NeoSparkMaxMotor;
import frc.robot.utils.Logger;


public class WheelsMotor extends NeoSparkMaxMotor {
    public WheelsMotor(int CANID) {
        super(CANID);
        Logger.info("Initializing intake wheel motor");
    }
}
