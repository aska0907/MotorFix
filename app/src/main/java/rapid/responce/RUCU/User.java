package rapid.responce.RUCU;
public class User {
    public String email;
    public String password;
    private String firstName;
    private String secondName;
    private String lastName;
    private String region;
    private String district;
    private String address;
    private String phone;
    private String role;

    public User() {
        // Required empty constructor
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;

    }

    public User(String email, String password, String Role, String first_name, String second_name, String last_name, String region, String district, String address, String phone) {
        this.email = email;
        this.password = password;
        this.firstName = first_name;
        this.secondName = second_name;
        this.lastName = last_name;
        this.region = region;
        this.district = district;
        this.address = address;
        this.phone = phone;
        this.role=Role;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }
    public String getRegion(){return region;}
    public String getRole(){return role;}
    public  String getPassword(){return password;}
    public  String getEmail(){return email;}

}



