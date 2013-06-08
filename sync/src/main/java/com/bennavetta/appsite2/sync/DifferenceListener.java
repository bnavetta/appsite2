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

import java.io.IOException;

/**
 * A callback for {@link Rsync#calculateDifferences(com.carrotsearch.hppc.ObjectArrayList, int, java.io.InputStream)}.
 * @author ben
 *
 */
public interface DifferenceListener
{
	/**
	 * Called when a match is found with a block from the receiver's file.
	 * @param block the block index in the receiver's file
	 * @throws IOException if there is an error processing the match
	 */
	public void onMatch(int block) throws IOException;
	
	/**
	 * Called when new data is found (not in receiver's file).
	 * @param data the buffer containing the new data
	 * @param off the offset within the buffer that the new data begins at
	 * @param len the length of the new data
	 * @throws IOException if there is an error processing the match
	 */
	public void onDifferent(byte[] data, int off, int len) throws IOException;
	
	/**
	 * Called when difference calculation has begun.
	 * @throws IOException if there is an error processing the match
	 */
	public void onStart() throws IOException;
	
	/**
	 * Called when difference calculation has finished.
	 * @throws IOException if there is an error processing the match
	 */
	public void onFinish() throws IOException;
}
