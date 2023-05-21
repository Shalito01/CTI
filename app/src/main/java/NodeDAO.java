import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.BOM.beans.Node;

public class NodeDAO {
	private Connection connection;

	public NodeDAO() {
		// TODO Auto-generated constructor stub
	}

	public NodeDAO(Connection con) {
		this.connection = con;
	}

	public List<Node> findAllProducts() throws SQLException {
		List<Node> Nodes = new ArrayList<Node>();
		try (PreparedStatement pstatement = connection.prepareStatement("SELECT * FROM bom.product");) {
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Node BP = new Node();
					BP.setId(result.getInt("id"));
					BP.setName(result.getString("name"));
					BP.setUnitcost(result.getInt("unitcost"));
					BP.setDescription(result.getString("description"));
					Nodes.add(BP);
				}
			}
		}
		return Nodes;
	}

	public List<Node> findTopProductsAndSubtrees() throws SQLException {
		List<Node> Nodes = new ArrayList<Node>();
		try (PreparedStatement pstatement = connection
				.prepareStatement("SELECT * FROM bom.product WHERE id NOT IN (select child FROM bom.subparts)");) {
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Node BP = new Node();
					BP.setId(result.getInt("id"));
					BP.setName(result.getString("name"));
					BP.setUnitcost(result.getInt("unitcost"));
					BP.setDescription(result.getString("description"));
					BP.setIsTop(true);
					Nodes.add(BP);
				}
				for (Node p : Nodes) {
					findSubparts(p);
				}
			}
		}
		return Nodes;
	}

	public void findSubparts(Node p) throws SQLException {
		Node BP = null;
		try (PreparedStatement pstatement = connection.prepareStatement(
				"SELECT P.id, P.name, P.description, P.unitcost, S.quantity FROM bom.subparts S JOIN bom.product P on P.id = S.child WHERE S.father = ?");) {
			pstatement.setInt(1, p.getId());
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					BP = new Node();
					BP.setId(result.getInt("id"));
					BP.setName(result.getString("name"));
					BP.setUnitcost(result.getInt("unitcost"));
					BP.setDescription(result.getString("description"));
					Integer q = Integer.valueOf(result.getInt("quantity"));
					findSubparts(BP);
					p.addSubpart(BP, q);
				}
			}
		}

	}

	public void createProduct(String name, String descr, int cost) throws SQLException {
		String query = "INSERT into bom.product (name, unitcost, description) VALUES(?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, name);
			pstatement.setInt(2, cost);
			pstatement.setString(3, descr);
			pstatement.executeUpdate();
		}
	}

	public void updateProduct(int pid, int cost) throws SQLException {
		String query = "UPDATE bom.product SET unitcost = ? WHERE id = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, cost);
			pstatement.setInt(2, pid);
			pstatement.executeUpdate();
		}
	}

	public void createLink(int fatherId, int childId, int qty) throws SQLException {
		String query = "INSERT into bom.subparts(father, child, quantity) VALUES(?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, fatherId);
			pstatement.setInt(2, childId);
			pstatement.setInt(3, qty);
			pstatement.executeUpdate();
		}
	}

	public boolean linkExists(int fatherId, int childId)  throws SQLException {
		boolean linkExists = false;
		try (PreparedStatement pstatement = connection.prepareStatement("SELECT * FROM bom.subparts where father = ? and child = ?");) {
			pstatement.setInt(1, fatherId);
			pstatement.setInt(2, childId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					linkExists = true;
				}
			}
		}
		return linkExists;
	}

	public void deleteLink(int fatherId, int childId) throws SQLException {
		String query = "DELETE FROM bom.subparts WHERE father = ? and child = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, fatherId);
			pstatement.setInt(2, childId);
			pstatement.executeUpdate();
		}
	}



	public void deleteProduct(int pId) throws SQLException {
		connection.setAutoCommit(false);

		String query = "DELETE FROM bom.subparts WHERE father = ? OR child = ?";
		PreparedStatement pstatement = null;

		String query2 = "DELETE FROM bom.product WHERE id = ?";
		PreparedStatement pstatement2 = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, pId);
			pstatement.setInt(2, pId);
			pstatement.executeUpdate();

			pstatement2 = connection.prepareStatement(query2);
			pstatement2.setInt(1, pId);
			pstatement2.executeUpdate();

			connection.commit();

		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			connection.setAutoCommit(true);
			if (pstatement != null) {
				try {
					pstatement.close();
				} catch (Exception e) {
					throw e;
				}
			}
			if (pstatement2 != null) {
				try {
					pstatement2.close();
				} catch (Exception e) {
					throw e;
				}
			}
		}
	}


	public boolean cyclicLinkExists(int p1, int p2) throws SQLException {
		//check if product p2 is an ancestor of product p1 by using RECURSIVE
		boolean exists = false;
		String query = "with recursive cte (father, child) as (select father, child from bom.subparts where child = ? union all select p.father, p.child from bom.subparts p inner join cte on p.child = cte.father) select  * from cte where father = ?;";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, p1);
			pstatement.setInt(2, p2);
			System.out.println(pstatement.toString());
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					exists = true;
				}
			}
		}
		return exists;
	}



	// THE FOLLOWING FUNCTIONS ARE NOT USED

	/*public void deleteSubtree(int pid) throws SQLException {
		List<Node> children = findChildren(pid);
		for (Node c : children) {
			deleteSubtree(c.getId());
		}
		deleteProduct(pid);
	}

	public void changeLinkQuantity(int fatherId, int childId, int qty) throws SQLException {
		// Function not used
		String query = "UPDATE bom.subparts SET quantity = ? WHERE father = ? AND child = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, qty);
			pstatement.setInt(2, fatherId);
			pstatement.setInt(2, childId);
			pstatement.executeUpdate();
		}

	}

	public List<Node> findChildren(int pid) throws SQLException {
		List<Node> children = new ArrayList<Node>();
		try (PreparedStatement pstatement = connection
				.prepareStatement("SELECT child FROM bom.subparts WHERE father = ?");) {
			pstatement.setInt(1, pid);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Node BP = new Node();
					BP.setId(result.getInt("id"));
					BP.setName(result.getString("name"));
					BP.setDescription(result.getString("description"));
					BP.setUnitcost(result.getInt("cost"));
					children.add(BP);
				}
			}
		}
		return children;
	}*/

}
