package utils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import beans.NodeBean;
import dao.TreeDAO;

public class Util {
	
    public static void send(HttpServletResponse res, JsonVars ... jsonData) throws IOException {
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        JsonObject message = new JsonObject();
        
        for(JsonVars item : jsonData) {
            if(!item.getKey().isEmpty()) {
                message.add(item.getKey(), JsonParser.parseString(item.getValue()));
            }
        }

        res.getWriter().println(message);
    }

    public static void sendError(HttpServletResponse res, int status, String error) throws IOException {
        res.setStatus(status);
        res.getWriter().println("Wrong username and/or password");
    }

    public static void recursionOnList(NodeBean root, List<NodeBean> tree) throws SQLException {
        if(root == null) return;
		
		var childs = tree.stream()
						.filter((x) -> x.getIdPadre().equals(root.getId()))
						.collect(Collectors.toList());
		root.setChildrens(childs);
		
        if(root.getChilds() == null || root.getChilds().size() == 0) return;
		
		tree.removeAll(childs);

        for(var x : root.getChilds()) {
            recursionOnList(x, tree);
        }
    }
    // public static void recursion(NodeBean root, TreeDAO service) throws SQLException {
    //     if(root == null) return;
		
	// 	var childs = service.getChildList(root);
	// 	root.setChildrens(childs);
			  
    //     if(root.getChilds() == null || root.getChilds().size() == 0) return;

    //     for(var x : root.getChilds()) {
    //         recursion(x, service);
    //     }
    // }
}
