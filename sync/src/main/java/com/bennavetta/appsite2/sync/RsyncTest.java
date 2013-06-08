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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.carrotsearch.hppc.ObjectArrayList;
import com.google.common.io.BaseEncoding;

/**
 * A test class.
 * @author ben
 *
 */

@SuppressWarnings("PMD") // this will end up being broken into unit tests soon
public class RsyncTest
{
	/**
	 * The block size to use for rsync.
	 */
	private static final int BLOCK_SIZE = 8;
	
	/**
	 * Hidden constructor.
	 */
	private RsyncTest() {}
	
	/**
	 * Run the test.
	 * @param args ignored
	 * @throws IOException if something goes wrong
	 */
	//CHECKSTYLE.OFF: UncommentedMain - Still debugging this one
	public static void main(String[] args) throws IOException
	{
		File oldFile = new File("../data/old.txt");
		File newFile = new File("../data/new.txt");
		File updatedFile = new File("../data/updated.txt");
		
		ObjectArrayList<Block> blocks = getBlocks(oldFile);
		
		try(RandomAccessFile oldSource = new RandomAccessFile(oldFile, "r");
				InputStream newIn = new FileInputStream(newFile);
				FileOutputStream out = new FileOutputStream(updatedFile))
		{
			Rsync.calculateDifferences(blocks, BLOCK_SIZE, newIn, new DifferenceListener() {
				private byte[] buf = new byte[BLOCK_SIZE];
				@Override
				public void onStart() throws IOException
				{
					System.out.println("Starting difference calculation");
				}
				
				@Override
				public void onMatch(int block) throws IOException
				{
					System.out.println("Matched block " + block);
					oldSource.seek((long) block * BLOCK_SIZE);
					oldSource.readFully(buf);
					out.write(buf);
				}
				
				@Override
				public void onFinish() throws IOException
				{
					System.out.println("Finished difference calculation");
					out.flush();
				}
				
				@Override
				public void onDifferent(byte[] data, int off, int len) throws IOException
				{
					System.out.println("Writing new data: " + BaseEncoding.base16().encode(data, off, len));
					out.write(data, off, len);
				}
			});
		}
	}
	//CHECKSTYLE.ON: UncommentedMain

	/**
	 * Calculate the blocks in a file.
	 * @param file the data file
	 * @return a list of blocks
	 * @throws IOException if {@link Rsync#calculateBlocks(InputStream, int)} throws an exception or
	 * there is an error opening the file
	 * @see {@link Rsync#calculateBlocks(InputStream, int)}
	 */
	private static ObjectArrayList<Block> getBlocks(File file) throws IOException
	{
		try(InputStream in = new FileInputStream(file))
		{
			return Rsync.calculateBlocks(in, BLOCK_SIZE);
		}
	}
}
