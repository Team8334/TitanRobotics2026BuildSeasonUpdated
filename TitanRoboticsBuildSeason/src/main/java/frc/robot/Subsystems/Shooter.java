package frc.robot.Subsystems;

import java.security.PublicKey;

import javax.swing.GroupLayout.Alignment;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel;
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
import yams.mechanisms.config.FlyWheelConfig;
import frc.robot.Teleop;
import frc.robot.Data.PortMap;
import frc.robot.Data.Constants;
import frc.robot.Devices.NeoSparkMaxMotor;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.controller.PIDController;

public class Shooter implements Subsystem {
    private static Shooter instance = null;

    String state = "stop";

    private NeoSparkMaxMotor shooterMotorRight;
    private NeoSparkMaxMotor shooterMotorLeft;
    private NeoSparkMaxMotor kickerMotor;
    public double shooterMotorSpeed;

    public record ShootingSolution(Rotation2d shootingAngle, double flywheelRPM, boolean shotPossibilty) {
    };
    //public double AutoShootingSolution;
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
        shooterMotorLeft = new NeoSparkMaxMotor(PortMap.shooterMotorLeft);
        shooterMotorRight = new NeoSparkMaxMotor(PortMap.shooterMotorRight);
        kickerMotor = new NeoSparkMaxMotor(PortMap.kickerMotor);
        flyWheelFeedFowardLeft = new SimpleMotorFeedforward(Constants.kFLYWHEELs, Constants.kFLYWHEELv,
                Constants.kFLYWHEELa);
        flyWheelFeedFowardRight = new SimpleMotorFeedforward(Constants.kFLYWHEELs, Constants.kFLYWHEELv,
                Constants.kFLYWHEELa);

        flyWheelPIDLeft = new PIDController(Constants.kFLYWHEELp, 0, 0);
        flyWheelPIDRight = new PIDController(Constants.kFLYWHEELp, 0, 0);
    }

    public Translation3d goalLocation() {
        Alliance alliance = DriverStation.getAlliance().get();

        if (alliance == Alliance.Red) {
            return Constants.RED_HUB_LOCATION;
        }

        if (alliance == Alliance.Blue) {
            return Constants.BLUE_HUB_LOCATION;
        }

        return null;
    }

    public ShootingSolution calculateShootingSolution(Pose2d robotPose) {
        Translation2d goalLoc = goalLocation().toTranslation2d();
        Translation2d robotTranslation = robotPose.getTranslation();

        Translation2d shooterLoc = robotTranslation
                .plus(new Translation2d(Constants.SHOOTER_OFFSET, 0).rotateBy(robotPose.getRotation()));
        Translation2d distanceToHub = goalLoc.minus(shooterLoc);
        double normalDistanceToHub = distanceToHub.getNorm();

        double numerator = Constants.GRAVITY * (normalDistanceToHub * normalDistanceToHub);
        double denominator = 2 * (Math.cos(Constants.FIRING_ANGLE) * Math.cos(Constants.FIRING_ANGLE));
        double possiblityDeterminator = normalDistanceToHub * Math.tan(Constants.FIRING_ANGLE)
                - Constants.HEIGHT_DIFFERENCE;

        double shootingOutputVelocity = Math.sqrt(numerator / denominator * possiblityDeterminator);

        Rotation2d shootingAngle = distanceToHub.div(normalDistanceToHub).getAngle();

        double flywheelRPM = (shootingOutputVelocity / Constants.FLYWHEEL_CIRCUMFENCE) * 60;

        if (possiblityDeterminator <= 0) {
            // shot is impossible
            return new ShootingSolution(new Rotation2d(), 0, false);
        } else {
            return new ShootingSolution(shootingAngle, flywheelRPM, true);
        }

        // RPM correction table:
        // 2.5 meters:
        // 5 meters:
        // 7.5 meters:
        // 10 meters:
        // 12.5 meters:
        // 15 meters:
    }

    public void setTargetRPM(double targetRPM){
        this.targetRPM = targetRPM;
    }

    public void setFlyWheelVelocity() {

        if (targetRPM > 0) {
            shooterMotorLeft.setVoltage(flyWheelFeedFowardLeft.calculate(targetRPM)
                    + flyWheelPIDLeft.calculate(shooterMotorLeft.getSpeed(), targetRPM));
            shooterMotorRight.setVoltage(flyWheelFeedFowardRight.calculate(targetRPM)
                    + flyWheelPIDRight.calculate(shooterMotorRight.getSpeed(), targetRPM));
        } else {
            shooterMotorLeft.setVoltage(0);
            shooterMotorRight.setVoltage(0);
        }
    }

    public boolean isAtCorrectSpeed() {

        if (Math.abs(shooterMotorLeft.getSpeed()) - targetRPM < 60
                && Math.abs(shooterMotorRight.getSpeed()) - targetRPM < 60) {
            return true;
        } else {
            return false;
        }

    }

    public double getSpeed() {
        return 0;
        // to do
    }

    public void manualSpeed(double operatorJoystick) {
        shooterMotorLeft.setVoltage(Constants.MINIMUMMOTORSPEEDTOSHOOT + operatorJoystick);
        shooterMotorRight.setVoltage(Constants.MINIMUMMOTORSPEEDTOSHOOT + operatorJoystick);
        if (isAtCorrectSpeed()) {
            kickerMotor.setVoltage(Constants.KICKERMOTOR);
        }
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
                break;

            case "shoot":
                if (isAtCorrectSpeed()) {
                    kickerMotor.setVoltage(Constants.KICKERMOTOR);
                }
                setFlyWheelVelocity();

                // activate kicker wheels

                break;

            case "stop":
                shooterMotorSpeed = 0;
                break;

        }
    }

    public void update() {
        ShooterStateProcessing();
    }

    public void initialize() {
    }

    public void log() {

    }

    public boolean isEnabled() {
        return true;
    }

    public String getName() {
        return "Shooter";
    }
}
