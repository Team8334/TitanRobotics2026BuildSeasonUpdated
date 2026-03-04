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
    private double driverRightY;
    private boolean driverAButton;
    private boolean driverRightBumper;

    // Operator State
    private double operatorLeftY;
    private double operatorLeftTrigger;
    private double operatorRightTrigger;
    private boolean operatorAButton;
    private boolean operatorRightBumper;

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

    public void teleopPeriodic() {
        readControllers();
        driveBaseControl();
        intakeControl();
        operatorControl();
    }

    private void readControllers() {
        // Read Operator Controller
        operatorLeftY = operatorController.getLeftY();
        operatorLeftTrigger = operatorController.getLeftTriggerAxis();
        operatorRightTrigger = operatorController.getRightTriggerAxis();
        operatorAButton = operatorController.getAButton();
        operatorRightBumper = operatorController.getRightBumper();

        // Read Driver Controller
        if (!joystickEnabled) {
            driverLeftY = driverController.getLeftY();
            driverLeftX = driverController.getLeftX();
            driverRightX = driverController.getRightX();
            driverRightY = 0;
            driverAButton = driverController.getAButton();
            driverRightBumper = driverController.getRightBumperButton();
        } else {
            driverLeftY = joystickController.getY();
            driverLeftX = joystickController.getX();
            // Assuming getTwist() mapped to driverRightX
            driverRightX = joystickController.getTwist();
            driverRightY = 0;
            driverAButton = joystickController.getRawButton(1);
            driverRightBumper = joystickController.getTop();
        }
    }

    public void intakeControl() {
        // Intake uses driver buttons standardly per original code
        if (driverController != null) {
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
    }

    public void driveBaseControl() {
        boolean isFieldOriented = true;

        double forward;
        double strafe;
        double rotation = 0;

        if (Math.abs(driverLeftY) >= 0.1) {
            forward = driverLeftY * Constants.MAX_SPEED;
        } else {
            forward = 0;
        }

        if (Math.abs(driverLeftX) >= 0.1) {
            strafe = driverLeftX * Constants.MAX_SPEED;
        } else {
            strafe = 0;
        }

        if (Math.abs(driverRightX) >= 0.1) {
            rotation = -(Math.abs(driverRightX) * driverRightX) * Constants.MAX_ROTATION_SPEED;
        } else {
            rotation = 0;
        }

        if (driverAButton) {
            swerveBase.zeroGyro();
            rotationX = 0;
            rotationY = -1;
        }

        if (Math.abs(operatorLeftY) >= 0.1) {
            hopper.setSpeed(operatorLeftY);
        } else {
            hopper.setSpeed(0);
        }

        if (isFieldOriented) {
            swerveBase.drive(new Translation2d(forward, strafe), rotation, true);
        } else {
            swerveBase.drive(new Translation2d(forward, strafe), rotation, false);
        }
    }

    public void operatorControl() {
        if (operatorRightTrigger >= 0.5) {
            if (shootingSolution != null && shootingSolution.shotPossibilty()) {
                shooter.shoot();
            }
        }
        
        if (operatorLeftTrigger > 0.05) {
            shooter.manualSpeed(operatorLeftTrigger);
        }
        
        if (operatorAButton) {
            shooter.stop();
        }

        shootingSolution = shooter.calculateShootingSolution(swerveBase.getPose());
        
        if (shootingSolution != null) {
            shooter.setTargetRPM(shootingSolution.flywheelRPM());
            
            if (operatorRightBumper) {
                if (shootingSolution.shotPossibilty()) {
                    if (Math.abs(shootingSolution.shootingAngle().minus(swerveBase.getHeading()).getDegrees()) <3) {
                        shooter.shoot();
                    } else {
                        shooter.prepareToShoot();
                    }
                    swerveBase.driveFieldOriented(swerveBase.getTargetSpeeds(0, 0, shootingSolution.shootingAngle()));
                }
            }
        }
    }
}