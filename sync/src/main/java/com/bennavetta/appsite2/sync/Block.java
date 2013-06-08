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

import java.util.Arrays;

import com.google.common.io.BaseEncoding;

/**
 * A holder for the information needed about a block of data. {@code Block} objects are immutable.
 * @author ben
 *
 */
public class Block
{
	/**
	 * The weak checksum of the block.
	 */
	private final long checksum;
	
	/**
	 * The MD5 checksum of the block.
	 */
	private final byte[] hash;
	
	/**
	 * Create a new block with the given checksum and hash.
	 * @param checksum the weak rolling checksum
	 * @param hash the stronger MD5 hash (will be copied)
	 */
	public Block(final long checksum, final byte[] hash)
	{	
		this.checksum = checksum;
		this.hash = Arrays.copyOf(hash, hash.length);
	}
	
	/**
	 * Get the checksum of this block.
	 * @return a weak rolling checksum as a {@code long} integer.
	 * @see RollingChecksum
	 */
	public long getChecksum()
	{
		return checksum;
	}
	
	/**
	 * Get the MD5 hash of this block.
	 * @return a copy of the 128-bit (16-byte) MD5 hash of this block
	 */
	public byte[] getHash()
	{
		return Arrays.copyOf(hash, hash.length);
	}
	
	@Override
	public String toString()
	{
		return "Block [checksum: " + checksum + ", hash: " + BaseEncoding.base16().encode(hash) + "]";
	}
}
