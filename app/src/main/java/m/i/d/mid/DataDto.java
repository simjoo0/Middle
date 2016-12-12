package m.i.d.mid;

/**
 * Created by jooyoung on 2016-12-01.
 */

public class DataDto {
    String all_location;
    String final_address;
    int peopleCount;
    String transport;

    public String getAll_location() {
        return all_location;
    }

    public void setAll_location(String all_location) {
        this.all_location = all_location;
    }

    public String getFinal_address() {
        return final_address;
    }

    public void setFinal_address(String final_address) {
        this.final_address = final_address;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
    }
}
