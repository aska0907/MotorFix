package rapid.responce.RUCU;
public class UserData {
    public String email;
    public String password;
    private String garageName;
    private String garageCompany;
    private String fullName;
    private String region;
    private String district;
    private String address;
    private String phone;
    private  String nida;
    private  String tin;
    private  String role;

    public UserData() {
        // Required empty constructor
    }

    public UserData(String email, String password) {
        this.email = email;
        this.password = password;

    }

    public UserData(String email, String Role, String password, String garage_name, String company_name, String full_name, String region, String district, String address, String phone,String nnida, String ttin) {
        this.email = email;
        this.password = password;
        this.garageName = garage_name;
        this.garageCompany = company_name;
        this.fullName = full_name;
        this.region = region;
        this.district = district;
        this.address = address;
        this.phone = phone;
        this.nida=nnida;
        this.tin=ttin;
        this.role=Role;
    }

    public String getGarageName() {
        return garageName;
    }

    public String getPhone() {
        return phone;
    }
    public String getRegion(){return region;}
}



