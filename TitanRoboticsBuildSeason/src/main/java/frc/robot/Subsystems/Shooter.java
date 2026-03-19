package frc.robot.Subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Interfaces.Subsystem;
import frc.robot.Data.PortMap;
import frc.robot.Data.Constants;
import frc.robot.Devices.NeoSparkMaxMotor;

/*
 * Class: Shooter
 * Description: We use 2d positioning to calculate our shot possibility and how fast the motors
 *              need to turn to launch the fuel the appropriate amount.
 * Author: Sarah, Trevor
 */

public class Shooter implements Subsystem {
    private boolean wasAtSpeed = false;
    private static Shooter instance = null;

    String state = "stop";

    public NeoSparkMaxMotor shooterMotorRight;
    public NeoSparkMaxMotor shooterMotorLeft;
    public NeoSparkMaxMotor kickerMotor;
    public double shooterMotorSpeed;
    public double normalDistanceToHub;
    public double leftShooterVoltageCalc;

    public record ShootingSolution(Rotation2d shootingAngle, double flywheelRPM, boolean shotPossibility) {
    };

    private final SimpleMotorFeedforward flyWheelFeedFowardLeft;
    private final SimpleMotorFeedforward flyWheelFeedFowardRight;
    private final PIDController flyWheelPIDLeft;
    private final PIDController flyWheelPIDRight;

    public double targetRPM;

    public static Shooter getInstance() {
        if (instance == null) {
            instance = new Shooter();
        }
        return instance;
    }

    public Shooter() {
        SubsystemManager.registerSubsystem(this);
        shooterMotorLeft = new NeoSparkMaxMotor(PortMap.shooterMotorLeft);
        shooterMotorRight = new NeoSparkMaxMotor(PortMap.shooterMotorRight);
        kickerMotor = new NeoSparkMaxMotor(PortMap.kickerMotor);
        
        flyWheelFeedFowardLeft = new SimpleMotorFeedforward(Constants.kFLYWHEELs, Constants.kFLYWHEELv, Constants.kFLYWHEELa);
        flyWheelFeedFowardRight = new SimpleMotorFeedforward(Constants.kFLYWHEELs, Constants.kFLYWHEELv, Constants.kFLYWHEELa);

        flyWheelPIDLeft = new PIDController(Constants.kFLYWHEELp, Constants.kFLYWHEELi, Constants.kFLYWHEELd);
        flyWheelPIDRight = new PIDController(Constants.kFLYWHEELp, Constants.kFLYWHEELi, Constants.kFLYWHEELd);
        
        // Prevent integral windup from contributing too much voltage
        flyWheelPIDLeft.setIntegratorRange(-1.5, 1.5);
        flyWheelPIDRight.setIntegratorRange(-1.5, 1.5);
        
        shooterMotorRight.setInverted(true);
        shooterMotorRight.setBrakeMode(false);
        shooterMotorLeft.setBrakeMode(false);
        
        SparkMaxConfig sparkMaxConfig = new SparkMaxConfig();
        sparkMaxConfig.encoder.quadratureMeasurementPeriod(10).quadratureAverageDepth(2);
        //shooterMotorLeft.configure(sparkMaxConfig);
    }

    public Translation3d goalLocation() {
        Alliance alliance = DriverStation.getAlliance().orElse(Alliance.Blue);

        if (alliance == Alliance.Red) {
            return Constants.RED_HUB_LOCATION;
        }

        return Constants.BLUE_HUB_LOCATION;
    }

    public ShootingSolution calculateShootingSolution(Pose2d robotPose) {
        Translation2d goalLoc = goalLocation().toTranslation2d();
        Translation2d robotTranslation = robotPose.getTranslation();

        Translation2d shooterLoc = robotTranslation
                .plus(new Translation2d(Constants.SHOOTER_OFFSET, 0).rotateBy(robotPose.getRotation()));
        Translation2d distanceToHub = goalLoc.minus(shooterLoc);
        normalDistanceToHub = distanceToHub.getNorm();

        // Note: Make sure Constants.FIRING_ANGLE is in Radians, not Degrees!
        double numerator = Constants.GRAVITY * (normalDistanceToHub * normalDistanceToHub);
        double denominator = 2 * (Math.cos(Constants.FIRING_ANGLE) * Math.cos(Constants.FIRING_ANGLE));
        double possibilityDeterminator = normalDistanceToHub * Math.tan(Constants.FIRING_ANGLE) - Constants.HEIGHT_DIFFERENCE;

        double shootingOutputVelocity = Math.sqrt(numerator / (denominator * possibilityDeterminator));

        // Simplified angle calculation using WPILib's built in getAngle()
        Rotation2d shootingAngle = distanceToHub.getAngle();

        double flywheelRPM = (shootingOutputVelocity / Constants.FLYWHEEL_CIRCUMFENCE) * 60;

        if (possibilityDeterminator <= 0) {
            // shot is impossible
            return new ShootingSolution(new Rotation2d(), 0, false);
        } else {
            return new ShootingSolution(shootingAngle, flywheelRPM, true);
        }
    }

    public void setTargetRPM(double targetRPM) {
        // Reset PID integral accumulated error if we transition from 0 to something else
        if (Math.abs(this.targetRPM) == 0 && Math.abs(targetRPM) > 0) {
            flyWheelPIDLeft.reset();
            flyWheelPIDRight.reset();
        }
        this.targetRPM = targetRPM;
    }

    public void setFlyWheelVelocity() {
        // Changed to allow for negative RPM (reversing)
        if (Math.abs(targetRPM) > 0) {
            leftShooterVoltageCalc = flyWheelFeedFowardLeft.calculate(targetRPM)
                    + flyWheelPIDLeft.calculate(shooterMotorLeft.getSpeed(), targetRPM);
            shooterMotorLeft.setVoltage(leftShooterVoltageCalc);
            
            shooterMotorRight.setVoltage(flyWheelFeedFowardRight.calculate(targetRPM)
                    + flyWheelPIDRight.calculate(shooterMotorRight.getSpeed(), targetRPM));
        } else {
            shooterMotorLeft.setVoltage(0);
            shooterMotorRight.setVoltage(0);
        }
    }

    public boolean isAtCorrectSpeed() {
        double leftError = Math.abs(shooterMotorLeft.getSpeed() - targetRPM);
        double rightError = Math.abs(shooterMotorRight.getSpeed() - targetRPM);
        
        if (!wasAtSpeed && leftError < 150 && rightError < 150) {
            wasAtSpeed = true;
        } else if (wasAtSpeed && (leftError > 750 || rightError > 750)) {
            wasAtSpeed = false;
        }
        return wasAtSpeed;
    }

    public double getSpeed() {
        return shooterMotorLeft.getSpeed(); 
    }

    public void manualSpeed(double operatorJoystick) {
        state = "manual";
        targetRPM = (operatorJoystick * 3000);
    }

    public void manualFire(double operatorJoystick){
        state = "manualFire";
        targetRPM = (operatorJoystick * 4000); 
        //*4000 hit the ceiling
        //115 inches (back of bot without bumpers to our hub wall) at *3500
        //96.5 inches (back of bot without bumpers to our hub wall) at *3000
        //1800 drops the fuel just in front of the robot in case you need to hopper dump
    }

    public void stop() {
        state = "stop";
    }

    public void shoot() {
        state = "shoot";
    }

    public void prepareToShoot() {
        state = "preparing";
    }

    public void ShooterStateProcessing() {
        switch (state) {
            case "preparing":
                setFlyWheelVelocity();
                kickerMotor.setVoltage(0); // Ensure kicker is off while preparing
                break;

            case "manualPrep":
                setFlyWheelVelocity();
                // In manual mode, we just spin up the wheels.
                if (isAtCorrectSpeed()) {
                    kickerMotor.setVoltage(-(Constants.KICKERMOTOR));
                } else if (targetRPM < 0) {
                    kickerMotor.setVoltage(Constants.KICKERMOTOR); 
                } else {
                    kickerMotor.setVoltage(0);
                }
                break;

            case "manualFire":
                setFlyWheelVelocity();
                kickerMotor.setVoltage(-(Constants.KICKERMOTOR));
                break;

            case "shoot":
                setFlyWheelVelocity();
                // Only activate kicker if we are at the correct speed
                if (isAtCorrectSpeed()) {
                    kickerMotor.setVoltage(-(Constants.KICKERMOTOR));
                } else {
                    kickerMotor.setVoltage(0); 
                }
                break;

            case "stop":
                shooterMotorLeft.setVoltage(0);
                shooterMotorRight.setVoltage(0);
                kickerMotor.setVoltage(0);
                targetRPM = 0;
                break;
        }
    }

    public void update() {
        ShooterStateProcessing();
    }

    public void initialize() {
    }

    public void log() {
        SmartDashboard.putNumber("Shooter/Shooter Left Motor Speed", shooterMotorLeft.getSpeed());
        SmartDashboard.putNumber("Shooter/Shooter Right Motor Speed", shooterMotorRight.getSpeed());
        SmartDashboard.putNumber("Shooter/Shooter Target RPM", targetRPM);
        SmartDashboard.putNumber("Shooter/Kicker Motor Speed", kickerMotor.getSpeed());
        SmartDashboard.putString("Shooter/Shooter State", state);
        SmartDashboard.putBoolean("Shooter/Shooter At Target Speed", isAtCorrectSpeed());
        SmartDashboard.putNumber("Shooter/distance to Shooter", normalDistanceToHub);
        SmartDashboard.putNumber("Shooter/Left Motor voltage calc", leftShooterVoltageCalc);
    }

    public boolean isEnabled() {
        return true;
    }

    public String getName() {
        return "Shooter";
    }
}