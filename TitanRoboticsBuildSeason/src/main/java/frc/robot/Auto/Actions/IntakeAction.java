package frc.robot.Auto.Actions;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Interfaces.Actions;
import frc.robot.Subsystems.intake.IntakeMechanism;

public class IntakeAction implements Actions {
    private double seconds;
    Timer timer;
    IntakeMechanism intakeMechanism;
    public String state;

    /* Class: Intake Action
     * Description: This sets the state of the Intake to either "Standby", "Intaking", "Reverse",
     *              or "Disabled"
     * Author: Mai
     */
    public IntakeAction(double seconds, String state) {
        this.seconds = seconds;
        this.state = state;
        intakeMechanism = IntakeMechanism.getInstance();
    }

    @Override
    public void start() {
        timer = new Timer();
        timer.start();
    }

    @Override
    public void update() {
        intakeMechanism.setState(state);
    }

    @Override
    public boolean isFinished() {
        return timer.get() >= seconds;
    }

    @Override
    public void done() {
        timer.stop();
        intakeMechanism.setState("Down");
    }

}