package frc.robot;

import frc.robot.Devices.Controller;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.Subsystems.SwerveBase;
import frc.robot.Subsystems.Shooter.ShootingSolution;
import frc.robot.Data.PortMap;
import frc.robot.Data.Constants;
import frc.robot.Subsystems.Shooter;

public class Teleop {

    Controller driverController; // object of Controller for the driver
    Controller operatorController; // object of Controller for the operator
    SwerveBase swerveBase; // object of SwerveBase
    Shooter shooter;
    ShootingSolution shootingSolution;

    private double controllerLeftX; // variable for the left x joystick axis
    private double controllerLeftY; // variable for the left y joystick axis
    private double controllerRightX; // variable for the right x joystick axis
    private double controllerRightY;
    private double controllerRightTrigger; // axis
    private double controllerLeftTrigger;
    private boolean controllerAButton;
    private boolean controllerRightBumper; // variable for if the right bumper is pressed
    double rotationX;
    double rotationY;

    public Teleop() {
        driverController = new Controller(PortMap.DRIVER_CONTROLLER); // creates a new controller
        operatorController = new Controller(PortMap.OPERATOR_CONTROLLER);
        swerveBase = SwerveBase.getInstance(); // gets an instance of SwerveBase
        shooter = Shooter.getInstance();
    }

    public void teleopPeriodic() // everything in this method will get executed
    {
        driveBaseControl(); // executes the driveBaseControl method
    }

    public void driveBaseControl() {
        controllerLeftY = driverController.getLeftY(); // sets the variable controllerLeftY to the actual data coming
                                                       // from the controller
        controllerLeftX = driverController.getLeftX(); // sets the variable controllerLeftX to the actual data coming
                                                       // from the controller
        controllerRightX = driverController.getRightX(); // sets the variable controllerRightX to the actual data coming
                                                         // from the controller
        controllerRightY = driverController.getRightY(); // sets the variable controllerRightY to the actual data coming
                                                         // from the controller
        controllerAButton = driverController.getAButton();
        controllerRightBumper = driverController.getRightBumperButton(); // sets the variable controllerRightBumper to
                                                                         // the actual data coming from the controller

        double forward;
        double strafe; // Rhea this means going side to side
        double rotation = 0;

        boolean isFieldOrriented = true;

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
        if (Math.abs(controllerRightX) >= 0.1) {
            rotation = -((Math.abs(controllerRightX)) * (controllerRightX)) * Constants.MAX_ROTATION_SPEED;
        } else {
            rotation = 0;
        }

        if (Math.abs(controllerRightX) >= 0.5 || Math.abs(controllerRightY) >= 0.5) {
            rotationX = controllerRightX;
            rotationY = controllerRightY;
        }

        if (controllerAButton) {
            swerveBase.zeroGyro();
            rotationX = 0;
            rotationY = -1;
        }

        if (isFieldOrriented) // translation2d is used for lateral movement of the swerve drive
        {

            swerveBase.driveFieldOriented(
                    swerveBase.getTargetSpeeds(forward, strafe, new Rotation2d(-rotationY, -rotationX)));
            // swerveBase.drive(new Translation2d(forward,strafe), rotation, true);
        } else {
            swerveBase.drive(new Translation2d(forward, strafe), rotation, false);
        }
    }

    public void operatorControl() {
        controllerLeftTrigger = operatorController.getLeftTriggerAxis();
        controllerRightTrigger = operatorController.getRightTriggerAxis();
        controllerRightBumper = operatorController.getRightBumper();
        double forward;
        double strafe;

        if (controllerRightTrigger >= 0.5) {
            if (shootingSolution.shotPossibilty())
            shooter.ShooterStateProcessing();
        }
        if (controllerLeftTrigger > 0.05) {
            shooter.manualSpeed(controllerLeftTrigger);
        }
        if (controllerRightBumper) {
            if (shootingSolution.shotPossibilty()){
                shooter.prepareToShoot();

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
                swerveBase.getTargetSpeeds(forward, strafe, shootingSolution.shootingAngle());
            }

        }

    }
}