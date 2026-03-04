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
import frc.robot.Subsystems.Shooter;
import frc.robot.Subsystems.Shooter.ShootingSolution;
import frc.robot.Subsystems.SwerveBase;
import frc.robot.Subsystems.intake.Hopper;
import frc.robot.Subsystems.intake.IntakeMechanism;
import frc.robot.Data.PortMap;
import frc.robot.Data.Constants;

public class Teleop {

    Controller driverController; //object of Controller
    Controller operatorController; // object of Controller for the operator
    IntakeMechanism intakeMechanism; // object of Intake Mechanism
    SwerveBase swerveBase; //object of SwerveBase
    Joystick joystickController; //object of joystick
    Shooter shooter;
    ShootingSolution shootingSolution;
    Controller opController;
    Hopper hopper;

    public static boolean JoystickEnabled = false;
    private double controllerLeftX; //variable for the left x joystick axis
    private double controllerLeftY; //variable for the left y joystick axis
    private double controllerRightX; //variable for the right x joystick axis
    private double controllerRightTrigger; // axis
    private double controllerLeftTrigger; //variable for if the left trigger is pressed
    private double controllerRightY;
    private boolean controllerAButton; 
    private boolean controllerRightBumper; //variable for if the right bumper is pressed
    private double opControllerLeftY;
    double rotationX;
    double rotationY;

    public Teleop() {
        swerveBase = SwerveBase.getInstance(); //gets an instance of SwerveBase
        operatorController = new Controller(PortMap.OPERATOR_CONTROLLER);
        swerveBase = SwerveBase.getInstance(); // gets an instance of SwerveBase
        shooter = Shooter.getInstance();
        intakeMechanism = IntakeMechanism.getInstance();
        hopper = Hopper.getInstance();
        
        if (JoystickEnabled == false){
            driverController = new Controller(PortMap.DRIVER_CONTROLLER);
        }
        else{
            joystickController = new Joystick(PortMap.DRIVER_CONTROLLER);
        }
    }

    public void teleopPeriodic() //everything in this method will get executed 
    {
        driveBaseControl(); //executes the driveBaseControl method
        IntakeControl(); 
        operatorControl(); // executes the operatorControl method
    }

    public void IntakeControl() {
        if (driverController.getYButton()) {
            intakeMechanism.setState("Standby");
        }

       if (driverController.getBButton()) {  
        intakeMechanism.setState("Intaking");
       }

       if (driverController.getAButton()) {
        intakeMechanism.setState("Reversed");
       }

       if (driverController.getXButton()) {
        intakeMechanism.setState("Disabled");
       }
    }
    
    public void driveBaseControl() {

        boolean isFieldOrriented = true;

        if (JoystickEnabled == false){
            controllerLeftY = driverController.getLeftY(); //sets the variable controllerLeftY to the actual data coming from the controller
            controllerLeftX = driverController.getLeftX(); //sets the variable controllerLeftX to the actual data coming from the controller
            controllerRightX = driverController.getRightX(); //sets the variable controllerRightX to the actual data coming from the controller
            controllerRightY = 0; //sets the variable controllerRightY to the actual data coming from the controller
            controllerAButton = driverController.getAButton();
            controllerRightBumper = driverController.getRightBumperButton(); //sets the variable controllerRightBumper to the actual data coming from the controller
        }
        else{
            controllerLeftY = joystickController.getY(); //sets the variable controllerLeftY to the actual data coming from the controller
            controllerLeftX = joystickController.getX(); //sets the variable controllerLeftX to the actual data coming from the controller
            controllerRightX = joystickController.getTwist(); //sets the variable controllerRightX to the actual data coming from the controller
            controllerRightY = 0; //sets the variable controllerRightY to the actual data coming from the controller
            controllerAButton = joystickController.getRawButton(1);
            controllerRightBumper = joystickController.getTop(); //sets the variable controllerRightBumper to the actual data coming from the controller


        }

        double forward; 
        double strafe; //Rhea this means going side to side
        double rotation = 0;

        if (Math.abs(controllerLeftY) >= 0.1) {
            forward = (controllerLeftY) * Constants.MAX_SPEED;
        } else {
            forward = 0;
        }
        if (Math.abs(controllerLeftX) >= 0.1) {
            strafe = (controllerLeftX) * Constants.MAX_SPEED;
        } else {
            strafe = 0;
        }
        if (Math.abs(controllerRightX) >= 0.1) {
            rotation = -((Math.abs(controllerRightX))*(controllerRightX)) * Constants.MAX_ROTATION_SPEED;
        } else {
            rotation = 0;
        }

       /*  if (Math.abs(controllerRightX) >= 0.5 || Math.abs(controllerRightY) >= 0.5)
        {
            rotationX = controllerRightX;
            rotationY = controllerRightY;
        } 
        */
        if (controllerAButton)
        {
            swerveBase.zeroGyro();
            rotationX = 0;
            rotationY = -1;
        }
        
        if (Math.abs(opControllerLeftY) >=0.1) {
            hopper.setSpeed(opControllerLeftY);

        }

        if(isFieldOrriented) //translation2d is used for lateral movement of the swerve drive
        {
            //swerveBase.driveFieldOriented(swerveBase.getTargetSpeeds(forward, strafe, new Rotation2d(-rotationY, -rotationX)));
            swerveBase.drive(new Translation2d(forward,strafe), rotation, false);
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

        
        if (controllerRightTrigger >= 0.5) {
            if (shootingSolution.shotPossibilty())
            shooter.shoot();
        }
        if (controllerLeftTrigger > 0.05) {
            shooter.manualSpeed(controllerLeftTrigger);
        }
        if (controllerAButton) {
            shooter.stop();
        }

        shootingSolution = shooter.calculateShootingSolution(swerveBase.getPose());
        
        shooter.setTargetRPM(shootingSolution.flywheelRPM());
        if (controllerRightBumper) {
            if (shootingSolution.shotPossibilty()){
                if (Math.abs(shootingSolution.shootingAngle().minus(swerveBase.getHeading()).getDegrees())<3){
                    shooter.shoot();
                }
                else {
                    shooter.prepareToShoot();
                }
                swerveBase.driveFieldOriented(swerveBase.getTargetSpeeds(0, 0, shootingSolution.shootingAngle()));
            }
        }
    }
}