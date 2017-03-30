package com.andrius.project.Chapter13_3.others;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;

public class GZIPServletOutputStream extends ServletOutputStream{
  
	// Keep a reference to the raw GZIP stream. This instance variable is package-private
	// to allow the compression response wrapper access to this variable:
	GZIPOutputStream internalGzipOS;
	
	// Decorator constructor:
	GZIPServletOutputStream(ServletOutputStream sos) throws IOException{
		this.internalGzipOS = new GZIPOutputStream(sos);
	}

	@Override
	public void write(int param) throws IOException {
		// This method implements the compression decoration by delegating the write() call
		// to the GZIP compression stream, which is wrapping the original ServletOutputStream
		// , (which in turn is ultimately wrapping the TCP network output stream to the client):
		internalGzipOS.write(param);
	}
}
