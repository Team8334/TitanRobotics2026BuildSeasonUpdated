package frc.robot.Subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
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

    public record ShootingSolution(Rotation2d shootingAngle, double flywheelRpmLeft, double flywheelRpmRight, boolean shotPossibility) {
    };

    private final SimpleMotorFeedforward flyWheelFeedFowardLeft;
    private final SimpleMotorFeedforward flyWheelFeedFowardRight;
    private final PIDController flyWheelPIDLeft;
    private final PIDController flyWheelPIDRight;

    public double targetRpmLeft;
    public double targetRpmRight;
    
    // Interpolation tables mapping distance (meters) to RPM
    private final InterpolatingDoubleTreeMap leftRpmTable = new InterpolatingDoubleTreeMap();
    private final InterpolatingDoubleTreeMap rightRpmTable = new InterpolatingDoubleTreeMap();

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

        // Populate the interpolation tables based on measured tuning:
        // 2.15 meters -> 3000 RPM
        // 2.62 meters -> 3500 RPM
        leftRpmTable.put(1.92, 2700.0);
        //leftRpmTable.put(2.15, 3000.0);
        leftRpmTable.put(2.47, 2900.0);
        leftRpmTable.put(3.05, 3500.0);
        leftRpmTable.put(3.48, 3550.0);

        leftRpmTable.put(4.18, 3750.0);

        rightRpmTable.put(1.92, 2750.0); 
        rightRpmTable.put(2.47, 2950.0);
        rightRpmTable.put(3.05, 3550.0);
        rightRpmTable.put(3.48, 3600.0);

        rightRpmTable.put(4.18, 3800.0);

        initialize();
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
        double possibilityDeterminator = normalDistanceToHub * Math.tan(Constants.FIRING_ANGLE) - Constants.HEIGHT_DIFFERENCE;

        // Simplified angle calculation using WPILib's built in getAngle()
        Rotation2d shootingAngle = distanceToHub.getAngle();

        // Get RPM from interpolation tables based on distance
        double rpmLeft = leftRpmTable.get(normalDistanceToHub);
        double rpmRight = rightRpmTable.get(normalDistanceToHub);

        if (possibilityDeterminator <= 0) {
            // shot is impossible due to height difference vs trajectory angle
            return new ShootingSolution(new Rotation2d(), 0, 0, false);
        } else {
            return new ShootingSolution(shootingAngle, rpmLeft, rpmRight, true);
        }
    }

    public void setTargetRPM(double targetRpmLeft, double targetRpmRight) {
        // Reset PID integral accumulated error if we transition from 0 to something else
        if (Math.abs(this.targetRpmLeft) == 0 && Math.abs(targetRpmLeft) > 0) {
            flyWheelPIDLeft.reset();
        }
        if (Math.abs(this.targetRpmRight) == 0 && Math.abs(targetRpmRight) > 0) {
            flyWheelPIDRight.reset(); 
        }
        this.targetRpmLeft = targetRpmLeft;
        this.targetRpmRight = targetRpmRight;
    }

    public void setFlyWheelVelocity() {
        // Changed to allow for negative RPM (reversing)
        if (Math.abs(targetRpmLeft) > 0 || Math.abs(targetRpmRight) > 0) {
            leftShooterVoltageCalc = flyWheelFeedFowardLeft.calculate(targetRpmLeft)
                    + flyWheelPIDLeft.calculate(shooterMotorLeft.getSpeed(), targetRpmLeft);
            shooterMotorLeft.setVoltage(leftShooterVoltageCalc);
            
            shooterMotorRight.setVoltage(flyWheelFeedFowardRight.calculate(targetRpmRight)
                    + flyWheelPIDRight.calculate(shooterMotorRight.getSpeed(), targetRpmRight));
        } else {
            shooterMotorLeft.setVoltage(0);
            shooterMotorRight.setVoltage(0);
        }
    }

    public boolean isAtCorrectSpeed() {
        double leftError = Math.abs(shooterMotorLeft.getSpeed() - targetRpmLeft);
        double rightError = Math.abs(shooterMotorRight.getSpeed() - targetRpmRight);
        
        // numbers should be in rpm
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

    public void manualFire(double triggerValue){
        state = "manualFire";
        double manualTarget = triggerValue * 5676.0; // Math based on how far trigger is pressed down
        setTargetRPM(manualTarget, manualTarget);
        //*4000 hit the ceiling
        //115 inches (back of bot without bumpers to our hub wall) at *3500
        //96.5 inches (back of bot without bumpers to our hub wall) at *3000
        //1800 drops the fuel just in front of the robot in case you need to hopper dump 
        //(115 inches plus 11.25) minus the distance from from center of the hub to the edge of the hub (23)
        //2.62255 meters from center of the hub to the center of the robot at *3500
        //2.15265 meters from the center of the hub to the center of the robot at *3000
    }

    public void manualPrep(){
        state = "manualPrep";
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
                    kickerMotor.setVoltage((Constants.KICKERMOTOR));
                } else if (targetRpmLeft < 0 || targetRpmRight < 0) {
                    kickerMotor.setVoltage(Constants.KICKERMOTOR); 
                } else {
                    kickerMotor.setVoltage(0);
                }
                break;

            case "manualFire":
                setFlyWheelVelocity();
                if (isAtCorrectSpeed()) {
                    kickerMotor.setVoltage((Constants.KICKERMOTOR));
                } else {
                    kickerMotor.setVoltage(0);
                }
                break;

            case "shoot":
                setFlyWheelVelocity();
                // Only activate kicker if we are at the correct speed
                if (isAtCorrectSpeed()) {
                    kickerMotor.setVoltage((Constants.KICKERMOTOR));
                } else {
                    kickerMotor.setVoltage(0); 
                }
                break;

            case "stop":
                shooterMotorLeft.setVoltage(0);
                shooterMotorRight.setVoltage(0);
                kickerMotor.setVoltage(0);
                targetRpmLeft = 0;
                targetRpmRight = 0;
                break;
        }
    }

    public void update() {
        ShooterStateProcessing();
    }

    public void initialize() {
        SmartDashboard.setDefaultNumber("Shooter/Manual RPM Setpoint", 3000.0);
    }

    public void log() {
        SmartDashboard.putNumber("Shooter/Shooter Left Motor Speed", shooterMotorLeft.getSpeed());
        SmartDashboard.putNumber("Shooter/Shooter Right Motor Speed", shooterMotorRight.getSpeed());
        SmartDashboard.putNumber("Shooter/Shooter Target RPM Left", targetRpmLeft);
        SmartDashboard.putNumber("Shooter/Shooter Target RPM Right", targetRpmRight);
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