package main;
/**
 *
 * @author MAZ
 */
import com.sun.security.auth.callback.TextCallbackHandler;
import java.math.BigInteger;
import java.security.AccessControlException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
//
import saltmanager.SaltChange;
import saltmanager.SaltRecovery;
//
public final class SaltManager {
  
    static private final String CLASS_NAME = SaltManager.class.getName();
    static private final Logger LOGGER = Logger.getLogger(CLASS_NAME);
    
    public static void main (final String[] args) {
        
        final LoginContext loginContext;
        try {
            loginContext = new LoginContext("SALTMANAGER", new TextCallbackHandler());
        } catch (final LoginException ex) {
            System.out.println("No configuration entry to create specified LoginContext");
            return;
        } catch (final SecurityException ex) {
            System.out.println("No permission to create specified LoginContext (" + ex.getMessage() + ")");
            return;
        }

        try {

            loginContext.login();

            final Subject subject = loginContext.getSubject();

            menu(subject);

            try {
                System.out.println("4");
                loginContext.logout();
            } catch (final LoginException ex) {
                LOGGER.log(Level.SEVERE, "Fallo al eliminar el contexto de login", ex.getCause());
                System.out.println("Fallo al eliminar el contexto de login");
            }
        
        } catch (final LoginException ex) {
            LOGGER.log(Level.WARNING, "Autenticación fallida en arranque de la aplicación", ex.getCause());
            System.out.println("Autenticación fallida en arranque de la aplicación");
        } catch (final AccessControlException ex) {
            LOGGER.log(Level.WARNING, "Problema de permisos en arranque de aplicación", ex.getCause());
            System.out.println("Problema de permisos en arranque de aplicación"); 
            System.out.println(ex);
        }
    }

  private static void menu (final Subject subject) {
    
    final Scanner scanner = new Scanner(System.in);
    int opcion; 
    do {
                   
      System.out.println("Opciones:");
      System.out.println("  1 - Consultar pizca de sal");      
      System.out.println("  2 - Cambiar pizca de sal");      
      System.out.println("  0 - Salir");
      System.out.print("Introduce opcion: ");
      try {  
        opcion = scanner.nextInt();
        scanner.nextLine();
      
        final String filename;
        switch (opcion) {
          case 1:
            System.out.print("Introduce nombre de fichero: ");
            filename = scanner.nextLine();
            final SaltRecovery saltRecover = new SaltRecovery();
            final byte[] saltData = saltRecover.recover(subject, filename);
            if (saltData.length > 0) {
              final BigInteger saltValue = new BigInteger(+1, saltData);
              System.out.println("Pizca de sal: " + saltValue.toString(16));
            } else {
              System.out.println("Problema al intentar recuperar la pizca de sal");
            }
            break;
          case 2:
            System.out.print("Introduce nombre de fichero: ");
            filename = scanner.nextLine();
            final SaltChange saltChange = new SaltChange();
            if (saltChange.change(subject, filename)) {
              System.out.println("Pizca de sal cambiada");
            } else {
              System.out.println("Problema al intentar cambiar la pizca de sal");
            }
            break;
          default:
        }

      } catch (final InputMismatchException ex) {
        scanner.nextLine();
        opcion = Integer.MAX_VALUE;
      }
      
    } while (opcion != 0); 
    
  }
  
}