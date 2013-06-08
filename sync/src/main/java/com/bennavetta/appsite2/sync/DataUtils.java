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

import java.util.Random;

/**
 * Utilities for generating test data.
 * @author ben
 *
 */
public class DataUtils
{
	/**
	 * The size of the data buffer to use.
	 */
	private static final int BUF_SIZE = 1024;
	
	/**
	 * Hidden constructor.
	 */
	private DataUtils() {}
	
	/**
	 * Generate random data.
	 * @param length the amount of data to generate.
	 * @return a {@code byte[]} of the given length filled with random bytes
	 * @see Random#nextBytes(byte[])
	 */
	public static byte[] randomData(int length)
	{
		Random rand = new Random();
		byte[] buf = new byte[BUF_SIZE];
		byte[] out = new byte[length];
		
		for(int i = 0; i < out.length; i += buf.length)
		{
			rand.nextBytes(buf);
			// use Math.min in case the requested amount isn't a multiple of BUF_SIZE
			System.arraycopy(buf, 0, out, i, Math.min(buf.length, out.length - i));
		}
		return out;
	}
	
	/**
	 * Generate sequential data (0, 1, 2, ...). The data will wrap around when it reaches the
	 * limit of a byte.
	 * @param length the length of the buffer to generate
	 * @return the generated data
	 */
	public static byte[] sequentialData(int length)
	{
		byte[] out = new byte[length];
		for(int i = 0; i < length; i++)
		{
			out[i] = (byte) i;
		}
		return out;
	}
}
