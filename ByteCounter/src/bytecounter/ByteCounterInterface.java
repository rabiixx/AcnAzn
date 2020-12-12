/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bytecounter;

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
public final class ByteCounterInterface {
    
    static private final String CLASS_NAME = ByteCounterInterface.class.getName();
    static private final Logger LOGGER = Logger.getLogger(CLASS_NAME);
    
    public int count( final Subject subject, final String filename ) {
        try {
            final PrivilegedAction<Integer> byteCounter = new ByteCounter( subject, filename );
            
            return AccessController.doPrivileged(byteCounter);
        } catch ( final AccessControlException ex ) {
            LOGGER.log(Level.WARNING, "problema de permisos", ex);
            System.out.println("Error: " + ex.getCause());
            return -1;
        }
    }
    
}
