package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Subsystems.SwerveBase;

public class RobotContainer {

    private final SwerveBase swerveBase;
    private final SendableChooser<Command> autoChooser;

    public RobotContainer() {
        // Instantiate the subsystem so AutoBuilder gets configured
        swerveBase = SwerveBase.getInstance();

        // Create the SendableChooser for Auto routines using PathPlanner's AutoBuilder
        autoChooser = AutoBuilder.buildAutoChooser();

        // Put the chooser on the SmartDashboard
        SmartDashboard.putData("Auto Chooser", autoChooser);
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // Return the selected command
        return autoChooser.getSelected();
    }
}
