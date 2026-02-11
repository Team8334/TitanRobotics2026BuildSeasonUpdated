package frc.robot.Subsystems;

import java.security.PublicKey;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Interfaces.Subsystem;
import frc.robot.Teleop;
import frc.robot.Devices.NeoSparkMaxMotor;

public class Shooter implements Subsystem {
    private static Shooter instance = null;

    String state = "stop";

    private NeoSparkMaxMotor shooterMotor;
    public double shooterMotorSpeed;

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
