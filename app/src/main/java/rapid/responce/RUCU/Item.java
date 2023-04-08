package rapid.responce.RUCU;

public class Item {
    private String garageName;
    private String garageDescription;

    public Item() {
        // Default constructor required for calls to DataSnapshot.getValue(Item.class)
    }

    public Item(String garageName, String garageDescription) {
        this.garageName = garageName;
        this.garageDescription = garageDescription;

    }

    public String getName() {
        return garageName;
    }

    public String getGarageDescription() {
        return garageDescription;
    }


    public void setName(String garageName) {
        this.garageName = garageName;
    }

    public void setGarageDescription(String garageDescription) {
        this.garageDescription = garageDescription;
    }

}