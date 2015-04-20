package io.sidd;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Servlet implementation class BusTiming
 */
@WebServlet("/BusTiming")
public class BusTiming extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BusTiming() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();

        String webhookSecret = "69G7HE66NKD3HPRPT9CHCZ6PU2GM6XKC";
    
        if (!webhookSecret.equals(request.getParameter("secret")))
        {
        	
            response.setStatus(403);
            out.write("Invalid webhook secret");
        }
        else if ("incoming_message".equals(request.getParameter("event")))
        {
            String content = request.getParameter("content");
            String fromNumber = request.getParameter("from_number");
            String phoneId = request.getParameter("phone_id");
            
            System.out.println("Received following message: \n <=========>\n"
            		+ content + "\n <=========>\n From: " + fromNumber + 
            		"\nPhone ID: " + phoneId);
            
            response.setContentType("application/json");
            
            try
            {
            	JSONArray messages = new JSONArray();
            	JSONObject message = new JSONObject();
            	String[] data = content.split(" ");
            	
            	if (!((data.length > 0) && data[0].equals("@bus"))) {
            		response.setStatus(403);
            		out.write("@bus keyword missing");
            		return;
            	}
            	
            	BusSgTimingFetcher tf = new BusSgTimingFetcher(data[1], data[2]);
            	message.put("content" , tf.timeToBus());
            	messages.add(message);
            	
            	System.out.println("Sending following response to " + fromNumber + " : \n"
            			+ tf.timeToBus());

                JSONObject json = new JSONObject();
                json.put("messages", messages);
                
                json.writeJSONString(out);
            }
            catch (IOException ex)
            {
                throw new ServletException(ex);
            }        
        }      
	}

}
