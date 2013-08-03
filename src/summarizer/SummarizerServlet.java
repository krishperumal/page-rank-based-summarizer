package summarizer;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

public class SummarizerServlet extends HttpServlet {
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
      String text=req.getParameter("input_text");
      int p=0;
      if(!(req.getParameter("percentage")).equals("")) {
        p=(int)Integer.parseInt(req.getParameter("percentage"));
      }
      int percentage;
      if(p<0)
	percentage=0;
      else if(p>99)
	percentage=99;
      else
	percentage=p;
      Summarizer summarizer=new Summarizer();
      String summary=summarizer.getHighlightedSummary(text,percentage);
      req.setAttribute("input",text);
      req.setAttribute("percentage",percentage);
      req.setAttribute("summary",summary);
      RequestDispatcher view =req.getRequestDispatcher("summarizer.jsp");
      view.forward(req,resp);
  }
}
