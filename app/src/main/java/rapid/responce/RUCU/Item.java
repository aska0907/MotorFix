package rapid.responce.RUCU;

public class Item {
    private String id;
    private String garageName;
    private String region;
    private String image;
    private String garageCompany;
    private String contact;
    private String district;
    private String description;


    public Item(String id, String garageName, String region, String description, String image, String garageCompany, String contact, String district) {
        this.id = id;
        this.garageName = garageName;
        this.region = region;
        this.image = image;
        this.garageCompany = garageCompany;
        this.contact = contact;
        this.district = district;
        this.description=description;
    }

    public String getId() {
        return id;
    }
    public String getDescription(){return description;}

    public String getGarageName() {
        return garageName;
    }

    public String getRegion() {
        return region;
    }
    public String getDistrict() {
        return district;
    }
    public String getImage() {
        return image;
    }

    public String getGarageCompany() {
        return garageCompany;
    }

    public String getContact() {
        return contact;
    }




    public void seGarageName(String garageName) {
        this.garageName = garageName;
    }

    public void setRegion(String garageDescription) {
        this.region = garageDescription;
    }
    public void setDescription(String description) {this.description=description;}
    public void setImage(String image) {
        this.image = image;
    }

    public void setGarageCompany(String garageCompany) {
        this.garageCompany = garageCompany;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
