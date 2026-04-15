public class Company {

    private String companyName;
    private String address;
    private String phone;
    private String email;
    private String contactName;
    private String contactTitle;

    public Company(String companyName, String address, String phone, String email, String contactName) {
        this(companyName, address, phone, email, contactName, "");
    }

    public Company(String companyName, String address, String phone, String email, String contactName, String contactTitle) {
        this.companyName = companyName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.contactName = contactName;
        this.contactTitle = contactTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactTitle() {
        return contactTitle;
    }

    public void setContactTitle(String contactTitle) {
        this.contactTitle = contactTitle;
    }
}