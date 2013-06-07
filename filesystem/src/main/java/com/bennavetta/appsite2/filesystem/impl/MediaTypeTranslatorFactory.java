package com.bennavetta.appsite2.filesystem.impl;

import java.lang.reflect.Type;

import com.google.common.net.MediaType;
import com.googlecode.objectify.impl.Path;
import com.googlecode.objectify.impl.Property;
import com.googlecode.objectify.impl.translate.CreateContext;
import com.googlecode.objectify.impl.translate.LoadContext;
import com.googlecode.objectify.impl.translate.SaveContext;
import com.googlecode.objectify.impl.translate.SkipException;
import com.googlecode.objectify.impl.translate.ValueTranslator;
import com.googlecode.objectify.impl.translate.ValueTranslatorFactory;

/**
 * Translator factory for converting {@link MediaType} instances to and from {@link String} instances. This enables
 * entities to have {@code MediaType} fields. 
 * @author ben
 *
 */
public class MediaTypeTranslatorFactory extends ValueTranslatorFactory<MediaType, String>
{
	/**
	 * Create a new translator factory.
	 */
	public MediaTypeTranslatorFactory()
	{
		super(MediaType.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ValueTranslator<MediaType, String> createSafe(Path path, Property property, Type type, CreateContext ctx)
	{
		return new MediaTypeTranslator(path);
	}
	
	/**
	 * The implementation translator class.
	 * @author ben
	 *
	 */
	private class MediaTypeTranslator extends ValueTranslator<MediaType, String>
	{
		/**
		 * Create a new translator for the given path.
		 * @param path the path of the property
		 */
		public MediaTypeTranslator(Path path)
		{
			super(path, String.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected MediaType loadValue(String value, LoadContext ctx) throws SkipException
		{
			return MediaType.parse(value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String saveValue(MediaType value, SaveContext ctx) throws SkipException
		{
			return value.toString();
		}

	}
}
