// IFayeService.aidl
package catchla.yep;

import catchla.yep.model.InstantStateMessage;

// Declare any non-default types here with import statements

interface IFayeService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    boolean instantState(in InstantStateMessage message);
}
