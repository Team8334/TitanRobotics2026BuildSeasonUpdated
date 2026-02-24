package frc.robot;

import frc.robot.Devices.Controller;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;

import frc.robot.Subsystems.SwerveBase;
import frc.robot.Subsystems.intake.IntakeMechanism;
import frc.robot.Subsystems.Shooter.ShootingSolution;
import frc.robot.Data.PortMap;
import frc.robot.Data.Constants;
import frc.robot.Subsystems.Shooter;

public class Teleop {

    Climber climber;
    Controller driverController; //object of Controller
    Controller operatorController; // object of Controller for the operator
    IntakeMechanism intakeMechanism;
    Shooter shooter;
    ShootingSolution shootingSolution;
    SwerveBase swerveBase; //object of SwerveBase
    Joystick joystickController; //object of joystick

    public static boolean JoystickEnabled = false;
    private double controllerLeftX; //variable for the left x joystick axis
    private double controllerLeftY; //variable for the left y joystick axis
    private double controllerRightX; //variable for the right x joystick axis
    private double controllerRightY;
    private boolean controllerAButton; 
    private boolean controllerRightBumper; //variable for if the right bumper is pressed
    double rotationX;
    double rotationY;

    public Teleop() {
        swerveBase = SwerveBase.getInstance(); //gets an instance of SwerveBase
        if (JoystickEnabled == false){
            driverController = new Controller(PortMap.DRIVER_CONTROLLER);
        }
        else{
            joystickController = new Joystick(PortMap.DRIVER_CONTROLLER);
        }
    }

    public void teleopPeriodic() // everything in this method will get executed
    {
        climberControl();
        driveBaseControl();
    }

        /*private void applyDrive(double finalForward, double finalStrafe, double manualRotation, boolean isRed) {
        if (isSnapMode) {
            Rotation2d targetHeading = (Math.abs(rotationX) < 1e-6 && Math.abs(rotationY) < 1e-6)
                    ? swerveBase.getPose().getRotation()
                    : new Rotation2d(-rotationY, -rotationX);

            if (isRed)
                targetHeading = targetHeading.plus(Rotation2d.fromDegrees(180));

            edu.wpi.first.math.kinematics.ChassisSpeeds targetSpeeds = swerveBase.getTargetSpeeds(finalForward,
                    finalStrafe, targetHeading);
            swerveBase.drive(new Translation2d(finalForward, finalStrafe), targetSpeeds.omegaRadiansPerSecond,
                    Dashboard.isFieldOrientedEnabled());
            logSnap(targetHeading, targetSpeeds.omegaRadiansPerSecond);
        } else {
            swerveBase.drive(new Translation2d(finalForward, finalStrafe), manualRotation,isFieldOrriented)
        }
        }*/

    public void climberControl(){
        controllerXButton = driverController.getXButton();
        controllerBButton = driverController.getBButton();

        if (controllerXButton){
            climber.setState("UP");
        }
        else if(controllerBButton){
            climber.setState("DOWN");
        }
        else {
            climber.setState("STATIONARY");
        }
        //climber.setState("STATIONARY");
    }

    public void IntakeControl() {
        if (driverController.getYButton()) {
            intakeMechanism.setState("Standby");
        }

       if (driverController.getAButton()) {  
        intakeMechanism.setState("Intaking");
       }

       if (driverController.getBButton()) {
        intakeMechanism.setState("Reversed");
       }

       if (driverController.getXButton()) {
        intakeMechanism.setState("Stop");
       }
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
            swerveBase.driveAndAim(new Translation2d(forward, strafe), 0, isFieldOrriented);
            return; // Exit method here so we don't call the normal drive code below
        }

        // NORMAL MODE: Standard joystick rotation
        if (Math.abs(controllerRightX) >= 0.1) {
            rotation = -((Math.abs(controllerRightX)) * (controllerRightX)) * Constants.MAX_ROTATION_SPEED;
        } else {
            rotation = 0;
        }

       /*  if (Math.abs(controllerRightX) >= 0.5 || Math.abs(controllerRightY) >= 0.5)
        {
            rotationX = controllerRightX;
            rotationY = controllerRightY;
        } */
        if (controllerAButton)
        {
            swerveBase.zeroGyro();
            rotationX = 0;
            rotationY = -1;
        }
        

        if(isFieldOrriented) //translation2d is used for lateral movement of the swerve drive
        {
            //swerveBase.driveFieldOriented(swerveBase.getTargetSpeeds(forward, strafe, new Rotation2d(-rotationY, -rotationX)));
            swerveBase.drive(new Translation2d(forward,strafe), rotation, true);
        }
        else 
        {
            swerveBase.drive(new Translation2d(forward,strafe), rotation, false);
        }
    }

    public void operatorControl() {
        controllerLeftTrigger = operatorController.getLeftTriggerAxis();
        controllerRightTrigger = operatorController.getRightTriggerAxis();
        controllerRightBumper = operatorController.getRightBumper();
        double forward;
        double strafe;

        shooter.setTargetRPM(shootingSolution.flywheelRPM());

        if (controllerRightTrigger >= 0.5) {
            if (shootingSolution.shotPossibilty())
            shooter.shoot();
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
                swerveBase.driveFieldOriented(swerveBase.getTargetSpeeds(forward, strafe, shootingSolution.shootingAngle()));
            }

        }

    }
}
