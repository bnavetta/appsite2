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

import com.google.common.hash.Hashing;

/**
 * Constants for use in the protocol.
 * @author ben
 *
 */
public final class Constants
{	
	/**
	 * The length in bytes of the hash used by the algorithm (MD5).
	 * <br/>
	 * Value: {@value}
	 */
	public static final int HASH_LENGTH = Hashing.md5().bits() / Byte.SIZE;
	
	/**
	 * The code to write out indicating that a matched block is being written.
	 * <br/>
	 * Value: {@value}
	 */
	public static final byte TYPE_BLOCK_MATCH = 0;
	
	/**
	 * The code to write out indicating that new data is being written.
	 * <br/>
	 * Value: {@value}
	 */
	public static final byte TYPE_NEW_DATA = 1;
	
	/**
	 * The code to write out indicating that all data has been written.
	 * <br/>
	 * Value: {@value}
	 */
	public static final byte TYPE_FINISHED = 2;
	
	/**
	 * Hidden constructor.
	 */
	private Constants() {}
}
