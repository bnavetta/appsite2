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

import java.io.DataOutputStream;
import java.io.IOException;

import com.bennavetta.appsite2.sync.DifferenceListener;

/**
 * A {@link DifferenceListener} implementation that writes differences in a format that can be read by {@link DifferenceReader}.
 * It uses constants in the {@link Constants} class. 
 * @author ben
 */
public class DifferenceWriter implements DifferenceListener
{
	/**
	 * The stream to write differences to.
	 */
	private final DataOutputStream out;
	
	/**
	 * Create a new {@code DifferenceWriter} that will write differences to the given stream.
	 * @param out the stream to write to
	 */
	public DifferenceWriter(final DataOutputStream out)
	{
		this.out = out;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onMatch(final int block) throws IOException
	{
		out.write(Constants.TYPE_BLOCK_MATCH);
		out.writeInt(block);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDifferent(final byte[] data, final int off, final int len) throws IOException
	{
		out.write(Constants.TYPE_NEW_DATA);
		out.writeInt(len);
		out.write(data, off, len);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStart()
	{
		// nothing to do
	}

	/**
	 * {@inheritDoc}
	 * @throws IOException 
	 */
	@Override
	public void onFinish() throws IOException
	{
		out.write(Constants.TYPE_FINISHED);
	}

}
