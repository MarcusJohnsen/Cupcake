package presentation;

import presentation.commands.ConfirmationCommand;
import presentation.commands.RegistrationCommand;
import presentation.commands.ProductsCommand;
import presentation.commands.ShoppageCommand;
import presentation.commands.LoginCommand;
import presentation.commands.UnknownCommand;
import presentation.commands.InvoiceCommand;
import presentation.commands.LogoutCommand;
import presentation.commands.GoToJspCommand;
import java.util.HashMap;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import presentation.commands.AdminFunctionsCommand;

public abstract class Command {

    /**
     * @author Gruppe 3
     * 
     * hosts a list of all commands and directs the flow according to the 
     * requested command key words.
     * 
     */
    
    private static HashMap<String, Command> commands;

    private static void initCommands() {
        commands = new HashMap<>();
        commands.put("login", new LoginCommand());
        commands.put("registration", new RegistrationCommand());
        commands.put("products", new ProductsCommand());
        commands.put("confirmation", new ConfirmationCommand());
        commands.put("shoppage", new ShoppageCommand());
        commands.put("invoice", new InvoiceCommand());
        commands.put("logout", new LogoutCommand());
        commands.put("goToJsp", new GoToJspCommand());
        commands.put("adminfunctions", new AdminFunctionsCommand());
    }

    public static Command from(HttpServletRequest request) {
        String commandName = request.getParameter("command");
        if (commands == null) {
            initCommands();
        }
        return commands.getOrDefault(commandName, new UnknownCommand());
    }

    public abstract String execute(HttpServletRequest request, HttpServletResponse response);
}