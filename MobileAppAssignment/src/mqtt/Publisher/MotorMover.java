package mqtt.Publisher;

import com.phidget22.*;

public class MotorMover
{
   static RCServo servo = null;
   
   public static RCServo getInstance() 
   {
	  //Singleton implementation to allow multiple callbacks to the code
	  System.out.println("In singleton constructor");
      if(servo == null) 
      {
         servo = MotorMover();
      }
      return servo;
   }
	
   private static RCServo MotorMover() 
   {   
	   try 
	   {
		   System.out.println("Constructing Motor Mover");
		   servo = new RCServo();
		   servo.open(2000);
	   } 
	   catch (PhidgetException PE) 
	   {
		   PE.printStackTrace();
	   }
       moveServoTo(0);
       System.out.println("Motor initially positioned at: 0");
       return servo;
   }               

   public static void moveServoTo(double motorPosition) 
   {
       try 
       {
    	   MotorMover.getInstance();//gets the access to motor
    	   System.out.println("Moving the motor to " + motorPosition + "\n");
    	   servo.setMaxPosition(210.0);
    	   servo.setTargetPosition(motorPosition);
    	   servo.setEngaged(true);
       } 
       catch (PhidgetException PE) 
       {
    	   PE.printStackTrace();
       }
   }
}