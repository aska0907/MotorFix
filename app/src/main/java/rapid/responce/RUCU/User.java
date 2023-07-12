package rapid.responce.RUCU;
public class User {
    private String email;
    private String password;
    private String lastName;
    private String region;
    private String phone;
    private String imageUrl;

    public User() {
        // Required empty constructor
    }

    public User(String email, String password, String lastName, String region, String phone, String imageUrl) {
        this.email = email;
        this.password = password;
        this.lastName = lastName;
        this.region = region;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRegion() {
        return region;
    }

    public String getPhone() {
        return phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
