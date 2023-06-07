package beans;

public class NodeBean {
	//Ogni nodo dell'albero è fatto cosi
	private String id;
	private String NomeCatalogo;
	private String IdPadre;
	private int numFigli;
	private boolean selected = false;

	public NodeBean(String id, String nomeCatalogo, String idPadre) {
		this.id = id;
		this.NomeCatalogo = nomeCatalogo;
		this.IdPadre = idPadre;
	}

	public boolean isSelected() {
		return selected;
	}

	public void select(boolean select) {
		this.selected = select;
	}

	//Ora scrivo getter e setter
	public String getId()
	{
		return this.id;
	}

	public String getName()
	{
		return this.NomeCatalogo;
	}

	public void setID (String newID)
	{
		this.id = newID;
	}

	public void setName (String newName)
	{
		this.NomeCatalogo = newName;
	}

	public String getIdPadre()
	{
		return this.IdPadre;
	}

	public void setIdPadre(String NewName)
	{
		this.IdPadre = NewName;
	}

	public int getNumFigli() {
		return this.numFigli;
	}

	public void setlumFigli(int figli) {
		this.numFigli = figli;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(id);
		buffer.append(" ");
		buffer.append(NomeCatalogo);
		return buffer.toString();
	}
}
