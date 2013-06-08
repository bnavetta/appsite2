/**
 * Copyright 2013 Ben Navetta <ben.navetta@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bennavetta.appsite2.sync;


/**
 * An implementation of the Rsync rolling checksum.
 * @author ben
 *
 */
public final class RollingChecksum
{	
	/**
	 * The hidden constructor - this class should not be instantiated.
	 */
	private RollingChecksum() {}
	
	/**
	 * Calculate the checksum for a block of data.
	 * @param data the data buffer
	 * @param off the offset within the buffer to read from
	 * @param len the length of data to use when calculating the checksum (the block size)
	 * @return the checksum as a {@code long}
	 */
	public static long checksum(final byte[] data, final int off, final int len)
	{
		int a = 0; // NOPMD - using variable names from algorithm
		int b = 0; // NOPMD - using variable names from algorithm
		for(int i = off; i < off + len; i++)
		{
			a += data[i];
			b += a;
			// b += (len - i) * data[i]
		}
		return combine(a, b);
	}
	
	/**
	 * Update a previously-calculated checksum with new data.
	 * @param checksum the checksum to start with
	 * @param prev the byte that was removed (just before the new data window)
	 * @param added the new byte (whatever was just read)
	 * @param length the block size used when calculating the checksum
	 * @return the updated checksum as a {@code long}
	 */
	public static long update(final long checksum, final byte prev, final byte added, final int length)
	{
		int a = getA(checksum); // NOPMD - using variable names from algorithm
		int b = getB(checksum); // NOPMD - using variable names from algorithm
		
		a -= prev - added;
		b -= prev *  length - a;
		return combine(a, b);
	}
	
	/**
	 * Combine two {@code int}s into a {@code long}.
	 * @param a the first integer
	 * @param b the second integer
	 * @return {@code a} and {@code b} as a {@code long}
	 * @see #getA(long)
	 * @see #getB(long)
	 */
	private static long combine(final int a, final int b) // NOPMD - using variable names from algorithm
	{
		//CHECKSTYLE.OFF: MagicNumber - Clearer to have the bit shift constants here
		return (long)a << 32 | b & 0xFFFFFFFFL;
		//CHECKSTYLE.ON: MagicNumber
	}
	
	/**
	 * Extract the first {@code int} from a {@code long} - the first 32 bits.
	 * @param c the {@code long}
	 * @return the first {@code int}
	 * @see #combine(int, int)
	 * @see #getB(long)
	 */
	private static int getA(final long c) // NOPMD - using variable names from algorithm
	{
		//CHECKSTYLE.OFF: MagicNumber - More readable than (int)(c >> FIRST_BIT_SHIFT) or something
		return (int)(c >> 32);
		//CHECKSTYLE.ON: MagicNumber
	}
	
	/**
	 * Extract the second {@code int} from a {@code long} - the second 32 bits.
	 * @param c the {@code long}
	 * @return the second {@code int}
	 * @see #combine(int, int)
	 * @see #getA(long)
	 */
	private static int getB(final long c) // NOPMD - using variable names from algorithm
	{
		return (int)c;
	}
}
