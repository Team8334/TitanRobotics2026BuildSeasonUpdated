package frc.robot;

import frc.robot.Devices.Controller;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Subsystems.Climber;
import frc.robot.Subsystems.SwerveBase;
import frc.robot.Subsystems.intake.IntakeMechanism;
import frc.robot.Data.PortMap;
import frc.robot.Data.Constants;

public class Teleop {

    Controller driverController; //object of Controller
    SwerveBase swerveBase; //object of SwerveBase
    IntakeMechanism intakeMechanism;

    private double controllerLeftX; //variable for the left x joystick axis
    private double controllerLeftY; //variable for the left y joystick axis
    private double controllerRightX; //variable for the right x joystick axis
    private double controllerRightY;
    private boolean controllerAButton; 
    private boolean controllerRightBumper; //variable for if the right bumper is pressed
    private boolean controllerXButton;
    private boolean controllerBButton;
    double rotationX;
    double rotationY;



    
    Climber climber;

    public Teleop() {
        driverController = new Controller(PortMap.DRIVER_CONTROLLER); //creates a new controller
        //swerveBase = SwerveBase.getInstance(); //gets an instance of SwerveBase
        climber = Climber.getInstance();
        swerveBase = SwerveBase.getInstance(); //gets an instance of SwerveBase
        intakeMechanism = IntakeMechanism.getInstance();
    }

    public void teleopPeriodic() //everything in this method will get executed 
    {
        climberControl();
    }

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
        controllerLeftY = driverController.getLeftY(); //sets the variable controllerLeftY to the actual data coming from the controller
        controllerLeftX = driverController.getLeftX(); //sets the variable controllerLeftX to the actual data coming from the controller
        controllerRightX = driverController.getRightX(); //sets the variable controllerRightX to the actual data coming from the controller
        controllerRightY = driverController.getRightY(); //sets the variable controllerRightY to the actual data coming from the controller
        controllerAButton = driverController.getAButton();
        controllerRightBumper = driverController.getRightBumperButton(); //sets the variable controllerRightBumper to the actual data coming from the controller

        double forward; 
        double strafe; //Rhea this means going side to side
        double rotation = 0;
        
        boolean isFieldOrriented = true;

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
            rotation = ((Math.abs(controllerRightX))*(controllerRightX)) * Constants.MAX_ROTATION_SPEED;
        } else {
            rotation = 0;
        }

        /*if (Math.abs(controllerRightX) >= 0.5 || Math.abs(controllerRightY) >= 0.5)
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
                
            //swerveBase.driveFieldOriented(swerveBase.getTargetSpeeds(forward, strafe, new Rotation2d(-rotationX, -rotationY)));
            swerveBase.drive(new Translation2d(forward,strafe), rotation, true);
        }
        else 
        {
            swerveBase.drive(new Translation2d(forward,strafe), rotation, false);
        }
    }
}