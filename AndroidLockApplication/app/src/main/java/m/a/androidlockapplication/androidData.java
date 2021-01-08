package m.a.androidlockapplication;

import java.io.Serializable;

public class androidData implements Serializable {
    String android_id;
    String room_name;
    String tag_id;
    int unique_key;

    public androidData(String android_id, String room_name, String tag_id, int unique_key) {
        this.android_id = android_id;
        this.room_name = room_name;
        this.tag_id = tag_id;
        this.unique_key = unique_key;
    }

    public String getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public int getUnique_key() {
        return unique_key;
    }

    public void setUnique_key(int unique_key) {
        this.unique_key = unique_key;
    }

    @Override
    public String toString() {
        return "androidData{" + "android_id='" + android_id + '\'' + ", room_name='" + room_name + '\'' + ", tag_id='" + tag_id + '\'' + ", unique_key=" + unique_key + '}';
    }
}
