package frc.robot.Devices;
import com.revrobotics.spark.*;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import java.util.Set;

import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*
 * Class: NEOSparkMaxMotor
 * Description: Holds the object for the motors that turn.
 * Author: Mai
 */

public class NeoSparkMaxMotor {

    private SparkMax m_motor;
    private SparkClosedLoopController closedLoopController;
    private RelativeEncoder encoder;
    private SparkAbsoluteEncoder absoluteEncoder;

    public NeoSparkMaxMotor(int CANID) {
        System.out.println("NeoSparkMaxMotor. Initializing... CANID = " + CANID);
        m_motor = new SparkMax(CANID, SparkLowLevel.MotorType.kBrushless);
        encoder = m_motor.getEncoder();
        closedLoopController = m_motor.getClosedLoopController();

        SparkMaxConfig motorConfig = new SparkMaxConfig();
        
        // Example configuration (optional, adjust as needed)
        // double conversionFactor = (2 * Math.PI * 0.1) / 60.0 / 10.71;
        // motorConfig.encoder
        //      .positionConversionFactor(conversionFactor)
        //      .velocityConversionFactor(conversionFactor);

        m_motor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        System.out.println("NeoSparkMaxMotor. Initializing completed for CANID " + CANID);
    }

    public NeoSparkMaxMotor(int CANID, boolean hasThroughbore) {
        System.out.println("NeoSparkMaxMotor. Initializing absolute... CANID = " + CANID);
        m_motor = new SparkMax(CANID, SparkLowLevel.MotorType.kBrushless);
        absoluteEncoder = m_motor.getAbsoluteEncoder();
        encoder = m_motor.getEncoder();
        closedLoopController = m_motor.getClosedLoopController();

        SparkMaxConfig motorConfig = new SparkMaxConfig();
        
        // Configure absolute encoder if needed
        motorConfig.absoluteEncoder
            .inverted(false); // Adjust based on your setup

        m_motor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    public void setVoltage(double voltage) {
        m_motor.setVoltage(voltage);
    }

    public void setInverted(boolean inverted) {
        SparkMaxConfig config = new SparkMaxConfig();
        config.inverted(inverted);
        m_motor.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
    }

    public void set(double power) {
        m_motor.set(power);
    }

    public double getAppliedOutput() {
        return m_motor.getAppliedOutput();
    }

    public void setRotationalSpeed(double velocity) {
        closedLoopController.setReference(velocity, ControlType.kVelocity);
    }

    public double getSpeed() {
        return encoder.getVelocity();
    }

    public double getPosition() {
        return encoder.getPosition();
    }
   
    public void setBrakeMode(boolean brake) {
        SparkMaxConfig config = new SparkMaxConfig();
        config.idleMode(brake ? IdleMode.kBrake : IdleMode.kCoast);
        m_motor.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
    }

    public double getAbsolutePosition() {
        if (absoluteEncoder != null) {
            return absoluteEncoder.getPosition();
        }
        return 0;
    }

    public double getBusVoltage(){
        return m_motor.getBusVoltage();
    }
}