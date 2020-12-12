package main;
/**
 *
 * @author MAZ
 */
import com.sun.security.auth.callback.TextCallbackHandler;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
//
import bytecounter.ByteCounter;
import bytecounter.ByteCounterInterface;
import java.security.AccessControlException;

public final class App {
  
  static private final String CLASS_NAME = App.class.getName();
  static private final Logger LOGGER = Logger.getLogger(CLASS_NAME);
    
  /**
   * @param args the command line arguments
   */
  public static void main (final String[] args) {
    
    final LoginContext loginContext;
    try {
      loginContext = new LoginContext("BYTECOUNTER2", new TextCallbackHandler());
    } catch (final LoginException ex) {
      System.out.println("No configuration entry to create specified LoginContext");
      return;
    } catch (final SecurityException ex) {
      System.out.println("No permission to create specified LoginContext (" + ex.getMessage() + ")");
      return;
    }

    try {

      // 1: intento de autenciación.
      loginContext.login();

      // 2: se recupera el sujeto resultante de la autenticación.
      final Subject subject = loginContext.getSubject();

      // 3: se ofrece el menú de servicio; obsérvese
      // que se pasa el sujeto autenticado.
      menu(subject);
      
      // 4: se elimina sujeto autenticado y credenciales asociadas
      try {
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
    }
    
  }

  private static void menu (final Subject subject) {
    
    final Scanner scanner = new Scanner(System.in);
    int opcion; 
    do {
                   
      System.out.println("Opciones:");
      System.out.println("  1 - Contar octetos");
      System.out.println("  0 - Salir");
      System.out.print("Introduce opcion: ");
      try {  
        opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
          case 1:
            System.out.print("Introduce la ubicación (nombre de tu directorio + / + nombre de fichero) de tu fichero : ");
            final String filename = scanner.next();
            final ByteCounterInterface byteCounterInterface = new ByteCounterInterface();
            final int bytes = byteCounterInterface.count(subject, filename);
            if (bytes >= 0) {
              System.out.println("Fichero " + filename + ": " + bytes + " octetos");
            } else {
              System.out.println("Problema al operar con fichero");
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