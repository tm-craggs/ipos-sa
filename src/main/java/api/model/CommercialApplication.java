package api.model;

public class CommercialApplication {
    private int id;
    private String company_name;
    private int reg_num;
    private String email;
    private String phone;
    private String address;
    private String director;

    // blank constructor for jackson parsing
    public CommercialApplication() {}

    // regular constructor
    public CommercialApplication(int id, String name, int reg, String email, String phone, String addr, String dir) {
        this.id = id;
        this.company_name = name;
        this.reg_num = reg;
        this.email = email;
        this.phone = phone;
        this.address = addr;
        this.director = dir;
    }

    // getters
    public int getId() { return id; }
    public String getCompany_name() { return company_name; }
    public int getReg_num() { return reg_num; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getDirector() { return director; }
}