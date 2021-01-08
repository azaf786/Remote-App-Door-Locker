package mqtt.Data;

public class RfidData {
	
	//variables
	String tag_id;
	String room_name;
	int reader_id;
	boolean valid;
	
	//constructor
	public RfidData(String tag_id, String room_name, int reader_id, boolean valid) {
		super();
		this.tag_id = tag_id;
		this.room_name = room_name;
		this.reader_id = reader_id;
		this.valid = valid;
	}
	
	//getters and setters
	public String getTag_id() {
		return tag_id;
	}
	public void setTag_id(String tag_id) {
		this.tag_id = tag_id;
	}
	public String getRoom_name() {
		return room_name;
	}
	public void setRoom_name(String room_name) {
		this.room_name = room_name;
	}
	public int getReader_id() {
		return reader_id;
	}
	public void setReader_id(int reader_id) {
		this.reader_id = reader_id;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	@Override
	public String toString() {
		return "RfidData [tag_id=" + tag_id + ", room_name=" + room_name + ", reader_id=" + reader_id + ", valid="
				+ valid + "]";
	}

}
