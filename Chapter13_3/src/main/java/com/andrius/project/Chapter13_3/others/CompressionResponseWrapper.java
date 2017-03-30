package com.andrius.project.Chapter13_3.others;
import javax.servlet.http.HttpServletResponse;
// Servlets imports:
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.*;
// I/O imports:
import java.io.*;
import java.util.zip.GZIPOutputStream;

public class CompressionResponseWrapper  extends HttpServletResponseWrapper{

	private GZIPServletOutputStream servletGzipOS = null; // compressed output stream for the servlet response
	private PrintWriter pw = null; // The PrintWriter object to the compressed output stream
	private Object streamUsed = null;
	
	// The super constructor performs the Decorator responsibility of storing a reference
	// to the object being decorated in this case the HTTP response object:
	public CompressionResponseWrapper(HttpServletResponse response) {
		super(response);
	}
	// Ignore this method-the output will be compressed:
	public void setContentLength(int len){}
	
	// This decorator method, used by the filter, gives the compression filter a handle on the 
	// GZIP output stream so that the filter can "finish" and flush the GZIP stream:
	public GZIPOutputStream getGZIPOutputStream(){
		return this.servletGzipOS.internalGzipOS;
	}
	
	// Provide access to decorated servlet output stream:
	public ServletOutputStream getOutputStream()throws IOException{
		// Allow the servlet to access a servlet output stream, only if the servlet
		// has not already accessed the print writer:
		if((streamUsed != null) && (streamUsed != pw)){
			throw new IllegalStateException();
		}
		
		// Wrap the original servlet output stream with our compression servlet output stream:
		if(servletGzipOS == null){
			servletGzipOS = new GZIPServletOutputStream(getResponse().getOutputStream());
			streamUsed = servletGzipOS;
		}
		return servletGzipOS;
	}
	
	// Provide access to decorated print writer:
	public PrintWriter getWriter()throws IOException{
		// Allow the servlet to access a print writer, only if the servlet has not already
		// accessed the servlet output stream:
		if((streamUsed != null) && (streamUsed != servletGzipOS)){
			throw new IllegalStateException();
		}
		
		if (pw == null){
			// To make a print writer, we have to first wrap the servlet output stream
			servletGzipOS = new GZIPServletOutputStream(getResponse().getOutputStream());
			// and then wrap the compression servlet output stream in two additional
			// output stream decorators: OutputStreamWriter which converts characters
			// into bytes, and then a PrintWriter on top of the OutputStreamWriter object:
			OutputStreamWriter osw = new OutputStreamWriter(servletGzipOS,
					getResponse().getCharacterEncoding());
			
			pw = new PrintWriter(osw);
			streamUsed = pw;
		}
		return pw;
	}
}
