package frc.robot;

import frc.robot.Devices.Controller;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.Subsystems.SwerveBase;
import frc.robot.Data.Constants;

public class Teleop {

    Controller driverController; // object of Controller
    SwerveBase swerveBase; // object of SwerveBase

    private double controllerLeftX; // variable for the left x joystick axis
    private double controllerLeftY; // variable for the left y joystick axis
    private double controllerRightX; // variable for the right x joystick axis
    private double controllerRightY;
    private boolean controllerAButton;
    private boolean controllerRightBumper; // variable for if the right bumper is pressed
    double rotationX;
    double rotationY;

    public Teleop() {
        driverController = new Controller(PortMap.DRIVER_CONTROLLER); // creates a new controller
        swerveBase = SwerveBase.getInstance(); // gets an instance of SwerveBase
    }

    public void teleopPeriodic() // everything in this method will get executed
    {
        driveBaseControl(); // executes the driveBaseControl method
    }

    public void driveBaseControl() {
        controllerLeftY = driverController.getLeftY();
        controllerLeftX = driverController.getLeftX();
        controllerRightX = driverController.getRightX();
        controllerRightY = driverController.getRightY();
        controllerAButton = driverController.getAButton();
        // Using Right Bumper for Aiming
        controllerRightBumper = driverController.getRightBumper();

        double forward;
        double strafe;
        double rotation = 0;

        boolean isFieldOriented = true;

        // --- Translation Logic (Left Stick) ---
        if (Math.abs(controllerLeftY) >= 0.1) {
            forward = -(controllerLeftY) * Constants.MAX_SPEED;
        } else {
            forward = 0;
        }
        if (Math.abs(controllerLeftX) >= 0.1) {
            strafe = -(controllerLeftX) * Constants.MAX_SPEED;
        } else {
            strafe = 0;
        }

        // --- Rotation Logic (Right Stick OR Limelight) ---
        if (controllerRightBumper) {
            // AIMING MODE: Override rotation with the Limelight method
            // (Make sure you added the driveAndAim logic to SwerveBase as discussed!)
            swerveBase.driveAndAim(new Translation2d(forward, strafe), 0, isFieldOriented);
            return; // Exit method here so we don't call the normal drive code below
        }

        // NORMAL MODE: Standard joystick rotation
        if (Math.abs(controllerRightX) >= 0.1) {
            rotation = -((Math.abs(controllerRightX)) * (controllerRightX)) * Constants.MAX_ROTATION_SPEED;
        } else {
            rotation = 0;
        }

        // ... rest of your gyro zeroing logic ...
        if (controllerAButton) {
            swerveBase.zeroGyro();
        }

        // Final Drive call for normal mode
        swerveBase.drive(new Translation2d(forward, strafe), rotation, isFieldOriented);
    }
}
