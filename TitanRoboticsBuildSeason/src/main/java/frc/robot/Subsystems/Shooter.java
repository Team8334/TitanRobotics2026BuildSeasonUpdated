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

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Interfaces.Subsystem;
import frc.robot.Teleop;
import frc.robot.Data.Constants;
import frc.robot.Devices.NeoSparkMaxMotor;

public class Shooter implements Subsystem {
    private static Shooter instance = null;

    String state = "stop";

    private NeoSparkMaxMotor shooterMotor;
    public double shooterMotorSpeed;
    public double shootingOutput;

    public static Shooter getInstance() {
        if (instance == null) {
            instance = new Shooter();
        }
        return instance;
    }

    public Shooter() {
        // motors
        // other devices
    }

    public Translation3d goalLocation() {
        Alliance alliance = DriverStation.getAlliance().get();

        if (alliance == Alliance.Red){
            return Constants.RED_HUB_LOCATION;
        }

        if (alliance == Alliance.Blue){
            return Constants.BLUE_HUB_LOCATION;
        }

        return null;
    }

    public double calculateShootingSolution(Pose2d robotPose) {
        Translation2d goalLoc = goalLocation().toTranslation2d();
        Translation2d robotTranslation = robotPose.getTranslation();

        Translation2d shooterLoc = robotTranslation.plus(new Translation2d(Constants.SHOOTER_OFFSET, 0).rotateBy(robotPose.getRotation()));

        return shootingOutput;
    }

    public double getSpeed() {
        return 0;
        // to do
    }

    public void stop() {
        state = "stop";
    }

    public void shoot() {
        state = "shoot";
    }

    public void ShooterStateProcessing() {
        switch (state) {
            case "shoot":
                shooterMotorSpeed = 0.5;
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

    // use if statements to set states to which buttons are pressed

}
