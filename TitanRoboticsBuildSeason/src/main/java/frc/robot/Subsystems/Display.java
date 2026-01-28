package frc.robot.Subsystems;

import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Interfaces.Subsystem;
import frc.robot.Subsystems.*;
import frc.robot.Subsystems.SubsystemManager;
import swervelib.SwerveDrive;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;

public class Display implements Subsystem {

    private static Display instance = null;

    private final Field2d m_field = new Field2d();
    SwerveBase swerveBase;

    public static Display getInstance() {
        if (instance == null) {
            instance = new Display();
        }
        return instance;
    }

    public Display() {

        SubsystemManager.registerSubsystem(this);
        swerveBase = SwerveBase.getInstance();

    }

    public void pushPosition() {
        // Do this in either robot or subsystem init
        SmartDashboard.putData("Field", m_field);
        // Do this in either robot periodic or subsystem periodic
        m_field.setRobotPose(swerveBase.getPose());
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initialize'");
    }

    @Override
    public void log() {
        // TODO Auto-generated method stub
        SmartDashboard.updateValues();
        throw new UnsupportedOperationException("Unimplemented method 'log'");
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isEnabled'");
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

}
