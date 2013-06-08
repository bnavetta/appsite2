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
package com.bennavetta.appsite2.sync.protocol;

import java.io.DataInputStream;
import java.io.IOException;

import com.bennavetta.appsite2.sync.DifferenceListener;

/**
 * Read a stream of differences written by a {@code DifferenceWriter}.
 * @author ben
 *
 */
public class DifferenceReader
{
	/**
	 * Create a new {@code DifferenceReader}.
	 */
	public DifferenceReader()
	{
		// nothing to do
	}
	
	/**
	 * Read the encoded differences from a stream.
	 * @param in the stream to read from
	 * @param listener a handler for the read differences
	 * @throws IOException if there is an exception reading the differences
	 */
	public void readDifferences(DataInputStream in, DifferenceListener listener) throws IOException
	{
		listener.onStart();
		
		byte type = -1;
		MAIN: while(true)
		{
			type = in.readByte();
			switch(type)
			{
			case Constants.TYPE_FINISHED:
				break MAIN;
			case Constants.TYPE_BLOCK_MATCH:
				listener.onMatch(in.readInt());
				break;
			case Constants.TYPE_NEW_DATA:
				byte[] data = new byte[in.readInt()];
				in.readFully(data);
				listener.onDifferent(data, 0, data.length);
				break;
			default:
				throw new IOException("Unknown difference instruction: " + type);
			}
		}
		
		listener.onFinish();
	}
}
