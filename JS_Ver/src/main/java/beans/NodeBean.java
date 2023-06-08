package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NodeBean implements Serializable {
	private static final long serialVersionUID = 1L;

	//Ogni nodo dell'albero è fatto cosi
	private String id;
	private String nomeCatalogo;
	private String idPadre;
	private List<NodeBean> subCatalogs;
	private boolean selected = false;

	public NodeBean(String id, String nomeCatalogo, String idPadre) {
		this.id = id;
		this.nomeCatalogo = nomeCatalogo;
		this.idPadre = idPadre;
	}

	public void setChildrens(List<NodeBean> nodes) {
		this.subCatalogs = nodes;
	}

	public void addChild(NodeBean node) {
		this.subCatalogs.add(node);
	}

	public List<NodeBean> getChilds() {
		return this.subCatalogs;
	}

	public boolean isSelected() {
		return selected;
	}

	public void select(boolean select) {
		this.selected = select;
	}

	public String getId()
	{
		return this.id;
	}

	public String getName()
	{
		return this.nomeCatalogo;
	}

	public void setID (String newID)
	{
		this.id = newID;
	}

	public void setName (String newName)
	{
		this.nomeCatalogo = newName;
	}

	public String getIdPadre()
	{
		return this.idPadre;
	}

	public void setIdPadre(String NewName)
	{
		this.idPadre = NewName;
	}

	public int getNumFigli() {
		return this.subCatalogs.size();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(id);
		buffer.append(" ");
		buffer.append(nomeCatalogo);
		return buffer.toString();
	}
}
