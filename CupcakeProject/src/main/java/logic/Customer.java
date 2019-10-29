package logic;

import javax.security.auth.login.LoginException;
import static logic.User.getUserListArray;

/**
 * @author andre
 */
public class Customer extends User {

    private static double balance;
    private static ShoppingCart shoppingCart;

    public Customer(String username, String password, String email, String role, double balance) {
        super(username, password, email, role);
        this.balance = balance;
    }

    public void payForShoppingCart() {
        if (this.canBalanceCoverPayment()) {
            double payment = getShoppingCart().getTotalPrice();
            this.balance -= payment;
            User.getUserMapper().updateBalance(this);
        } else {
            // TODO: Throw error
        }
    }

    public boolean canBalanceCoverPayment() {
        if (this.shoppingCart.getTotalPrice() > this.balance) {
            return false;
        }
        return true;
    }

    public static double getBalance() {
        return balance;
    }

    public static ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public static void setShoppingCart(ShoppingCart shoppingCart) {
        Customer.shoppingCart = shoppingCart;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
    
}
