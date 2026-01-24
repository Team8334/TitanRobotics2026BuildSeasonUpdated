package frc.robot.Devices;
import com.revrobotics.spark.*;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import java.util.Set;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class NeoSparkMaxMotor {


    private SparkMax m_motor;
    private boolean isInverted;
    private int CANID;
    private SparkClosedLoopController closedLoopController;
    private RelativeEncoder encoder;
    private SparkMaxConfig motorConfig;
    private ClosedLoopSlot closedLoopSlot;


    public NeoSparkMaxMotor(int CANID){

        this.CANID = CANID;
        try {
            m_motor = new SparkMax(CANID,SparkLowLevel.MotorType.kBrushless);
        }
        catch(Exception e) {
            m_motor = null;
            System.out.println("SparkMax not found: " + CANID);
        }

        encoder = m_motor.getEncoder();
        motorConfig = new SparkMaxConfig();
        closedLoopController = m_motor.getClosedLoopController();

        motorConfig.encoder
            .positionConversionFactor((2 * Math.PI*.1) / 60.0 / 10.71)
            .velocityConversionFactor((2 * Math.PI*.1) / 60.0 / 10.71);

        motorConfig.closedLoop
            .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
            // Set PID values for position control. We don't need to pass a closed loop
            // slot, as it will default to slot 0.
            .p(0.035)
            .i(0)
            .d(0)
            .velocityFF(1.0 / 5676, closedLoopSlot)
            .outputRange(-1, 1);  

        c

        m_motor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
    }

    public void setVoltage(double voltage){
        m_motor.setVoltage(voltage);
    }

    public void setSpeed(double speed){

        if(isInverted){
            speed*=-1;
        }

        else{
            setSpeed(speed);
        }
    }
}