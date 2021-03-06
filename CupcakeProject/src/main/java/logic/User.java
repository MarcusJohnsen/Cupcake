package logic;

import java.util.ArrayList;
import java.util.HashMap;
import javax.security.auth.login.LoginException;
import persistence.mappers.UserMapperInterface;

/**
 *
 * @author Gruppe 3
 */
public abstract class User {

    private String username;
    private String password;
    private String email;
    private String role;
    private static final double newUserBalance = 50;

    private static UserMapperInterface userMapper;
    private static ArrayList<User> userList = new ArrayList();
    
    /**
     * Setup the User class with the correct Mapper for either testing or execution, 
     * and pull all data from the relevent mapper to make local variables matching the given database.
     * 
     * @author Michael N. Korsgaard
     * @param mapper Correct Mapper or Fake Mapper for testing
     */
    public static void setupUserClass(UserMapperInterface mapper) {
        User.userMapper = mapper;
        createUsersFromDB();
    }

    /**
     * Search the list of users for a User with a matching email to the given parameter String, and return
     * the User. If no match is found, IllegalArgumentExcepton is thrown with that message.
     * 
     * @author Marcus E. Johnsen
     * @param email String that should be matching a String email for a user in the userList
     * @return return user with matching email to given String
     * @throws IllegalArgumentException if no user has matching email
     */
    public static User getUserFromUserList(String email) throws IllegalArgumentException {
        for (User user : userList) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        throw new IllegalArgumentException("User not found in userList");
    }

    public User(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        userList.add(this);
    }

    public static ArrayList<User> getUserList() {
        return userList;
    }

    /**
     * Create Users from the data in DB, and store then in the UserList.
     * 
     * @author Cahit Bakirci
     */
    public static void createUsersFromDB() {
        userList.clear();
        for (HashMap<String, String> map : userMapper.getUserList()) {
            String username = map.get("username");
            String password = map.get("login");
            String email = map.get("email");
            String role = map.get("role");
            if (role.equals("a")) {
                User user = new Admin(username, password, email, role);
            } else if (role.equals("c")) {
                Double balance = Double.parseDouble(map.get("balance"));
                User user = new Customer(username, password, email, role, balance);
            }
        }
    }

    /**
     * Attempt to login user with given email and password, and returns user where email matches if correct email is given.
     * 
     * @author Michael N. Korsgaard
     * @param email String to be used for login
     * @param password String to be used for login
     * @return User that matching Email and Password
     * @throws LoginException thrown is no user with matching Email is found, or if password is incorrect.
     */
    public static User LoginUser(String email, String password) throws LoginException {
        for (User user : userList) {
            if (user.getEmail().toLowerCase().equals(email.toLowerCase())) {
                if (user.getPassword().equals(password)) {
                    if (User.isUserCustomer(user)) {
                        Customer customer = (Customer) user;
                        if (customer.getShoppingCart() == null) {
                            customer.setShoppingCart(new ShoppingCart());
                        }
                        return user;
                    } else if (User.isUserAdmin(user)) {
                        return user;
                    }
                } else {
                    throw new LoginException("Wrong Email/Password");
                }
            }
        }
        throw new LoginException("Wrong Email/Password");
    }

    /**
     * Takes user input and check for valid input. 
     * IllegalArgumentException thrown if any input cannot be used to create a new User with unique data.
     * If all input is valid to create new user, new user is created locally and pushed into DB thought UserMapper.
     * 
     * @author Marcus & Cahit
     * @param username user input for username
     * @param password user input for password
     * @param password2 dublicate user input for password for confirmation
     * @param email user input for email
     * @throws IllegalArgumentException thrown if any input cannot be used to create a new User with unique data.
     */
    public static void RegisterUser(String username, String password, String password2, String email) throws IllegalArgumentException {

        //check for unfilled forms in registration
        // TODO: Check email for @ and minimum of 1 dot
        boolean noUsername = username.length() < 1;
        boolean noPassword = password.length() < 1;
        boolean noEmail = email.length() < 1;

        if (noUsername || noPassword || noEmail) {
            throw new IllegalArgumentException("remember to fill out all fields");
        }
        checkPasswordValidation(password, password2);
        checkEmailValidation(email);

        Customer newCustomer = new Customer(username, password, email, "c", newUserBalance);
        userMapper.insertUser(newCustomer);
    }

    /**
     * Check validation of password for user creation.
     * 
     * @author Cahit Bakirci
     * @param password user input for password
     * @param password2 dublicate user input for password for confirmation
     * @throws IllegalArgumentException thrown if any input cannot be used to create a new User with unique data.
     */
    private static void checkPasswordValidation(String password, String password2) throws IllegalArgumentException {
        // Check password contains between 6 and 20 characters
        if (password.length() < 6 || 20 < password.length()) {
            throw new IllegalArgumentException("Your password needs to contain at least a "
                    + "uppercase letter, lowercase letter, a number and be between 6 and 20 characters. Please try again.");
        }

        // Check password contains uppercase, lowercase and number
        boolean containsUppercaseLetter = false;
        boolean containsLowercaseLetter = false;
        boolean containsNumber = false;
        for (char s : password.toCharArray()) {
            if (Character.isUpperCase(s)) {
                containsUppercaseLetter = true;
            }
            if (Character.isLowerCase(s)) {
                containsLowercaseLetter = true;
            }
            if (Character.isDigit(s)) {
                containsNumber = true;
            }
        }
        if (!containsLowercaseLetter || !containsUppercaseLetter || !containsNumber) {
            throw new IllegalArgumentException("Your password needs to contain at least an "
                    + "uppercase letter, lowercase letter, a number and be between 6 and 20 characters. Please try again.");
        }

        // Check passwords matches
        if (!password2.equals(password)) {
            throw new IllegalArgumentException("passwords do not match.");
        }
    }

    /**
     * Check validation of email for user creation.
     * 
     * @author Michael N. Korsgaard
     * @param email user input for email
     * @throws IllegalArgumentException thrown if any input cannot be used to create a new User with unique data.
     */
    private static void checkEmailValidation(String email) throws IllegalArgumentException {

        //Check email for containing email symbols
        boolean containsAtSymbol = false;
        boolean containsDotSymbolAfterAt = false;
        for (char character : email.toCharArray()) {
            if (character == '@') {
                containsAtSymbol = true;
            }
            if (containsAtSymbol && character == '.') {
                containsDotSymbolAfterAt = true;
            }
        }
        if (!containsAtSymbol || !containsDotSymbolAfterAt) {
            throw new IllegalArgumentException("please write valid email with @ and .");
        }
        
        //Check for dublication
        for (User user : userList) {
            if (email.toLowerCase().equals(user.email.toLowerCase())) {
                throw new IllegalArgumentException("email is already in use.");
            }
        }
    }

    public static boolean isUserCustomer(User user) {
        return user.getClass() == Customer.class;
    }

    public static boolean isUserAdmin(User user) {
        return user.getClass() == Admin.class;
    }

    public static ArrayList<User> getUserListArray() {
        return userList;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public double getNewUserBalance() {
        return newUserBalance;
    }

    public static UserMapperInterface getUserMapper() {
        return userMapper;
    }
}
