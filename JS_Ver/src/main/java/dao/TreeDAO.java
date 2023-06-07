package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.spi.NumberFormatProvider;
import java.util.ArrayList;
import java.util.List;

import beans.*;

public class TreeDAO {

	private Connection conn;

	public TreeDAO(Connection connection) {
		this.conn = connection;
	}

	public List<NodeBean> getSottoAlbero(String id) throws SQLException {
		List<NodeBean> sottoAlbero = new ArrayList<>();
		String query = "SELECT c.id, c.catalog_name, cf.parent_id FROM catalogo as c JOIN catalogo_figli as cf ON c.id = cf.node_id WHERE c.id LIKE ? OR cf.parent_id = ? ORDER BY cf.node_id";
		PreparedStatement pstmt = conn.prepareStatement(query);
		pstmt.setString(1, id + "%");
		pstmt.setString(2, id);
		ResultSet result = pstmt.executeQuery();

		while (result.next()) {
			NodeBean n = new NodeBean(result.getString(1), result.getString(2), result.getString(3));
			System.err.println(n.toString());
			sottoAlbero.add(n);
		}
		return sottoAlbero;
	}

	public List<NodeBean> getAlberoCompleto() throws SQLException {
		List<NodeBean> AlberoCompleto = new ArrayList<NodeBean>();
		PreparedStatement pstatement = conn.prepareStatement("SELECT c.id, c.catalog_name, cf.parent_id FROM catalogo as c JOIN catalogo_figli as cf ON c.id = cf.node_id ORDER BY cf.node_id");
		ResultSet result = pstatement.executeQuery();
		System.err.println(result.toString());
		while (result.next()) {
			NodeBean n = new NodeBean(result.getString(1), result.getString(2), result.getString(3));
			System.err.println(n.toString());
			if(n.getIdPadre() != null)
				AlberoCompleto.add(n);
		}
		return AlberoCompleto;
	}

	public void inserisciUnFiglio(String id, String catalog_name, String parent_id)
			throws SQLException {
		String query = "INSERT into catalogo(id, catalog_name) VALUES(?,?)";
		System.err.println("Inserisco " + id + " - " + catalog_name);
		PreparedStatement pstatement = conn.prepareStatement(query);
		pstatement.setString(1, id);
		pstatement.setString(2, catalog_name);
		pstatement.executeUpdate();

		System.err.println("Collego " + id + " a " + parent_id);
		query = "INSERT into catalogo_figli(node_id, parent_id) VALUES(?,?)";
		pstatement = conn.prepareStatement(query);
		pstatement.setString(1, id);
		pstatement.setString(2, parent_id);
		pstatement.executeUpdate();
	}

	public void copySubTree(List<NodeBean> sottoAlbero, String old_root, int numCount, String new_root) throws SQLException {
		// String query = "INSERT into catalogo (id, catalog_name) VALUES (?,?)";
		// PreparedStatement pstatement;
		String id;
		String categoria;
		String idPadre;

		if(sottoAlbero == null) return;

		String root = new_root + String.valueOf(numCount + 1);
		inserisciUnFiglio(root, sottoAlbero.get(0).getName(), new_root);

		for (int i = 1; i < sottoAlbero.size(); i++) {
			var nodo = sottoAlbero.get(i);
			System.err.println("NODO " + nodo.getId() + " --- " + nodo.getIdPadre() + " --- " + nodo.getName());

			id = root + nodo.getId().substring(old_root.length());
			idPadre = root + nodo.getIdPadre().substring(old_root.length());
			categoria = nodo.getName();

			System.err.println("SOS " + id + " --- " + idPadre + " --- " + categoria);
			inserisciUnFiglio(id, categoria, idPadre);

			// pstatement = conn.prepareStatement(query);
			// pstatement.setString(1, id);
			// pstatement.setString(2, categoria);
			// pstatement.executeUpdate();

			// query = "INSERT into catalogo_figli(node_id, parent_id) VALUES (?,?)";
			// pstatement = conn.prepareStatement(query);
			// pstatement.setString(1, id);
			// pstatement.setString(2, idPadre);
			// pstatement.executeUpdate();
		}
	}

	public void cancellaRamo(NodeBean root) throws SQLException {
		String query = "DELETE FROM catalogo WHERE id LIKE ?";

		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, root.getId());
		stmt.executeUpdate();

		query = "DELETE FROM catalogo_figli WHERE parent_id LIKE ?";
		stmt = conn.prepareStatement(query);
		stmt.setString(1, root.getId());
		stmt.executeUpdate();
	}

	public void modificaNome(NodeBean n, String catalog_name) throws SQLException {
		String query = "UPDATE catalog_name SET catalogo = ? WHERE id = ?";
		PreparedStatement pstatement = conn.prepareStatement(query);
		pstatement.setString(2, n.getId());
		pstatement.setString(1, catalog_name);
		pstatement.executeUpdate();
	}

	public int getNumChildren(String newParent) throws SQLException {
		String pstmt1 = "SELECT COUNT(node_id) FROM catalogo_figli WHERE parent_id = ?"; // new_parent
		PreparedStatement pstmt = conn.prepareStatement(pstmt1);
		pstmt.setString(1, newParent);
		ResultSet res = pstmt.executeQuery();
		if(res.next()) {
			System.err.println("Children: " + res.getInt(1));
			return res.getInt(1);
		}

		return 9999;
	}

	public void setupCatalogTable() throws SQLException {
		String query = "CREATE TABLE catalogo " +
				"(id VARCHAR(255) NOT NULL, " +
				" catalog_name VARCHAR(255) NOT NULL, " +
				" PRIMARY KEY (id))";
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(query);

		query = "CREATE TABLE catalogo_figli " +
				"(id INT AUTO_INCREMENT NOT NULL,"+
				" node_id VARCHAR(255) NOT NULL, " +
				" parent_id VARCHAR(255) NOT NULL, " +
				" PRIMARY KEY (id), " +
				" FOREIGN KEY (parent_id) REFERENCES catalogo(id) ON UPDATE CASCADE ON DELETE NO ACTION)";
		stmt = conn.createStatement();
		stmt.executeUpdate(query);
	}

	// DROP and RECREATE ?
	public void initTree() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("INSERT INTO catalogo(id, catalog_name) VALUES(\"\", \"ROOT\")");
		stmt.executeUpdate("INSERT INTO catalogo(id, catalog_name) VALUES(\"1\", \"Materiali Solidi\")");
		stmt.executeUpdate("INSERT INTO catalogo(id, catalog_name) VALUES(\"11\", \"Materiali inerti\")");
		stmt.executeUpdate("INSERT INTO catalogo(id, catalog_name) VALUES(\"111\", \"Inerti da edilizia\")");
		stmt.executeUpdate("INSERT INTO catalogo(id, catalog_name) VALUES(\"1111\", \"Amianto\")");
		stmt.executeUpdate("INSERT INTO catalogo(id, catalog_name) VALUES(\"11111\", \"Amianto in lastre\")");
		stmt.executeUpdate("INSERT INTO catalogo(id, catalog_name) VALUES(\"11112\", \"Amianto in frammenti\")");
		stmt.executeUpdate("INSERT INTO catalogo(id, catalog_name) VALUES(\"1112\", \"Materiali cementizi\")");
		stmt.executeUpdate("INSERT INTO catalogo(id, catalog_name) VALUES(\"112\", \"Inerti ceramici\")");
		stmt.executeUpdate("INSERT INTO catalogo(id, catalog_name) VALUES(\"1121\", \"Piastrelle\")");
		stmt.executeUpdate("INSERT INTO catalogo(id, catalog_name) VALUES(\"1122\", \"Sanitari\")");
		stmt.executeUpdate("INSERT INTO catalogo(id, catalog_name) VALUES(\"12\", \"Materiali ferrosi\")");


		stmt.executeUpdate("INSERT INTO catalogo_figli(node_id, parent_id) VALUES(\"1\", \"\")");
		stmt.executeUpdate("INSERT INTO catalogo_figli(node_id, parent_id) VALUES(\"11\", \"1\")");
		stmt.executeUpdate("INSERT INTO catalogo_figli(node_id, parent_id) VALUES(\"111\", \"11\")");
		stmt.executeUpdate("INSERT INTO catalogo_figli(node_id, parent_id) VALUES(\"1111\", \"111\")");
		stmt.executeUpdate("INSERT INTO catalogo_figli(node_id, parent_id) VALUES(\"11111\", \"1111\")");
		stmt.executeUpdate("INSERT INTO catalogo_figli(node_id, parent_id) VALUES(\"11112\", \"1111\")");
		stmt.executeUpdate("INSERT INTO catalogo_figli(node_id, parent_id) VALUES(\"1112\", \"111\")");
		stmt.executeUpdate("INSERT INTO catalogo_figli(node_id, parent_id) VALUES(\"112\", \"11\")");
		stmt.executeUpdate("INSERT INTO catalogo_figli(node_id, parent_id) VALUES(\"1121\", \"112\")");
		stmt.executeUpdate("INSERT INTO catalogo_figli(node_id, parent_id) VALUES(\"1122\", \"112\")");
		stmt.executeUpdate("INSERT INTO catalogo_figli(node_id, parent_id) VALUES(\"12\", \"1\")");
	}
}
