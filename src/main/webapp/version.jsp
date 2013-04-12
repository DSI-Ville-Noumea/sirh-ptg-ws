<%@page import="java.net.InetAddress"%>
sirh.ptg.ws.version=${version}<br/>
sirh.ptg.ws.hostaddress=<%=InetAddress.getLocalHost().getHostAddress() %><br/>
sirh.ptg.ws.canonicalhostname=<%=InetAddress.getLocalHost().getCanonicalHostName() %><br/>
sirh.ptg.ws.hostname=<%=InetAddress.getLocalHost().getHostName() %><br/>
sirh.ptg.ws.tomcat.version=<%= application.getServerInfo() %><br/>
sirh.ptg.ws.tomcat.catalina_base=<%= System.getProperty("catalina.base") %><br/>
