package lk.ijse.pos.dto;

public class LoginDTO {
    private String password;
    private String userName;
    private String jobRole;

    public LoginDTO() {

    }

    public LoginDTO(String password, String userName, String jobRole) {
        this.password = password;
        this.userName = userName;
        this.jobRole = jobRole;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }
}
