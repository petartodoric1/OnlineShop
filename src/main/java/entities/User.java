package entities;

/**
 * Predstavlja jednog korisnika
 */
public class User {

    private String username;
    private String password;
    private String email;
    private Integer userId;

   public User(){}

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUsername(String username){
       this.username=username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Kreira jednog korisnika sa obaveznim parametrima
     * @param builder
     */
    private User(Builder builder){

        this.username=builder.username;
        this.password=builder.password;
        this.email=builder.email;
        this.userId=builder.userId;
    }

    public static class Builder{

        private final String username;
        private final String password;

        private String email;
        private Integer userId;

        public Builder(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }
        public Builder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }








}
