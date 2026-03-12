package frc.robot.Auto.Actions;

import frc.robot.Interfaces.Subsystem;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.Interfaces.Actions;
import frc.robot.Subsystems.Climber;
import frc.robot.Interfaces.*;
import frc.robot.Robot;

public class ClimberAction implements Actions {
    private DoubleSolenoid m_doubleSolenoidLeft;
    private Compressor m_compressor;
    private Climber climber = null;
    public String state;
    public boolean done = false;
    public ClimberAction(String state) {
       climber = Climber.getInstance();
       this.state = state;
    }

    @Override
    public void start() {
        climber.setState(state);
        this.done = true;
    }

    @Override
    public void update() {

    }

    @Override
    public boolean isFinished() {
        return done;
    }

    @Override
    public void done() {
    }
}