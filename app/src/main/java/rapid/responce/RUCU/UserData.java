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
    private String status;
    private String uid;
    private String imageUrl;


    public UserData() {
        // Required empty constructor
    }

    public UserData(String email, String password) {
        this.email = email;
        this.password = password;

    }

    public UserData(String email, String password, String garage_name, String company_name, String full_name, String region, String district, String address, String phone,String nnida, String ttin
   , String uid, String imageUrl, String status ) {
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
        this.uid=uid;
        this.imageUrl=imageUrl;
        this.status=status;

    }

    public String getGarageName() {
        return garageName;
    }

    public String getPhone() {
        return phone;
    }
    public String getRegion(){return region;}
    public String getDistrict(){return district;}
    public String getGarageCompany() {
        return garageCompany;
    }
    public String getUid() {
        return uid;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return imageUrl;
    }
}



