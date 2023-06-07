import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;


public class Node {
	//Ogni nodo dell'albero è fatto cosi
	private String id;
	private String NomeCatalogo;
	private String IdPadre;
	private int NumFigli;

	public Node() {}

	//Ora scrivo getter e setter
	public String getID()
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

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(id);
		buffer.append(" ");
		buffer.append(NomeCatalogo);
		return buffer.toString();
	}


	/*
	public List<Node> ExtractSubTree(Node n, List<Node> AllNodes) {
	    List<Node> result = new ArrayList<Node>();
	    for (Node node : AllNodes) {
	        if (node.getIdPadre() != null && node.getIdPadre().equals(n.getID())) {
	            result.add(node);
	            result.addAll(ExtractSubTree(node, AllNodes));
	        }
	    }
	    return result;
	}


	//ora mi serve un metodo che copia a partire da un determinato nodo destinazione un intero sottoAlbero.
	public static void CopiaSottoAlbero(List<Node> Subtree, String Destinazione) throws RuntimeException
	{
		if(Destinazione.NumFigli >= 9)
		{
			throw new RuntimeException("Non c'è posto nel nodo destinazione");
		}
		Subtree.get(0).IdPadre = Destinazione;
		CambiaIdSottoAlbero(Subtree, Destinazione);
	}

	public static void CambiaIdSottoAlbero(List<Node> ListaDiNodi, String Destinazione)
	{
		ListaDiNodi.get(0).setID(Integer.toString((Integer.parseInt(Destinazione)*10 + Destinazione)));

		ListaDiNodi.get(0).setIdPadre(Destinazione.getID());
		for(int i = 1; i < ListaDiNodi.size(); i++)
		{
			for(int j = i + 1; j < ListaDiNodi.get(i).NumFigli + i; j++)
			{
				ListaDiNodi.get(j).setID(Integer.toString((Integer.parseInt(ListaDiNodi.get(i).getID())/10)+j));
				ListaDiNodi.get(j).setIdPadre(ListaDiNodi.get(i).getID());
			}
		}
	}

	//funzione che inserisce un figlio solo come figlio di un altro se c'è posto.
	public void InserisciFiglio(String NmCatalogo, Node father, List<Node> AllNodes)
	{
		if(father.NumFigli < 9){
			Node nuovo = new Node();
			nuovo.setIdPadre(father.getID());
			nuovo.setName(NmCatalogo);
			nuovo.NumFigli = 0;
			nuovo.setID(Integer.toString(Integer.parseInt(father.getID())*10 + father.NumFigli +1));
			AllNodes.add(nuovo);
		}

	}
	*/
}
