
    package frc.robot.Subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Data.Constants;
import frc.robot.Data.LEDColorConstants;
import frc.robot.Interfaces.Subsystem;
import frc.robot.Subsystems.SubsystemManager;

public class LED implements Subsystem {

    private static LED instance = null;

    private final MotorController LEDlightstrip;

    public double color;

    
    public static LED getInstance() {
        if (instance == null) {
            instance = new LED();
        }
        return instance;
    }

    public LED()
    {
        LEDlightstrip = new Spark(1);
    }

    public void setColor(double color){
        this.color = color;
        LEDlightstrip.set(color);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void update() {
       setColor(color);
    }

    @Override
    public void log() {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "LEDs";
    }
}
