package frc.robot.Subsystems.intake;


import frc.robot.Devices.NeoSparkMaxMotor;
import frc.robot.Data.Constants;
import frc.robot.utils.Logger;
import edu.wpi.first.wpilibj.XboxController;


public class IntakeMechanism {

    private XboxController xboxController;
    private ArmMotor armMotor;
    private WheelsMotor wheelsMotor;


    public IntakeMechanism() {
        this(Constants.INTAKE_ARM_MOTOR_ID, Constants.INTAKE_WHEELS_MOTOR_ID);
    }


    public IntakeMechanism(int armCANID, int wheelsCANID) {
        this.xboxController = new XboxController(0);
        this.armMotor = new ArmMotor(armCANID);
        this.wheelsMotor = new WheelsMotor(wheelsCANID);
    }

    public void GetwheelsMotorSpeed() {
        
        
       
        if (this.xboxController.getAButtonPressed() == true) {
            // button pressed
            this.wheelsMotor.setSpeed(54354);
        } else {
            // button not pressed
            this.wheelsMotor.setSpeed(0);
        }

    }

}
