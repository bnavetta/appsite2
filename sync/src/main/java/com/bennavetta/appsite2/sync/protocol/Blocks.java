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
import java.io.DataOutputStream;
import java.io.IOException;

import com.bennavetta.appsite2.sync.Block;
import com.carrotsearch.hppc.ObjectArrayList;

/**
 * Utility methods for reading and writing blocks encoded by the protocol implemented
 * in this package.
 * @author ben
 *
 */
public class Blocks
{	
	/**
	 * Hidden constructor.
	 */
	private Blocks() {}
	
	/**
	 * Read blocks from the given stream.
	 * @param in the stream to read blocks from
	 * @return the blocks read
	 * @throws IOException if there is an exception reading the blocks
	 * @see #writeBlocks(ObjectArrayList, DataOutputStream)
	 */
	public static ObjectArrayList<Block> readBlocks(DataInputStream in) throws IOException
	{
		int numberOfBlocks = in.readInt();
		ObjectArrayList<Block> blocks = new ObjectArrayList<Block>(numberOfBlocks);
		for(int i = 0; i < numberOfBlocks; i++)
		{
			long checksum = in.readLong();
			byte[] hash = new byte[Constants.HASH_LENGTH]; // the hash is always the same size
			in.readFully(hash);
			blocks.add(new Block(checksum, hash));
		}
		return blocks;
	}
	
	/**
	 * Write a list of blocks to the given stream.
	 * @param blocks the blocks to write
	 * @param out the stream to write to
	 * @throws IOException if there is an exception writing the blocks
	 * @see {@link #readBlocks(DataInputStream)}
	 */
	public static void writeBlocks(ObjectArrayList<Block> blocks, DataOutputStream out) throws IOException
	{
		final int size = blocks.size();
		out.writeInt(size);
		// see javadoc for why the casts are necessary
		final Object[] buffer = (Object[]) blocks.buffer;
		for(int i = 0; i < size; i++)
		{
			Block block = (Block) buffer[i];
			out.writeLong(block.getChecksum());
			out.write(block.getHash()); // since it's a hash, the size is constant
		}
	}
}
