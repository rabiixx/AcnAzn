package saltmanager;
/**
 *
 * @author MAZ
 */
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessControlException;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
//
public final class SaltChange {
  
  static private final String CLASS_NAME = SaltChange.class.getName();  
  static private final Logger LOGGER = Logger.getLogger(CLASS_NAME);
  
  private boolean operate (final String filename) {
    
    final String path = System.getProperty("user.dir") + File.separator +
                                               "users" + File.separator;
    final File file = new File(path + filename);

    try (final DataOutputStream os = new DataOutputStream(new FileOutputStream(file))) {

      final SecureRandom rg = new SecureRandom();

      final byte[] bytes = new byte[64];
      rg.nextBytes(bytes);
      os.write(bytes);
      return true;

    } catch (final FileNotFoundException ex) {
      LOGGER.log(Level.WARNING, "fichero {0} no encontrado", filename);
    } catch (final IOException ex) {
      LOGGER.log(Level.WARNING, "problema de escritura en fichero {0}", filename);
    }
    
    return false;    
        
  }
  
  public boolean change( final Subject subject, final String filename ) {
        try {
            return Subject.doAsPrivileged(subject, (PrivilegedAction<Boolean>) () -> operate( filename ), null);
        } catch ( final AccessControlException ex ) {
            LOGGER.log(Level.WARNING, "sujeto sin permisos", ex);
            System.out.println("Error: " + ex.getMessage());
            return Boolean.FALSE;
        }
    }
  
  
}