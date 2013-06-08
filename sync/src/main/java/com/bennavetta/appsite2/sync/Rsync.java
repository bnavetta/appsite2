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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.carrotsearch.hppc.ByteArrayDeque;
import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.predicates.ObjectPredicate;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;

/**
 * Implements the <a href="http://en.wikipedia.org/wiki/Rsync">Rsync algorithm</a> in Java.
 * @author ben
 *
 */
public final class Rsync
{
	/**
	 * Hidden constructor.
	 */
	private Rsync() {}
	
	/**
	 * Generate the list of blocks in the receiver's file that will be sent to the sender to run
	 * the algorithm.
	 * @param input a stream to read blocks from
	 * @param blockSize the block size to use. Must be the same on the client and server
	 * @return a list containing the generated blocks
	 * @throws IOException if there is an exception reading the data
	 */
	public static ObjectArrayList<Block> calculateBlocks(final InputStream input, final int blockSize) throws IOException
	{
		final ObjectArrayList<Block> blocks = new ObjectArrayList<Block>();
		final byte[] buf = new byte[blockSize];
		while(input.read(buf, 0, blockSize) != -1)
		{
			blocks.add(new Block(// NOPMD - point of method is to create Block objects in a loop
				RollingChecksum.checksum(buf, 0, blockSize),
				Hashing.md5().hashBytes(buf).asBytes()
			));
		}
		return blocks;
	}
	
	/**
	 * Given the up-to-date file and the list of blocks from the old file, calculate the differences
	 * between the two.
	 * @param oldBlocks the blocks from the old file
	 * @param chunkSize the chunk size used to generate the blocks
	 * @param input the new file
	 * @param listener an event handler that will receive the calculated differences
	 * @throws IOException if there is an exception calculating differences
	 */
	public static void calculateDifferences(final ObjectArrayList<Block> oldBlocks, final int chunkSize, final InputStream input, final DifferenceListener listener) throws IOException
	{	
		final ByteArrayDeque buf = new ByteArrayDeque(chunkSize);
		final ByteArrayList newData = new ByteArrayList(chunkSize); // build up new data
		final byte[] matchBuf = new byte[chunkSize]; //the buffer to pass into findMatch, etc.
		final byte[] single = new byte[1];
		
		boolean inMatch = false;
		int matchRemaining = 0;
		
		listener.onStart();
		
		ByteStreams.readFully(input, matchBuf, 0, chunkSize); // if we read directly into the ByteArrayDeque, it doesn't know that data was added
		buf.addLast(matchBuf);
		long checksum = RollingChecksum.checksum(buf.buffer, buf.head, chunkSize);
		int match = findMatch(oldBlocks, checksum, matchBuf); //only this time, for performance
		if(match != -1)
		{
			listener.onMatch(0);
			inMatch = true;
			matchRemaining = chunkSize-1; // already one byte into match
		}
		
		while(true)
		{
			final byte old = buf.removeFirst();
			final int read = input.read(single);
			if(read == -1)
			{
				break;
			}
			buf.addLast(single[0]);
			checksum = RollingChecksum.update(checksum, old, buf.getLast(), chunkSize);
			
			if(inMatch)
			{
				if(matchRemaining == 0)
				{
					inMatch = false;
				}
				matchRemaining--;
			}
			if(!inMatch) // separate test because we could have just left being in a match
			{
				match = findMatch(oldBlocks, checksum, buf.toArray(matchBuf)); //TODO: figure out how to pass the buffer without copying
				if(match == -1)
				{
					newData.add(buf.getLast());
					//System.out.println("No match at " + i);
				}
				else
				{
					if(newData.size() > 0)
					{
						System.out.println("New data (" + newData.size() + " bytes):");
						System.out.println(Charsets.UTF_8.decode(ByteBuffer.wrap(newData.toArray())));
						System.out.println("Base64 => " + BaseEncoding.base16().encode(newData.toArray()));
						System.out.println("Decimal => " + newData.toString());
						listener.onDifferent(newData.toArray(), 0, newData.size());
						newData.elementsCount = 0; // could call clear(), but this keeps the buffer
						assert newData.size() == 0;
					}
					listener.onMatch(match);
					inMatch = true;
					matchRemaining = chunkSize-1; // already one byte into match
				}
			}
		}
		listener.onFinish();
	}
	
	/**
	 * Search for a match in a list of blocks.
	 * @param blocks the blocks to search in
	 * @param checksum the weak rolling checksum to search
	 * @param data the data being searched for (in case a hash needs to be generated)
	 * @return the index of the matching block, or {@code -1}
	 */
	private static int findMatch(final ObjectArrayList<Block> blocks, final long checksum, final byte[] data)
	{
		//CHECKSTYLE.OFF: VisibilityModifier - using getters for the anonymous class is kind of pointless
		// use a predicate so we can stop iterating once a match is found
		return blocks.forEach(new ObjectPredicate<Block>() {
			int match = -1;
			int index;
			byte[] dataHash;
			@Override
			public boolean apply(final Block value)
			{
				if(value.getChecksum() == checksum)
				{
					// calculate the hash of the input data lazily
					if(dataHash == null)
					{
						dataHash = Hashing.md5().hashBytes(data).asBytes();
					}
					if(Arrays.equals(dataHash, value.getHash()))
					{
						match = index;
						return false;
					}
				}
				index++;
				return true;
			}	
		}).match;
		//CHECKSTYLE.ON: VisibilityModifier
	}
	
	//CHECKSTYLE.OFF: MagicNumber - See below
	//CHECKSTYLE.OFF: UncommentedMain - This is just a quick test
	/**
	 * A test method.
	 * @param args ignored
	 * @throws IOException if something goes wrong
	 */
	@SuppressWarnings("PMD") // this will end up in unit tests soon
	public static void main(String[] args) throws IOException
	{
		/*
		byte[] data = new byte[1024 * 1024];
		for(int i = 0; i < data.length; i++)
		{
			data[i] = (byte) i;
		}
		*/
		//byte[] data = DataUtils.randomData(1024 * 1024);
		//ByteArrayInputStream in = new ByteArrayInputStream(data);
		File oldFile = new File("../test-4mb-old.dat"); // also test.dat - 1 GB
		File newFile = new File("../test-4mb-new.dat");
		
		FileInputStream in = new FileInputStream(oldFile);
		ObjectArrayList<Block> blocks = calculateBlocks(in, 1024);
		in.close();
		
		in = new FileInputStream(newFile);
		//in.reset();
		long start = System.currentTimeMillis();
		calculateDifferences(blocks, 1024, in, new NullDifferenceListener());
		long end = System.currentTimeMillis();
		in.close();
		
		long millis = end - start;
		double seconds = millis / 1000.0;
		double millisPer = millis / (double) (1024*1024*1024);
		double secondsPer = millisPer / 1000.0;
		System.out.printf("Completed in %.4f seconds (%d milliseconds) - average %f seconds (%f milliseconds) per byte%n", seconds, millis, secondsPer, millisPer);
	}
	//CHECKSTYLE.ON: UncommentedMain
	//CHECKSTYLE.ON: MagicNumber
}
