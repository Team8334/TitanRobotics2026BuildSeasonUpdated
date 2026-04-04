package frc.robot.Subsystems.intake;

import frc.robot.Interfaces.Subsystem;
import frc.robot.Subsystems.SubsystemManager;
import frc.robot.Data.PortMap;
import frc.robot.Devices.ModifiedEncoder;
import frc.robot.Devices.NeoSparkMaxMotor;

/*
 * Class: Hopper
 * Description: The hopper gets the ball from the intake and to the shooter.
 *              We just turn a motor for this.
 * Author: Mai, Sarah
 */

public class Hopper implements Subsystem {
    
    private static Hopper instance = null;

     public static Hopper getInstance() {
        if (instance == null) {
            instance = new Hopper();
        }
        return instance;
    }

    public NeoSparkMaxMotor hopperMotor;

    public Hopper() {
    hopperMotor = new NeoSparkMaxMotor(PortMap.HOPPER_MOTOR_CANID);
    }

    public void setSpeed(double speed) {
        hopperMotor.set(speed);
    }

    public double voltage; 


    public void update() {
    }

    public void initialize() {
    }

    public void log() {
    }

    public boolean isEnabled() {
        return true;
    }

    public String getName() {
        return "Hopper";
    }
}
