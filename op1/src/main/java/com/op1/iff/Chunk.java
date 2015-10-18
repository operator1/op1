package com.op1.iff;

import com.op1.iff.types.ID;
import com.op1.iff.types.SignedLong;

/**
 * Represents a chunk in an iff.
 */
public interface Chunk {

    /**
     * Returns the ID of this chunk.
     */
    ID getChunkID();

    /**
     * Returns the size in bytes of the data portion of the chunk. The count does not include the 8 bytes used by the
     * chunk ID and chunk size. Also, please note, when the data portion of a chunk contains an odd number of bytes,
     * chunk readers should expect to read an extra pad byte (0) which is also not included in the chunk size count.
     */
    SignedLong getChunkSize();

    /**
     * Returns the actual number of bytes used to represent this Chunk. This should be equal to the value returned by
     * getChunkSize() + 8 [+1 if getChunkSize() is odd].
     */
    int getPhysicalSize();
}
