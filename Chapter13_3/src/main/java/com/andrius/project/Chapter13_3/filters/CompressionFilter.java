package com.andrius.project.Chapter13_3.filters;
import javax.servlet.*;
import javax.servlet.http.*;

import com.andrius.project.Chapter13_3.others.CompressionResponseWrapper;

import java.io.*;
import java.util.zip.GZIPOutputStream;

// The heart of this filter wraps the response object with Decorator that wraps the 
// output stream with a compression I/O stream. Compression of the output stream is 
// performed if and only if the client icludes an Accept_Encoding header (specifically, for gzip)
public class CompressionFilter implements Filter {
	private ServletContext ctx;
	private FilterConfig cfg;
	
	// the init method saves the config object and a quick reference to the servlet
	// context object ( for logging purpose):
	public void init(FilterConfig cfg)throws ServletException{
		this.cfg = cfg;
		ctx = cfg.getServletContext();
		ctx.log(cfg.getFilterName() + " initialized.");
	}
	
	public void doFilter(ServletRequest req, ServletResponse res, 
			FilterChain chain)throws ServletException, IOException{
		// first we need http Request and Response:
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		
		// Does a client accept GZIP compression?:
		String valid_encodings = request.getHeader("Accept-Encoding");
		
		// If so, wrap the response object with a compression wrapper:
		if(valid_encodings.indexOf("gzip") > -1){
			// CompressionResponseWrapper is a custom class of HttpServletResponse:
		  CompressionResponseWrapper wrappedResp = new CompressionResponseWrapper(response);
		  wrappedResp.setHeader("Content-Encoding", "gzip"); // declare that the response content
		  // is being GZIP encoded, you can tes and debug comment this line!
		  
		  chain.doFilter(request, wrappedResp); // Chain to the next component
		  
		  GZIPOutputStream gzos = wrappedResp.getGZIPOutputStream();
		  // A GZIP compression stream must be finished, which also flushes the GZIP stream buffer,
		  // and send all of its data to the original response stream. 
		  gzos.finish();
		  
		  // The container handles the rest of the work
		  
		  ctx.log(cfg.getFilterName() + " : finished the request.");
		}else{
			ctx.log(cfg.getFilterName() + " : no encoding performed.");
			chain.doFilter(request, response);
		}
	}
	
	public void destroy(){
		// nulling out my instance variables:
		cfg = null;
		ctx = null;
	}
	
}
