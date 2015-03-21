package demo.service;

import java.util.Map;

import com.logicbus.backend.server.http.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

public class Helloworld extends AbstractServant{

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {
		m_welcome = sd.getProperties().GetValue("welcome", "Welcome to Logic Bus Server!");
	}

	@Override
	protected int onXml(MessageDoc msgDoc, Context ctx) throws Exception {
        if (ctx instanceof HttpContext) {
            HttpContext httpCtx = (HttpContext) ctx;
            System.out.println("x-forwarded-for:" + httpCtx.getRequest().getHeader("X-Forwarded-For"));
            System.out.println("ClientIp:" + ctx.getClientIp());
        }
		XMLMessage msg = (XMLMessage)msgDoc.asMessage(XMLMessage.class);
		Element root = msg.getRoot();
		Document doc = root.getOwnerDocument();
		root.appendChild(doc.createTextNode( m_welcome));

        if (ctx instanceof HttpContext) {
            HttpContext httpCtx = (HttpContext) ctx;
            root.appendChild(doc.createTextNode("x-forwarded-for:" + httpCtx.getRequest().getHeader("X-Forwarded-For")));
            root.appendChild(doc.createTextNode("ClientIp:" + ctx.getClientIp()));
        }
		metricsIncr("helloworld", new Long(1L));
		return 0;
	}

	@Override
	protected int onJson(MessageDoc msgDoc, Context ctx) throws Exception {
        if (ctx instanceof HttpContext) {
            HttpContext httpCtx = (HttpContext) ctx;
            System.out.println("x-forwarded-for:" + httpCtx.getRequest().getHeader("X-Forwarded-For"));
            System.out.println("ClientIp:" + ctx.getClientIp());
        }
		JsonMessage msg = (JsonMessage)msgDoc.asMessage(JsonMessage.class);
		Map<String,Object> root = msg.getRoot();

		root.put("welcome", m_welcome);
        if (ctx instanceof HttpContext) {
            HttpContext httpCtx = (HttpContext) ctx;
            root.put("x-forwarded-for:",httpCtx.getRequest().getHeader("X-Forwarded-For"));
            root.put("ClientIp:",ctx.getClientIp());
        }

		metricsIncr("helloworld", new Long(1L));
		return 0;
	}

	protected String m_welcome;
}
