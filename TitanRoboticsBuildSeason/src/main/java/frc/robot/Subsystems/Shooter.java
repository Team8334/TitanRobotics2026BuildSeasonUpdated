package frc.robot.Subsystems;

import frc.robot.Interfaces.Subsystem;

public class Shooter implements Subsystem {
    private static Shooter instance = null;

    public static Shooter getInstance() {
        if (instance == null) {
            instance = new Shooter();
        }
        return instance;
    }

    public Shooter(){
        //motors 
        //other devices

    }
    public double getSpeed(){
    return 0;
    //to do
     }
    
     public void stop(){

    }
    public void shoot(){

    }
    String state="stop";


    public void update(){
        //run in loop    
        switch (state){
            case "shooting":
            break; 
            
        }
    }

    public void initialize(){
    }

    public void log(){

    }

    public boolean isEnabled(){
        return true;
    }

    public String getName(){
        return "Shooter";
    }
}
