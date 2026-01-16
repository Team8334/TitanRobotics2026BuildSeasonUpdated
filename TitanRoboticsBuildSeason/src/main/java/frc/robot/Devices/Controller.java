package frc.robot.Devices;

import java.util.HashMap;
import edu.wpi.first.wpilibj.XboxController;

public class Controller extends XboxController{

    private HashMap<Integer, Boolean> debounceButtons = new HashMap<Integer, Boolean>();

    public Controller(int port) {
        super(port);
    }
    
    public boolean getDebouncedButton(int button) {
        if (!debounceButtons.containsKey(button)) {
            debounceButtons.put(button, false);
        }
        if (this.getRawButton(button) && debounceButtons.get(button))
        {
            debounceButtons.put(button, false);
            return false;
        }
        else if (this.getRawButtonPressed(button))
        {
            debounceButtons.put(button, true);
            return true;
        }
        else
        {
            return debounceButtons.get(button);
        }
    }

    public boolean getDebouncedButton(Button button) {
        if (!debounceButtons.containsKey(button.value)) {
            debounceButtons.put(button.value, false);
        }
        
        if (this.getRawButton(button.value) && debounceButtons.get(button.value))
        {
            debounceButtons.put(button.value, false);
            return false;
        }
        else if (this.getRawButtonPressed(button.value))
        {
            debounceButtons.put(button.value, true);
            return true;
        }
        else
        {
            return debounceButtons.get(button.value);
        }
    }
     
    public boolean isOperational() {
        return this.isConnected();
    }

    public String getName() {
        return "Controller";
    }
}