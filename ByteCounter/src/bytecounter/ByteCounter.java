package bytecounter;
/**
 *
 * @author MAZ
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessControlException;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
//
public final class ByteCounter implements PrivilegedAction<Integer>{
  
    static private final String CLASS_NAME = ByteCounter.class.getName();
    static private final Logger LOGGER = Logger.getLogger(CLASS_NAME);
  
    private final Subject subject;
    private final String filename;
  
    protected ByteCounter ( final Subject subject, final String filename ) {
        this.subject = subject;
        this.filename = filename;
    }
 
  private int count (final String filename) {
    
    final String path = System.getProperty("user.dir") + File.separator +
                        "users" + File.separator;
    final File file = new File(path + filename);
    
    try (final FileInputStream     fis = new FileInputStream(file);
         final BufferedInputStream bis = new BufferedInputStream(fis)) {
      
      int bytesCounter = 0;
      while (bis.read() != -1) {
        ++bytesCounter;
      }
      
      return bytesCounter;
      
    } catch (final FileNotFoundException ex) {
      LOGGER.log(Level.WARNING, "{0}: {1}", new Object[]{CLASS_NAME, ex.getMessage()});
    } catch (final IOException ex) {
      LOGGER.log(Level.WARNING, "{0}: {1}", new Object[]{CLASS_NAME, ex.getMessage()});
    }
    
    return -1;

  }
  
    @Override
    public Integer run() {
        try {
            return Subject.doAsPrivileged(subject, new PrivilegedAction<Integer>() {
                @Override
                public Integer run() {
                    return count( filename );
                }
            }, null);
        } catch (final AccessControlException ex) {
            LOGGER.log(Level.WARNING, "sujeto sin permisos", ex);
            System.out.println("Error: " + ex.getCause());
            return -1;
        }
    }

}