package rapid.responce.RUCU;

public class RequestItem {

        private String garageName;
        private String region;
        private String image;
        private String garageCompany;
        private String contact;
        private String description;

        public RequestItem() {
            // Default constructor required for calls to DataSnapshot.getValue(Item.class)
        }

        public RequestItem(String garageName, String region, String image, String garageCompany, String contact, String description) {
            this.garageName = garageName;
            this.region = region;
            this.image = image;
            this.garageCompany = garageCompany;
            this.contact = contact;
            this.description = description;
        }

        public String getGarageName() {
            return garageName;
        }

        public String getRegion() {
            return region;
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

        public String getDescription() {
            return description;
        }

        public void seGarageName(String garageName) {
            this.garageName = garageName;
        }

        public void setRegion(String garageDescription) {
            this.region = garageDescription;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setGarageCompany(String garageCompany) {
            this.garageCompany = garageCompany;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

