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
import frc.robot.Subsystems.intake.IntakeMechanism;

public class Teleop {

    // Subsystems
    IntakeMechanism intakeMechanism;
    Shooter shooter;
    SwerveBase swerveBase;

    // Controllers
    Controller driverController;
    Controller operatorController;
    Joystick joystickController;

    // Controller Configuration
    public static boolean joystickEnabled = false;

    // Driver States
    private double driverLeftX;
    private double driverLeftY;
    private double driverRightX;
    private boolean driverAButton;
    private boolean driverBButton;
    private boolean driverRightBumper;

    // Operator States
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

    // Intake Toggle States
    private String intakeToggleState = "Disabled"; // Start disabled until first interaction
    private boolean lastOperatorXButton = false;
    private boolean driverArmOverrideActive = false;
    private boolean lastDriverBButton = false;

    // Drive Variables
    double rotationX;
    double rotationY;
    private double driverForward;
    private double driverStrafe;

    // Shooter
    ShootingSolution shootingSolution;

    public Teleop() {
        intakeMechanism = IntakeMechanism.getInstance();
        shooter = Shooter.getInstance();
        swerveBase = SwerveBase.getInstance();

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
            driverBButton = driverController.getBButton();
            driverRightBumper = driverController.getRightBumperButton();
        } else {
            driverLeftY = joystickController.getY();
            driverLeftX = joystickController.getX();
            // Assuming getTwist() mapped to driverRightX
            driverRightX = joystickController.getTwist();
            driverAButton = joystickController.getRawButton(1);
            driverBButton = joystickController.getRawButton(2);
            driverRightBumper = joystickController.getRawButton(6);
        }
    }

    public void intakeControl() {
        // --- Driver Arm Lock Toggle (B Button) ---
        if (driverBButton && !lastDriverBButton) {
            driverArmOverrideActive = !driverArmOverrideActive;
        }
        lastDriverBButton = driverBButton;

        // --- Intake Toggle (X Button) ---
        // Determines if the arm should be Down or in Standby (Up)
        if (operatorXButton && !lastOperatorXButton) {
            if (!driverArmOverrideActive) {
                if (intakeToggleState.equals("Standby")) {
                    intakeToggleState = "Down"; 
                } else {
                    intakeToggleState = "Standby";
                }
            }
        }
        lastOperatorXButton = operatorXButton;

        if (driverArmOverrideActive) {
            intakeToggleState = "Standby";
        }

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
        } */
    }

    public void driveBaseControl() {
        boolean isFieldOriented = true;

        double rotation = 0;

        // E-Stop: Down on D-Pad from Operator
        if (operatorPOV == 180) {
            intakeMechanism.setState("Disabled");
            shooter.stop();
        }

        // --- Driving ---
        // Left JS: Y is forward/backward, X is strafe left/right
        if (Math.abs(driverLeftY) >= 0.1) {
            driverForward = driverLeftY * Constants.MAX_SPEED;
        } else {
            driverForward = 0;
        }

        if (Math.abs(driverLeftX) >= 0.1) {
            driverStrafe = driverLeftX * Constants.MAX_SPEED;
        } else {
            driverStrafe = 0;
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
            swerveBase.drive(new Translation2d(driverForward, driverStrafe), rotation, true);
        } else {
            swerveBase.drive(new Translation2d(driverForward, driverStrafe), rotation, false);
        }
    }

    public void operatorControl() {
        shootingSolution = shooter.calculateShootingSolution(swerveBase.getPose());

        boolean autoRequested = !driverRightBumper && operatorRightTrigger > 0.05 && !operatorYButton;
        boolean manualRequested = !driverRightBumper && operatorRightTrigger > 0.05 && operatorYButton;
            
        if (driverRightBumper) {
            shooter.stop();
        } else if (autoRequested) {
            if (shootingSolution != null && shootingSolution.shotPossibility()) {
                // Auto-aim Swerve override (allow translation while overriding rotation)
                swerveBase.driveFieldOriented(swerveBase.getTargetSpeeds(driverForward, driverStrafe, shootingSolution.shootingAngle()));
                
                // Start flywheels while lining up
                shooter.setTargetRPM(shootingSolution.flywheelRpmLeft(), shootingSolution.flywheelRpmRight());
                
                if (Math.abs(shootingSolution.shootingAngle().minus(swerveBase.getHeading()).getDegrees()) < 3) {
                    shooter.shoot();
                } else {
                    shooter.prepareToShoot();
                }
            } else {
                // Valid auto requested but shot is impossible from here
                shooter.stop();
            }
        } else if (manualRequested) {
            shooter.manualFire(operatorRightTrigger);
        } else {
            // Stop shooter if nothing pressed, unless E-Stop overrides it
            if (operatorPOV != 180) {
                shooter.stop();
            }
        }
    }
}