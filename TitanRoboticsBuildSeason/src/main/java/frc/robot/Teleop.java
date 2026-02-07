package frc.robot;

import frc.robot.Devices.Controller;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Subsystems.Climber;
import frc.robot.Data.PortMap;
import frc.robot.Data.Constants;

public class Teleop {

    Controller driverController; //object of Controller

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

    public void driveBaseControl() {
        controllerLeftY = driverController.getLeftY(); //sets the variable controllerLeftY to the actual data coming from the controller
        controllerLeftX = driverController.getLeftX(); //sets the variable controllerLeftX to the actual data coming from the controller
        controllerRightX = driverController.getRightX(); //sets the variable controllerRightX to the actual data coming from the controller
        controllerRightY = driverController.getRightY(); //sets the variable controllerRightY to the actual data coming from the controller
        controllerAButton = driverController.getAButton();
        controllerRightBumper = driverController.getRightBumperButton(); //sets the variable controllerRightBumper to the actual data coming from the controller
    }
}