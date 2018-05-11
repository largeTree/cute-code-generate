package com.qiuxs.codegenerate.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import freemarker.cache.TemplateLoader;

public class CuteTemplateLoader implements TemplateLoader {

	private static final String TEMPLATE_PATH = "/templates/";

	@Override
	public Object findTemplateSource(String name) throws IOException {
		URL url = CuteTemplateLoader.class.getResource(TEMPLATE_PATH + name);
		return new File(url.getFile());
	}

	@Override
	public long getLastModified(Object templateSource) {
		File file = (File) templateSource;
		return file.lastModified();
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(new BufferedInputStream(new FileInputStream((File)templateSource)), Charset.forName(encoding));
		return inputStreamReader;
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {

	}

}
