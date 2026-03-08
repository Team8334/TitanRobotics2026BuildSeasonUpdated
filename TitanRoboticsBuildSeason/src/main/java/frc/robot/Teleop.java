package frc.robot;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Joystick;

import frc.robot.Data.Constants;
import frc.robot.Data.PortMap;
import frc.robot.Devices.Controller;
import frc.robot.Subsystems.Shooter;
import frc.robot.Subsystems.Shooter.ShootingSolution;
import frc.robot.Subsystems.SwerveBase;
import frc.robot.Subsystems.intake.Hopper;
import frc.robot.Subsystems.intake.IntakeMechanism;

public class Teleop {

    // Subsystems
    SwerveBase swerveBase;
    IntakeMechanism intakeMechanism;
    Shooter shooter;
    Hopper hopper;

    // Controllers
    Controller driverController;
    Controller operatorController;
    Joystick joystickController;

    // Configuration
    public static boolean joystickEnabled = false;

    // Driver State
    private double driverLeftX;
    private double driverLeftY;
    private double driverRightX;
    private boolean driverAButton;

    // Operator State
    private double operatorRightY;
    private double operatorLeftTrigger;
    private double operatorRightTrigger;
    private boolean operatorXButton;
    private boolean operatorYButton;
    private boolean operatorRightBumper;
    private int operatorPOV;
    private double operatorLeftY;
    private boolean operatorLeftStickButton;
    private boolean operatorRightStickButton;

    // Intake Toggle State
    private String intakeToggleState = "Disabled"; // Start disabled until first interaction
    private boolean lastOperatorXButton = false;

    double rotationX;
    double rotationY;
    ShootingSolution shootingSolution;

    public Teleop() {
        swerveBase = SwerveBase.getInstance();
        shooter = Shooter.getInstance();
        intakeMechanism = IntakeMechanism.getInstance();
        hopper = Hopper.getInstance();

        operatorController = new Controller(PortMap.OPERATOR_CONTROLLER);

        if (!joystickEnabled) {
            driverController = new Controller(PortMap.DRIVER_CONTROLLER);
        } else {
            joystickController = new Joystick(PortMap.DRIVER_CONTROLLER);
        }
    }

    public void init() {
        intakeToggleState = "Disabled";
        intakeMechanism.setState("Disabled");
    }

    public void teleopPeriodic() {
        readControllers();
        driveBaseControl();
        intakeControl();
        operatorControl();
    }

    private void readControllers() {
        // Read Operator Controller
        operatorRightY = operatorController.getRightY();
        operatorLeftTrigger = operatorController.getLeftTriggerAxis();
        operatorRightTrigger = operatorController.getRightTriggerAxis();
        operatorXButton = operatorController.getXButton();
        operatorYButton = operatorController.getYButton();
        operatorRightBumper = operatorController.getRightBumperButton();
        operatorPOV = operatorController.getPOV();
        operatorLeftY = operatorController.getLeftY();
        operatorLeftStickButton = operatorController.getLeftStickButton();
        operatorRightStickButton = operatorController.getRightStickButton();

        // Read Driver Controller
        if (!joystickEnabled) {
            driverLeftY = driverController.getLeftY();
            driverLeftX = driverController.getLeftX();
            driverRightX = driverController.getRightX();
            driverAButton = driverController.getAButton();
        } else {
            driverLeftY = joystickController.getY();
            driverLeftX = joystickController.getX();
            // Assuming getTwist() mapped to driverRightX
            driverRightX = joystickController.getTwist();
            driverAButton = joystickController.getRawButton(1);
        }
    }

    public void intakeControl() {
        System.out.println(intakeToggleState);
        // --- Intake Toggle (X Button) ---
        // Determines if the arm should be Down or in Standby (Up)
        if (operatorXButton && !lastOperatorXButton) {
            if (intakeToggleState.equals("Standby")) {
                intakeToggleState = "Down"; 
            } else {
                intakeToggleState = "Standby";
            }
        }
        lastOperatorXButton = operatorXButton;

        // --- Roller and Arm Mapping ---
        boolean intakeRequested = operatorLeftTrigger > 0.5;
        boolean reverseRequested = operatorYButton && intakeRequested;
        boolean armDown = intakeToggleState.equals("Down");

        if (reverseRequested) {
            if (intakeToggleState.equals("Disabled")) intakeToggleState = "Standby";
            intakeMechanism.setState(armDown ? "Reversed" : "StandbyReversed");
        } else if (intakeRequested) {
            if (intakeToggleState.equals("Disabled")) intakeToggleState = "Standby";
            intakeMechanism.setState(armDown ? "Intaking" : "StandbyIntaking");
        } else if (!intakeToggleState.equals("Disabled")) {
            intakeMechanism.setState(armDown ? "Down" : "Standby");
        } else {
            intakeMechanism.setState("Disabled");
        }

        //add manual contorl here (see intake mechanism for implementation)
        //if(operatorRightStickButton){
        //    intakeMechanism.manualIntakeControl(operatorRightY);
        //}

        // E-stop check
       /*  if (operatorPOV == 180) {
            intakeMechanism.setState("Disabled");
            intakeToggleState = "Disabled";
        }*/
        
            // Using magnitude of right stick or just Y
            if (Math.abs(operatorLeftY) >= 0.1) {
                hopper.setSpeed(operatorLeftY/2); // previously divided by 10
            } else {
                hopper.setSpeed(0);
            }

    }

    public void driveBaseControl() {
        boolean isFieldOriented = true;

        double forward;
        double strafe;
        double rotation = 0;

        // E-Stop: Down on D-Pad from Operator
        if (operatorPOV == 180) {
            intakeMechanism.setState("Disabled");
            shooter.stop();
            hopper.setSpeed(0);
        }

        // --- Driving ---
        // Left JS: Y is forward/backward, X is strafe left/right
        if (Math.abs(driverLeftY) >= 0.1) {
            forward = -driverLeftY * Constants.MAX_SPEED;
        } else {
            forward = 0;
        }

        if (Math.abs(driverLeftX) >= 0.1) {
            strafe = -driverLeftX * Constants.MAX_SPEED;
        } else {
            strafe = 0;
        }

        // Right JS: X is rotate left/right
        if (Math.abs(driverRightX) >= 0.1) {
            rotation = -(Math.abs(driverRightX) * driverRightX) * Constants.MAX_ROTATION_SPEED;
        } else {
            rotation = 0;
        }

        // A Button: Zero Gyro (set current head as forward)
        if (driverAButton) {
            swerveBase.zeroGyroWithAlliance();
            rotationX = 0;
            rotationY = -1;
        }

        // Apply Drive
        if (isFieldOriented) {
            swerveBase.drive(new Translation2d(forward, strafe), rotation, true);
        } else {
            swerveBase.drive(new Translation2d(forward, strafe), rotation, false);
        }
    }

    public void operatorControl() {
        shootingSolution = shooter.calculateShootingSolution(swerveBase.getPose());

        // Y + Left Trigger: Reverse shooter
        if (operatorYButton && operatorRightTrigger > 0.5) {
            // If shooter has a reverse method, call it here. 
            // Workaround: negative manual speed
            shooter.manualSpeed(operatorRightTrigger); 
        } 
        // Left Trigger: Aim, Shoot
        else if (operatorRightTrigger > 0.5) {
            if (shootingSolution != null) {
                shooter.setTargetRPM(shootingSolution.flywheelRPM());
                
                if (shootingSolution.shotPossibilty()) {
                    if (Math.abs(shootingSolution.shootingAngle().minus(swerveBase.getHeading()).getDegrees()) < 3) {
                        shooter.shoot();
                    } else {
                        shooter.prepareToShoot();
                    }
                    // Auto-aim Swerve override
                    swerveBase.driveFieldOriented(swerveBase.getTargetSpeeds(0, 0, shootingSolution.shootingAngle()));
                }
            } else {
                // Manual fallback if no vision
                shooter.manualSpeed(operatorRightTrigger);
                shooter.shoot();
            }
        } else {
            // Stop shooter if nothing pressed, unless E-Stop overrides it
            if (operatorPOV != 180) {
                shooter.stop();
            }
        }
    }
}