package mqtt.Data;
public class MotorData
{
	//initialised vairables
	int motor_id;
	String room_name;
	
	public int getMotor_id() {
		return motor_id;
	}
	
	//constructor
	public MotorData(int motor_id, String room_name) {
		super();
		this.motor_id = motor_id;
		this.room_name = room_name;
	}

	//getters and setters
	public void setMotor_id(int motor_id) {
		this.motor_id = motor_id;
	}
	public String getRoom_name() {
		return room_name;
	}
	public void setRoom_name(String room_name) {
		this.room_name = room_name;
	}
	
	//to string
	@Override
	public String toString() {
		return "MotorData [motor_id=" + motor_id + ", room_name=" + room_name + "]";
	}
	
}