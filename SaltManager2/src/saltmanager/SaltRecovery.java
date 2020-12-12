package saltmanager;
/**
 *
 * @author MAZ
 */
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;import java.security.AccessControlException;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
//
public final class SaltRecovery implements PrivilegedAction<byte[]> {
  
    static private final String CLASS_NAME = SaltRecovery.class.getName();  
    static private final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    private final Subject subject;
    private final String filename;
    
    protected SaltRecovery(final Subject subject, final String filename ) {
        this.subject = subject;
        this.filename = filename;
    }
    
    private byte[] recover (final String filename) {
    
        final String path = System.getProperty("user.dir") + File.separator +
                                               "users" + File.separator;
        final File file = new File(path + filename);

        try (final DataInputStream is = new DataInputStream(new FileInputStream(file))) {
            final int length = is.available();
            final byte[] bytes = new byte[length];
            is.readFully(bytes);
            return bytes;
        } catch (final FileNotFoundException ex) {
            LOGGER.log(Level.WARNING, "fichero no encontrado");
        } catch (final IOException ex) {
            LOGGER.log(Level.WARNING, "problema de lectura");
        }
    
        return new byte[0];
    }
    

    @Override
    public byte[] run() {
        try {
            return Subject.doAsPrivileged(subject, (PrivilegedAction<byte[]>) () -> recover( filename ), null);
        } catch (final AccessControlException ex) {
            LOGGER.log(Level.WARNING, "sujeto sin permisos", ex);
            System.out.println("Error: " + ex.getCause());
            return new byte[0];
        }
    }
    
}