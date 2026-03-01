package frc.robot;

import frc.robot.Devices.Controller;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.Subsystems.SwerveBase;
import frc.robot.Data.PortMap;
import frc.robot.Data.Constants;

public class Teleop {

    Controller driverController; // object of Controller
    SwerveBase swerveBase; // object of SwerveBase
    Joystick joystickController; // object of joystick

    public static boolean JoystickEnabled = false;
    private double controllerLeftX; 
    private double controllerLeftY; 
    private double controllerRightX; 
    private double controllerRightY;
    private boolean controllerAButton; 
    private boolean controllerRightBumper; 
    
    // --- Architecture Variables ---
    private Rotation2d targetSnapHeading = new Rotation2d(); 
    private boolean isSnapMode = false;

    public Teleop() {
        swerveBase = SwerveBase.getInstance(); 
        if (JoystickEnabled == false){
            driverController = new Controller(PortMap.DRIVER_CONTROLLER);
        } else {
            joystickController = new Joystick(PortMap.DRIVER_CONTROLLER);
        }
    }

    public void teleopPeriodic() {
        driveBaseControl(); 
    }

    public void driveBaseControl() {
        boolean isFieldOriented = true;
        // Optional: Get alliance color if your targetSpeeds logic requires it
        boolean isRed = DriverStation.getAlliance().orElse(Alliance.Blue) == Alliance.Red;

        // 1. Get Inputs
        if (JoystickEnabled == false){
            controllerLeftY = driverController.getLeftY(); 
            controllerLeftX = driverController.getLeftX(); 
            controllerRightX = driverController.getRightX(); 
            controllerRightY = 0; 
            controllerAButton = driverController.getAButton();
            controllerRightBumper = driverController.getRightBumperButton(); 
        } else {
            controllerLeftY = joystickController.getY(); 
            controllerLeftX = joystickController.getX(); 
            controllerRightX = joystickController.getTwist(); 
            controllerRightY = 0; 
            controllerAButton = joystickController.getRawButton(1);
            controllerRightBumper = joystickController.getTop(); 
        }

        // 2. Calculate Translation (Forward/Strafe)
        double forward = 0; 
        double strafe = 0; 
        double manualRotation = 0;

        if (Math.abs(controllerLeftY) >= 0.1) {
            forward = -(controllerLeftY) * Constants.MAX_SPEED;
        }
        if (Math.abs(controllerLeftX) >= 0.1) {
            strafe = -(controllerLeftX) * Constants.MAX_SPEED;
        }

        // 3. Handle Aiming Override (Limelight)
        if (controllerRightBumper) {
            swerveBase.driveAndAim(new Translation2d(forward, strafe), 0, isFieldOriented);
            return; // Exit method here so we don't call the normal drive code below
        }

        // 4. Calculate Manual Rotation
        if (Math.abs(controllerRightX) >= 0.35) {
            manualRotation = -((Math.abs(controllerRightX)) * (controllerRightX)) * Constants.MAX_ROTATION_SPEED;
        }

        // 5. Handle Gyro Zeroing
        if (controllerAButton) {
            swerveBase.zeroGyro(); // Or zeroGyroWithAlliance() if your SwerveBase has that
            targetSnapHeading = new Rotation2d(); // Default to 0 degrees
            isSnapMode = true;
        }

        // 6. Check POV and Update Rotation State
        updateRotationState();

        // 7. Apply the Drive command
        applyDrive(forward, strafe, manualRotation, isFieldOriented, isRed);
    }

    // --- NEW HELPER METHODS ---

    private void updateRotationState() {
        // Get the POV value
        double pov = (JoystickEnabled) ? joystickController.getPOV() : driverController.getPOV();

        // If POV is pressed, enter snap mode
        if (pov != -1) {
            isSnapMode = true;
            // Negate POV because WPILib rotation is CCW, but POV is CW
            targetSnapHeading = Rotation2d.fromDegrees(-pov);
        } else {
            // If POV is released, fallback to manual right-stick rotation
            isSnapMode = false; 
        }
    }

    private void applyDrive(double finalForward, double finalStrafe, double manualRotation, boolean isFieldOriented, boolean isRed) {
        if (isSnapMode) {
            Rotation2d targetHeading = targetSnapHeading;

            // Optional: Flip target heading if on Red Alliance (uncomment if needed)
            // if (isRed) {
            //     targetHeading = targetHeading.plus(Rotation2d.fromDegrees(180));
            // }

            // Ask SwerveBase for the necessary rotation speed to hit the target heading
            edu.wpi.first.math.kinematics.ChassisSpeeds targetSpeeds = 
                swerveBase.getTargetSpeeds(finalForward, finalStrafe, targetHeading);

            swerveBase.drive(
                new Translation2d(finalForward, finalStrafe), 
                targetSpeeds.omegaRadiansPerSecond, 
                isFieldOriented
            );
            
        } else {
            // Standard manual driving
            swerveBase.drive(
                new Translation2d(finalForward, finalStrafe), 
                manualRotation, 
                isFieldOriented
            );
        }
    }
}