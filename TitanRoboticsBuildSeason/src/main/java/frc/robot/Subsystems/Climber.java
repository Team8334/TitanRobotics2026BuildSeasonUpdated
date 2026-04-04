
package frc.robot.Subsystems;

import frc.robot.Interfaces.Subsystem;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/*
 * Class: Climber
 * Description: We use pneumatics to climb. The solenoids are the things that we
 *              control to make them move.
 * Note: Not used for the first competition and is not working - while we are not using a climber at all this year,
 *       continuing this project might be helpful for training in future years. 
 * Author: Josiah
 */

public class Climber implements Subsystem {
    private final DoubleSolenoid m_doubleSolenoidLeft;
    private final DoubleSolenoid m_doubleSolenoidRight;
    private final Compressor m_compressor;

    public String state = "STATIONARY";
    
    private static Climber instance = null;

    public static Climber getInstance() {
        if (instance == null) {
            instance = new Climber();
        }
        return instance;
    }

    public Climber() {
        m_doubleSolenoidLeft = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 7, 6);
        m_doubleSolenoidRight = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 5, 4);
        m_compressor = new Compressor(PneumaticsModuleType.CTREPCM);
        m_compressor.enableDigital();
        SubsystemManager.registerSubsystem(this);
    }

    public void update() {
        switch (state) {

            case "UP":
                m_doubleSolenoidLeft.set(DoubleSolenoid.Value.kForward);
                m_doubleSolenoidRight.set(DoubleSolenoid.Value.kForward);
                break;
            case "STATIONARY":
                m_doubleSolenoidLeft.set(DoubleSolenoid.Value.kOff);
                m_doubleSolenoidRight.set(DoubleSolenoid.Value.kOff);
                break;
            case "DOWN":
                m_doubleSolenoidLeft.set(DoubleSolenoid.Value.kReverse);
                m_doubleSolenoidRight.set(DoubleSolenoid.Value.kReverse);
                break;
        }
    }

    public void setState(String state){
     this.state =  state;    
    }

    public void initialize() {
    }

    public void log() {
    }

    public boolean isEnabled() {
        return true;
    }

    public String getName() {
        return "Climber";
    }

    public void enableCompressor() {
        m_compressor.enableDigital();
    }

    public void disableCompressor() {
        m_compressor.disable();
    }
}