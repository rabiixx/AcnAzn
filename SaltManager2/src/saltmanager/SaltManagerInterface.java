/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package saltmanager;

import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;

/**
 *
 * @author kali
 */
public final class SaltManagerInterface {
    
    static private final String CLASS_NAME = SaltManagerInterface.class.getName();
    static private final Logger LOGGER = Logger.getLogger(CLASS_NAME);
    
    public boolean change( final Subject subject, final String filename ) {
        try {
            final PrivilegedAction<Boolean> saltChange = new SaltChange( subject, filename );
            return AccessController.doPrivileged( saltChange );
        } catch ( final AccessControlException ex ) {
            LOGGER.log(Level.WARNING, "problema de permisos", ex);
            System.out.println("Error: " + ex.getCause());
            return Boolean.FALSE;
        }
    }
    
    public byte[] recover( final Subject subject, final String filename ) {
        try {
            final PrivilegedAction<byte[]> saltRecovery = new SaltRecovery(subject, filename );
            return AccessController.doPrivileged( saltRecovery );
        } catch ( final AccessControlException ex ) {
            LOGGER.log(Level.WARNING, "problema de permisos", ex);
            System.out.println("Error: " + ex.getCause());
            return new byte[0];
        }
    }
  
}
